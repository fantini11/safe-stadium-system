const LOGIN_URL = "/modulos/login/login.html";
const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.ADMINS);
const API_LOGIN_URL = `${API_URL}/login`;

// Objeto para o banco de dados de usuarios e usuario corrente
var db_usuarios = {};
var usuarioCorrente = {};

// --- FUNCOES DE LOGICA ---

function carregarUsuarios(callback) {
    fetch(API_URL)
        .then(response => response.json())
        .then(data => {
            db_usuarios = data;
            if (callback) callback();
        })
        .catch(error => {
            console.error("Erro ao ler usuarios via API:", error);
        });
}

async function loginUser(login, senha) {
    login = (login || "").trim();
    senha = (senha || "").trim();

    try {
        const resp = await fetch(API_LOGIN_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ login, senha })
        });

        if (!resp.ok) {
            return false;
        }

        const usuario = await resp.json();
        usuarioCorrente = usuario;
        sessionStorage.setItem('usuarioCorrente', JSON.stringify(usuarioCorrente));
        
        var stored = sessionStorage.getItem('returnURL');
        var returnUrl = '/index.html';
        if (stored && stored.trim() !== '' && !/login/i.test(stored)) {
            returnUrl = stored;
        }
        sessionStorage.removeItem('returnURL');
        window.location.href = returnUrl;
        return true;
    } catch (err) {
        console.error("Erro ao autenticar usuario:", err);
        return false;
    }
}

function logoutUser() {
    sessionStorage.removeItem("usuarioCorrente");
    window.location.href = LOGIN_URL;
}

function addUser(nome, login, senha, email) {
    let usuario = { login, senha, nome, email };

    fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuario),
    })
        .then(response => response.json())
        .then(() => {
            alert("Usuario inserido com sucesso! Por favor, faca o login.");
            carregarUsuarios();
        })
        .catch(error => {
            console.error("Erro ao inserir usuario:", error);
            alert("Erro ao inserir usuario");
        });
}

// --- LOGICA DE INICIALIZACAO E EVENTOS DA PAGINA ---

// Executa quando o conteudo do HTML foi completamente carregado
document.addEventListener("DOMContentLoaded", function () {

    // Gerencia o cabecalho em qualquer pagina que carregar este script
    const btnLogout = document.getElementById("btnLogout");
    const btnLogin = document.getElementById("btnLogin");
    
    let usuarioLogado = null;
    try {
        usuarioLogado = JSON.parse(sessionStorage.getItem("usuarioCorrente"));
    } catch (e) {
        usuarioLogado = null;
    }

    // Mostra o botão correto baseado no estado de login
    if (usuarioLogado && usuarioLogado.nome) {
        // Usuário logado - mostra Sair
        if (btnLogout) {
            btnLogout.style.display = "inline-flex";
            // Remove listeners antigos para evitar duplicidade
            btnLogout.removeEventListener("click", logoutUser);
            btnLogout.addEventListener("click", logoutUser);
        }
        if (btnLogin) {
            btnLogin.style.display = "none";
        }
    } else {
        // Usuário não logado - mostra Login
        if (btnLogout) {
            btnLogout.style.display = "none";
        }
        if (btnLogin) {
            btnLogin.style.display = "inline-flex";
        }
    }

    // Logica de eventos que so deve rodar na pagina de login
    if (window.location.pathname.includes("login.html")) {
        // Carrega os usuarios existentes para exibir no painel
        carregarUsuarios();

        // Pega a referencia do modal de cadastro do Bootstrap
        const registerModalElement = document.getElementById("registerModal");
        const registerModal = new bootstrap.Modal(registerModalElement);

        // Funcao para processar o formulario de login
        async function processaFormLogin(event) {
            event.preventDefault();
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;
            const ok = await loginUser(username, password);
            if (!ok) {
                alert("Usuario ou senha incorretos");
            }
        }

        // Funcao para salvar o novo usuario
        function salvaNovoUsuario(event) {
            event.preventDefault();

            let login = document.getElementById("txt_login").value;
            let nome = document.getElementById("txt_nome").value;
            let email = document.getElementById("txt_email").value;
            let senha = document.getElementById("txt_senha").value;
            let senha2 = document.getElementById("txt_senha2").value;

            if (senha !== senha2) {
                alert("As senhas informadas nao conferem.");
                return;
            }
            if (!login || !nome || !senha) {
                alert("Por favor, preencha todos os campos.");
                return;
            }

            addUser(nome, login, senha, email);

            document.getElementById("register-form").reset();
            registerModal.hide();
        }

        document.getElementById("login-form").addEventListener("submit", processaFormLogin);
        document.getElementById("btn_salvar").addEventListener("click", salvaNovoUsuario);
    }
});
