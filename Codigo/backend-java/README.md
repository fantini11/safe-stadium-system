# SafeStadium – Back-End (Sprint 3)

**Stack**
- Java 17 + SparkJava (REST)
- PostgreSQL (Supabase) via JDBC
- Gson (JSON)
- (Opcional) Azure Vision Face API
- Build com Maven
- Porta padrão: **8081**

## Estrutura
```
backend-java/
├── pom.xml
├── .env.example
├── src/main/
│   ├── java/
│   │   ├── app/Aplicacao.java
│   │   ├── dao/{ConnectionFactory, AdminDAO, UsuarioDAO, EventoDAO, MovimentacaoDAO, IncidenteDAO}.java
│   │   ├── model/{Admin, Usuario, Evento, Movimentacao, Incidente}.java
│   │   └── service/{AdminService, UsuarioService, EventoService, MovimentacaoService, IncidenteService}.java
│   └── resources/
│       └── script-bd/schema.sql
```

## Preparação
1. Instale **Java 17** e **Maven 3.8+**.
2. Crie um arquivo `.env` (baseie-se em `.env.example`) ou exporte as variáveis abaixo antes de iniciar o servidor:
   ```powershell
   set DB_HOST=aws-1-us-east-2.pooler.supabase.com
   set DB_PORT=5432
   set DB_NAME=postgres
   set DB_USER=postgres.prwtauhapvrbdnvrbkfn
   set DB_PASSWORD=@SafeStadium123
   set AZURE_FACE_ENDPOINT=https://seu-endpoint.cognitiveservices.azure.com
   set AZURE_FACE_KEY=sua-chave
   ```
   > Use `export` no Linux/macOS; as variáveis do Azure são opcionais e só serão usadas quando a IA estiver configurada.
3. Execute no Supabase o script `src/main/resources/script-bd/schema.sql`. Ele cria/atualiza as tabelas `admins`, `usuarios`, `eventos`, `movimentacoes` e **incidentes**, além de inserir dados de exemplo.

## Execução
```bash
mvn clean package
mvn -q exec:java
```

Health-check rápido: `curl http://localhost:8081/health`.

## Endpoints
| Recurso       | Verbo(s) | Rota                                                    | Observações                                   |
|---------------|----------|----------------------------------------------------------|-----------------------------------------------|
| Health        | GET      | `/health`                                                | Verifica disponibilidade do serviço           |
| Usuários      | GET/POST | `/usuarios`                                              | CRUD completo                                 |
|               | GET      | `/usuarios/:id`, `/usuarios/cpf/:cpf`                    |                                               |
|               | PUT      | `/usuarios/:id`                                         |                                               |
|               | DELETE   | `/usuarios/:id`                                         |                                               |
| Eventos       | GET/POST | `/eventos`                                               | CRUD completo                                 |
|               | GET      | `/eventos/:id`                                          |                                               |
|               | PUT      | `/eventos/:id`                                          |                                               |
|               | DELETE   | `/eventos/:id`                                          |                                               |
| Admins        | GET/POST | `/admins`                                               | Cadastro de operadores                        |
|               | POST     | `/admins/login`                                         | Autenticação simples                          |
| Movimentações | GET/POST | `/movimentacoes`                                        | Registra entradas/saídas                      |
|               | GET      | `/movimentacoes/:id`, `/movimentacoes/cpf/:cpf`         |                                               |
|               | PUT      | `/movimentacoes/:id`                                    |                                               |
|               | DELETE   | `/movimentacoes/:id`                                    |                                               |
| Incidentes    | GET/POST | `/incidentes`                                           | Suporta filtros `?resolvido=` e `?nivel=`     |
|               | GET      | `/incidentes/:id`                                       |                                               |
|               | PUT      | `/incidentes/:id`                                       |                                               |
|               | DELETE   | `/incidentes/:id`                                       |                                               |

## Integração com IA
A integração de reconhecimento facial deve usar o Azure Vision Face API. Defina `AZURE_FACE_ENDPOINT` e `AZURE_FACE_KEY` e implemente as chamadas no serviço correspondente (`service`), respeitando os limites de uso da conta estudantil. Documente a proposta no canvas de Sistemas Inteligentes da sprint.
