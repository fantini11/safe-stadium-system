package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Usuario;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClarifaiService {

    private static final String USER_ID = "clarifai";
    private static final String APP_ID = "main";
    // Modelo de embedding geral (funciona para rostos)
    private static final String MODEL_ID = "general-image-embedding";
    private static final String API_URL = "https://api.clarifai.com/v2/users/" + USER_ID + "/apps/" + APP_ID + "/models/" + MODEL_ID + "/outputs";
    private static final String API_KEY = resolveApiKey();

    private final OkHttpClient client;
    private final Gson gson;

    public ClarifaiService() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    // Limiar de similaridade (0.0 - 1.0)
    private static final double SIMILARITY_THRESHOLD = 0.65;

    public Usuario identificarSuspeito(String urlImagemSuspeito, List<Usuario> usuarios) {
        if (API_KEY == null || API_KEY.isBlank()) {
            System.err.println("[Clarifai] API key ausente. Defina CLARIFAI_API_KEY no ambiente ou .env.");
            return null;
        }

        try {
            System.out.println("\n========================================");
            System.out.println("[Clarifai] Iniciando identificacao facial");
            System.out.println("========================================");
            System.out.println("[Clarifai] Total de usuarios no banco: " + usuarios.size());

            long usuariosComFoto = usuarios.stream()
                    .filter(u -> u.getFoto() != null && !u.getFoto().isEmpty())
                    .count();
            System.out.println("[Clarifai] Usuarios com foto cadastrada: " + usuariosComFoto);

            // 1. Detectar rostos e gerar embeddings do suspeito
            System.out.println("[Clarifai] Detectando rostos na imagem do suspeito...");
            List<double[]> facesSuspeito = detectFaces(urlImagemSuspeito);

            if (facesSuspeito == null || facesSuspeito.isEmpty()) {
                System.err.println("[Clarifai] Nenhum rosto detectado na imagem do suspeito");
                return null;
            }

            System.out.println("[Clarifai] " + facesSuspeito.size() + " rosto(s) detectado(s) na imagem do suspeito");
            double[] vetorSuspeito = facesSuspeito.get(0);

            Usuario melhorCandidato = null;
            double maiorSimilaridade = -1.0;
            int comparacoes = 0;

            // 2. Comparar com cada usuario do banco
            System.out.println("[Clarifai] Comparando com usuarios do banco...");
            for (Usuario u : usuarios) {
                if (u.getFoto() != null && !u.getFoto().isEmpty()) {
                    try {
                        comparacoes++;
                        System.out.println("  [" + comparacoes + "/" + usuariosComFoto + "] Analisando: " + u.getNome() + " (ID: " + u.getId() + ")");

                        List<double[]> facesUsuario = detectFaces(u.getFoto());

                        if (facesUsuario != null && !facesUsuario.isEmpty()) {
                            double[] vetorUsuario = facesUsuario.get(0);
                            double similaridade = cosineSimilarity(vetorSuspeito, vetorUsuario);
                            System.out.println("      Similaridade: " + String.format("%.4f (%.2f%%)", similaridade, similaridade * 100));

                            if (similaridade > maiorSimilaridade) {
                                maiorSimilaridade = similaridade;
                                if (similaridade > SIMILARITY_THRESHOLD) {
                                    melhorCandidato = u;
                                    System.out.println("      [OK] Novo melhor candidato (acima do limiar)");
                                } else {
                                    System.out.println("      [INFO] Melhor ate agora, mas abaixo do limiar");
                                }
                            }
                        } else {
                            System.out.println("      [WARN] Nenhum rosto detectado na foto deste usuario");
                        }
                    } catch (Exception e) {
                        System.err.println("      [ERRO] Falha ao processar usuario " + u.getId() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("\n========================================");
            if (melhorCandidato != null) {
                System.out.println("[SUCESSO] Suspeito identificado!");
                System.out.println("   Nome: " + melhorCandidato.getNome());
                System.out.println("   CPF: " + melhorCandidato.getCpf());
                System.out.println("   Similaridade: " + String.format("%.2f%%", maiorSimilaridade * 100));
            } else {
                if (maiorSimilaridade > 0) {
                    System.out.println("[INFO] Nenhum suspeito identificado com confianca > 65%");
                    System.out.println("   Maior similaridade encontrada: " + String.format("%.2f%%", maiorSimilaridade * 100));
                } else {
                    System.out.println("[INFO] Nenhum suspeito identificado");
                }
                if (comparacoes == 0) {
                    System.out.println("   [WARN] Nenhum usuario com foto foi encontrado no banco");
                }
            }
            System.out.println("========================================\n");

            return melhorCandidato;

        } catch (Exception e) {
            System.err.println("[Clarifai] Erro critico na identificacao: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Detecta rostos na imagem e retorna uma lista de embeddings (vetores) para
     * cada rosto detectado.
     */
    private List<double[]> detectFaces(String imageUrl) {
        try {
            JsonObject jsonBody = new JsonObject();
            JsonArray inputs = new JsonArray();
            JsonObject input = new JsonObject();
            JsonObject data = new JsonObject();
            JsonObject image = new JsonObject();

            // Preparar imagem (URL ou Base64)
            if (imageUrl.startsWith("http")) {
                image.addProperty("url", imageUrl);
            } else {
                String base64Clean = imageUrl;
                if (imageUrl.contains(",")) {
                    base64Clean = imageUrl.split(",")[1];
                }
                base64Clean = base64Clean.replaceAll("\\s+", "");
                image.addProperty("base64", base64Clean);
            }

            data.add("image", image);
            input.add("data", data);
            inputs.add(input);
            jsonBody.add("inputs", inputs);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Key " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "sem corpo";
                    System.err.println("[Clarifai] Erro na API Clarifai: " + response.code());
                    System.err.println("[Clarifai] Resposta: " + errorBody);
                    return null;
                }

                String responseBody = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                List<double[]> embeddings = new ArrayList<>();

                try {
                    JsonArray outputs = jsonResponse.getAsJsonArray("outputs");
                    if (outputs == null || outputs.size() == 0) {
                        System.err.println("[Clarifai] Resposta sem 'outputs'");
                        return null;
                    }

                    JsonObject outputData = outputs.get(0).getAsJsonObject().getAsJsonObject("data");

                    if (outputData == null) {
                        System.err.println("[Clarifai] Resposta sem 'data'");
                        return null;
                    }

                    if (outputData.has("embeddings")) {
                        JsonArray embeddingsArray = outputData.getAsJsonArray("embeddings");
                        if (embeddingsArray.size() > 0) {
                            JsonArray vector = embeddingsArray.get(0).getAsJsonObject().getAsJsonArray("vector");

                            double[] embedding = new double[vector.size()];
                            for (int j = 0; j < vector.size(); j++) {
                                embedding[j] = vector.get(j).getAsDouble();
                            }
                            embeddings.add(embedding);
                            System.out.println("[Clarifai] Embedding extraido (dimensao: " + embedding.length + ")");
                        }
                    } else if (outputData.has("regions")) {
                        JsonArray regions = outputData.getAsJsonArray("regions");
                        System.out.println("[Clarifai] " + regions.size() + " regiao(oes) detectada(s)");

                        for (int i = 0; i < regions.size(); i++) {
                            JsonObject region = regions.get(i).getAsJsonObject();
                            JsonObject regionData = region.getAsJsonObject("data");

                            if (regionData.has("embeddings")) {
                                JsonArray embeddingsArray = regionData.getAsJsonArray("embeddings");
                                if (embeddingsArray.size() > 0) {
                                    JsonArray vector = embeddingsArray.get(0).getAsJsonObject().getAsJsonArray("vector");

                                    double[] embedding = new double[vector.size()];
                                    for (int j = 0; j < vector.size(); j++) {
                                        embedding[j] = vector.get(j).getAsDouble();
                                    }
                                    embeddings.add(embedding);
                                    System.out.println("[Clarifai] Embedding " + (i + 1) + " extraido (dimensao: " + embedding.length + ")");
                                }
                            }
                        }
                    } else {
                        System.err.println("[Clarifai] Resposta sem 'embeddings' nem 'regions'");
                        return null;
                    }

                    return embeddings.isEmpty() ? null : embeddings;

                } catch (Exception e) {
                    System.err.println("[Clarifai] Erro ao fazer parse da resposta: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("[Clarifai] Erro na deteccao de faces: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static String resolveApiKey() {
        String fromEnv = System.getenv("CLARIFAI_API_KEY");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }

        String fromFile = EnvLoader.load().get("CLARIFAI_API_KEY");
        return fromFile != null ? fromFile.trim() : null;
    }

    /**
     * Loader simples de .env para reaproveitar em outras classes sem duplicar
     * codigo.
     */
    private static final class EnvLoader {

        private static Map<String, String> cached;

        static Map<String, String> load() {
            if (cached != null) {
                return cached;
            }
            cached = dao.ConnectionFactory.loadEnvFile();
            return cached;
        }
    }
}
