document.addEventListener('DOMContentLoaded', function() {
    const API_BASE_URL = window.API_CONFIG.BASE_URL;
    const INCIDENTES_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.INCIDENTES);

    // --- INÍCIO DA LÓGICA DE AUTENTICAÇÃO ATUALIZADA ---
    const userInfoElement = document.getElementById('userInfo');
    const btnLogout = document.getElementById('btnLogout');
    const btnLogin = document.getElementById('btnLogin');
    const currentUser = JSON.parse(sessionStorage.getItem('usuarioCorrente'));

    if (currentUser && currentUser.nome) {
        // Usuário está logado
        userInfoElement.textContent = `Bem-vindo, ${currentUser.nome}`;
        btnLogout.style.display = 'inline-flex';
        btnLogin.style.display = 'none';

        btnLogout.addEventListener('click', function() {
            sessionStorage.removeItem('usuarioCorrente');
            window.location.reload(); // Recarrega a página para atualizar o header
        });
    } else {
        // Usuário NÃO está logado
        userInfoElement.textContent = '';
        btnLogout.style.display = 'none';
        btnLogin.style.display = 'inline-flex'; // Mostra o botão de Cadastro / Login
    }
    // --- FIM DA LÓGICA DE AUTENTICAÇÃO ---

    // Mapa logic (backup)
});
