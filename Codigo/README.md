# SafeStadium – Código da Sprint 3

## Visão geral
Este diretório concentra o front-end estático (`public/`), o back-end Java (`backend-java/`) e scripts auxiliares. A arquitetura adotada na sprint integra:

- Front-end em HTML/CSS/JS puro servido via `http-server`.
- Back-end Java (Spark) expondo REST na porta `8081`.
- Banco PostgreSQL hospedado no Supabase.

## Requisitos
- Node.js 18+ (para rodar os scripts npm).
- Java 17 + Maven 3.8+.
- Conta Supabase com as tabelas criadas via `backend-java/src/main/resources/script-bd/schema.sql`.
- (Opcional) Conta Azure com Face API para o módulo inteligente.

## Configuração rápida
1. Instale as dependências de Node:
   ```bash
   npm install
   ```
2. Crie o arquivo `backend-java/.env` a partir de `.env.example` (ou exporte as variáveis no terminal).
3. No Supabase, execute o script SQL citado acima para garantir todas as tabelas (incluindo `incidentes`).

## Executando
Em dois terminais ou usando o comando combinado:
```bash
npm run start
```
Esse comando inicia o backend (`mvn -q exec:java`) e o front (`http-server public -p 3000`). Acesse o sistema em `http://localhost:3000`. O front consome a API Java exposta em `http://localhost:8081`.

Comandos individuais:
```bash
npm run backend   # apenas o servidor Java
npm run frontend  # apenas o front estático
```

## Estrutura principal
```
Codigo/
├── backend-java/       # Servidor REST em Java
├── public/             # Front-end
│   ├── assets/js/      # Scripts (consumindo API Java)
│   └── modulos/        # Telas específicas
├── db/db.json          # Dataset legado (referência)
├── package.json        # Scripts npm
```

## Integração com IA
O módulo de reconhecimento facial utilizará Azure Face API. Configure `AZURE_FACE_ENDPOINT` e `AZURE_FACE_KEY` no ambiente e registre a proposta no canvas de Sistemas Inteligentes conforme instruções da sprint. Enquanto os serviços não estiverem habilitados, exponha a rota, mocke respostas no front ou explique as limitações na apresentação.
