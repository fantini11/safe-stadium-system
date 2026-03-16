document.addEventListener('DOMContentLoaded', () => {
    // --- INÍCIO DA LÓGICA DE AUTENTICAÇÃO ATUALIZADA ---
    const userInfo = document.getElementById('userInfo');
    const btnLogout = document.getElementById('btnLogout');
    const btnLogin = document.getElementById('btnLogin');
    const usuarioCorrente = JSON.parse(sessionStorage.getItem('usuarioCorrente'));

    if (usuarioCorrente && usuarioCorrente.nome) {
        // Se o usuário está logado, configura o header
        userInfo.textContent = `Bem-vindo, ${usuarioCorrente.nome}`;
        btnLogout.style.display = 'inline-flex';
        btnLogin.style.display = 'none';

        btnLogout.addEventListener('click', function() {
            sessionStorage.removeItem('usuarioCorrente');
            window.location.href = '/modulos/login/login.html'; // Redireciona para o login após sair
        });
    } else {
        // Se não houver usuário logado, impede o acesso e redireciona
        alert('Acesso restrito. Por favor, faça o login para acessar esta página.');
        window.location.href = '../../modulos/login/login.html'; // Corrigido para o caminho certo
    }
    // --- FIM DA LÓGICA DE AUTENTICAÇÃO ---

    // Lógica original da página (mantida)
});
