document.addEventListener('DOMContentLoaded', function () {
    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.INCIDENTES);
    
    const formIncidente = document.getElementById('form-incidente');
    const btnVoltar = document.getElementById('btn-voltar');

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
            window.location.href = '/modulos/login/login.html';
        });
    } else {
        // Se não houver usuário logado, impede o acesso e redireciona
        alert('Acesso restrito. Por favor, faça o login para acessar esta página.');
        window.location.href = '/modulos/login/login.html';
        return; // Impede o resto do script de carregar
    }
    // --- FIM DA LÓGICA DE AUTENTICAÇÃO ---

    // Voltar para a lista
    btnVoltar.addEventListener('click', function() {
        window.location.href = 'lista.html';
    });

    // Enviar formulário
    formIncidente.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const novoIncidente = {
            data: document.getElementById('data').value,
            horario: document.getElementById('horario').value,
            setor: document.getElementById('setor').value,
            tipo: document.getElementById('tipo').value,
            descricao: document.getElementById('descricao').value,
            nivel: document.getElementById('nivel').value,
            policiamento: document.getElementById('policiamento').value === 'true',
            resolvido: document.getElementById('resolvido').value === 'true' 
        };

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(novoIncidente)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            alert('Incidente cadastrado com sucesso!');
            window.location.href = 'lista.html';
        } catch (error) {
            console.error('Erro ao cadastrar incidente:', error);
            alert('Erro ao cadastrar incidente. Por favor, tente novamente.');
        }
    });

    // Define a data atual como padrão
    const hoje = new Date().toISOString().split('T')[0];
    document.getElementById('data').value = hoje;
});
