package app;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Admin;
import model.Evento;
import model.Incidente;
import model.Movimentacao;
import model.Usuario;
import service.AdminService;
import service.ClarifaiService;
import service.EventoService;
import service.IncidenteService;
import service.MovimentacaoService;
import service.UsuarioService;

public class Aplicacao {
    public static void main(String[] args) {
        port(8081);
        enableCORS();

        System.out.println("==============================================");
        System.out.println("  SafeStadium Backend Java + Supabase");
        System.out.println("  Servidor rodando em http://localhost:8081");
        System.out.println("==============================================");

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return new Date(sdf.parse(json.getAsString()).getTime());
                } catch (Exception e) {
                    return null;
                }
            })
            .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) ->
                src == null ? null : new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (json, typeOfT, context) -> {
                try {
                    return Time.valueOf(LocalTime.parse(json.getAsString()));
                } catch (Exception e) {
                    return null;
                }
            })
            .registerTypeAdapter(Time.class, (JsonSerializer<Time>) (src, typeOfSrc, context) ->
                src == null ? null : new JsonPrimitive(src.toLocalTime().toString()))
            .create();

        UsuarioService usuarioService = new UsuarioService();
        EventoService eventoService = new EventoService();
        AdminService adminService = new AdminService();
        MovimentacaoService movimentacaoService = new MovimentacaoService();
        IncidenteService incidenteService = new IncidenteService();
        ClarifaiService clarifaiService = new ClarifaiService();

        int migrated = adminService.migratePlaintextPasswords();
        if (migrated > 0) {
            System.out.println("[SECURITY] Senhas de admins migradas para BCrypt: " + migrated);
        }

        get("/health", (req, res) -> "OK");

        // Debug: listar usuarios com foto
        get("/debug/usuarios-com-foto", (req, res) -> {
            res.type("application/json");
            List<Usuario> todos = usuarioService.listar();
            long comFoto = todos.stream()
                .filter(u -> u.getFoto() != null && !u.getFoto().isEmpty())
                .count();

            System.out.println("[DEBUG] Usuarios no banco: total=" + todos.size() + " comFoto=" + comFoto);

            return gson.toJson(Map.of(
                "totalUsuarios", todos.size(),
                "usuariosComFoto", comFoto,
                "usuarios", todos.stream()
                    .map(u -> Map.of(
                        "id", u.getId(),
                        "nome", u.getNome(),
                        "temFoto", u.getFoto() != null && !u.getFoto().isEmpty(),
                        "tamanhoFoto", u.getFoto() != null ? u.getFoto().length() : 0
                    ))
                    .collect(Collectors.toList())
            ));
        });

        // Identificacao por IA
        post("/denuncias/identificar", (req, res) -> {
            res.type("application/json");
            try {
                JsonObject body = gson.fromJson(req.body(), JsonObject.class);
                String imagem = null;
                if (body != null) {
                    if (body.has("imagem")) imagem = body.get("imagem").getAsString();
                    else if (body.has("foto")) imagem = body.get("foto").getAsString();
                }

                if (imagem == null) {
                    res.status(400);
                    return gson.toJson(Map.of("error", "Imagem nao fornecida"));
                }

                List<Usuario> usuarios = usuarioService.listar();
                Usuario identificado = clarifaiService.identificarSuspeito(imagem, usuarios);

                if (identificado != null) {
                    return gson.toJson(identificado);
                } else {
                    res.status(404);
                    return gson.toJson(Map.of("message", "Nenhum suspeito identificado com alta confianca"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", "Erro interno na identificacao: " + e.getMessage()));
            }
        });

        // Usuarios/Torcedores
        get("/usuarios", (req, res) -> {
            res.type("application/json");
            return gson.toJson(usuarioService.listar());
        });

        get("/usuarios/:id", (req, res) -> {
            res.type("application/json");
            return gson.toJson(usuarioService.get(Integer.parseInt(req.params(":id"))));
        });

        get("/usuarios/cpf/:cpf", (req, res) -> {
            res.type("application/json");
            return gson.toJson(usuarioService.getByCpf(req.params(":cpf")));
        });

        post("/usuarios", (req, res) -> {
            res.type("application/json");
            Usuario u = gson.fromJson(req.body(), Usuario.class);
            usuarioService.insert(u);
            return gson.toJson(u);
        });

        put("/usuarios/:id", (req, res) -> {
            res.type("application/json");
            Usuario u = gson.fromJson(req.body(), Usuario.class);
            u.setId(Integer.parseInt(req.params(":id")));
            usuarioService.update(u);
            return gson.toJson(u);
        });

        delete("/usuarios/:id", (req, res) -> {
            res.type("application/json");
            boolean success = usuarioService.remove(Integer.parseInt(req.params(":id")));
            return gson.toJson(success);
        });

        // Eventos
        get("/eventos", (req, res) -> {
            res.type("application/json");
            return gson.toJson(eventoService.listar());
        });

        get("/eventos/:id", (req, res) -> {
            res.type("application/json");
            return gson.toJson(eventoService.get(Integer.parseInt(req.params(":id"))));
        });

        post("/eventos", (req, res) -> {
            res.type("application/json");
            Evento e = gson.fromJson(req.body(), Evento.class);
            eventoService.insert(e);
            return gson.toJson(e);
        });

        put("/eventos/:id", (req, res) -> {
            res.type("application/json");
            Evento e = gson.fromJson(req.body(), Evento.class);
            e.setId(Integer.parseInt(req.params(":id")));
            eventoService.update(e);
            return gson.toJson(e);
        });

        delete("/eventos/:id", (req, res) -> {
            res.type("application/json");
            boolean success = eventoService.remove(Integer.parseInt(req.params(":id")));
            return gson.toJson(success);
        });

        // Administradores
        get("/admins", (req, res) -> {
            res.type("application/json");
            // Nunca devolver senha
            return gson.toJson(adminService.listar().stream()
                .peek(a -> a.setSenha(null))
                .collect(Collectors.toList()));
        });

        get("/admins/:id", (req, res) -> {
            res.type("application/json");
            Admin admin = adminService.get(Integer.parseInt(req.params(":id")));
            if (admin != null) admin.setSenha(null);
            return gson.toJson(admin);
        });

        post("/admins/login", (req, res) -> {
            res.type("application/json");
            Admin a = gson.fromJson(req.body(), Admin.class);
            Admin admin = adminService.login(a.getLogin(), a.getSenha());
            if (admin != null) {
                admin.setSenha(null);
                return gson.toJson(admin);
            } else {
                res.status(401);
                return gson.toJson(Map.of("error", "Credenciais invalidas"));
            }
        });

        post("/admins", (req, res) -> {
            res.type("application/json");
            Admin a = gson.fromJson(req.body(), Admin.class);
            adminService.insert(a);
            a.setSenha(null);
            return gson.toJson(a);
        });

        // Movimentacoes (Entrada/Saida)
        get("/movimentacoes", (req, res) -> {
            res.type("application/json");
            return gson.toJson(movimentacaoService.listar());
        });

        get("/movimentacoes/:id", (req, res) -> {
            res.type("application/json");
            return gson.toJson(movimentacaoService.get(Integer.parseInt(req.params(":id"))));
        });

        get("/movimentacoes/cpf/:cpf", (req, res) -> {
            res.type("application/json");
            return gson.toJson(movimentacaoService.listarPorCpf(req.params(":cpf")));
        });

        post("/movimentacoes", (req, res) -> {
            res.type("application/json");
            Movimentacao m = gson.fromJson(req.body(), Movimentacao.class);
            if (m.getTimestamp() == null) {
                m.setTimestamp(new Timestamp(System.currentTimeMillis()));
            }
            movimentacaoService.insert(m);
            return gson.toJson(m);
        });

        put("/movimentacoes/:id", (req, res) -> {
            res.type("application/json");
            Movimentacao m = gson.fromJson(req.body(), Movimentacao.class);
            m.setId(Integer.parseInt(req.params(":id")));
            movimentacaoService.update(m);
            return gson.toJson(m);
        });

        delete("/movimentacoes/:id", (req, res) -> {
            res.type("application/json");
            boolean success = movimentacaoService.remove(Integer.parseInt(req.params(":id")));
            return gson.toJson(success);
        });

        // Incidentes
        get("/incidentes", (req, res) -> {
            res.type("application/json");
            String resolvidoParam = req.queryParams("resolvido");
            String nivelParam = req.queryParams("nivel");

            Boolean resolvido = null;
            if (resolvidoParam != null && !resolvidoParam.isBlank()) {
                resolvido = Boolean.parseBoolean(resolvidoParam);
            }

            return gson.toJson(incidenteService.listar(resolvido, nivelParam));
        });

        get("/incidentes/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            Incidente incidente = incidenteService.get(id);
            if (incidente != null) {
                return gson.toJson(incidente);
            }
            res.status(404);
            return gson.toJson(Map.of("error", "Incidente nao encontrado"));
        });

        post("/incidentes", (req, res) -> {
            res.type("application/json");
            Incidente incidente = gson.fromJson(req.body(), Incidente.class);
            boolean created = incidenteService.insert(incidente);
            if (created) {
                res.status(201);
            } else {
                res.status(400);
            }
            return gson.toJson(incidente);
        });

        put("/incidentes/:id", (req, res) -> {
            res.type("application/json");
            Incidente incidente = gson.fromJson(req.body(), Incidente.class);
            incidente.setId(Integer.parseInt(req.params(":id")));
            boolean updated = incidenteService.update(incidente);
            if (!updated) {
                res.status(400);
            }
            return gson.toJson(incidente);
        });

        delete("/incidentes/:id", (req, res) -> {
            res.type("application/json");
            boolean removed = incidenteService.remove(Integer.parseInt(req.params(":id")));
            if (!removed) {
                res.status(404);
            }
            return gson.toJson(removed);
        });
    }

    // CORS liberado para chamadas do front local
    private static void enableCORS() {
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        options("/*", (req, res) -> {
            String reqHeaders = req.headers("Access-Control-Request-Headers");
            if (reqHeaders != null) { res.header("Access-Control-Allow-Headers", reqHeaders); }

            String reqMethod = req.headers("Access-Control-Request-Method");
            if (reqMethod != null) { res.header("Access-Control-Allow-Methods", reqMethod); }
            return "OK";
        });
    }
}

