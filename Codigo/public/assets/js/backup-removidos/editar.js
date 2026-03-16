document.addEventListener('DOMContentLoaded', function () {
    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.INCIDENTES);
    
    const formIncidente = document.getElementById('form-incidente');
    const btnVoltar = document.getElementById('btn-voltar');
    const idIncidente = new URLSearchParams(window.location.search).get('id');

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

    // Carrega os dados do incidente
    async function carregarIncidente() {
        try {
            const response = await fetch(`${API_URL}/${idIncidente}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            
            const incidente = await response.json();
            
            document.getElementById('id-incidente').value = incidente.id;
            document.getElementById('data').value = incidente.data;
            document.getElementById('horario').value = incidente.horario;
            document.getElementById('setor').value = incidente.setor;
            document.getElementById('tipo').value = incidente.tipo;
            document.getElementById('descricao').value = incidente.descricao;
            document.getElementById('nivel').value = incidente.nivel;
            document.getElementById('policiamento').value = incidente.policiamento.toString();
            document.getElementById('resolvido').value = incidente.resolvido.toString();
        } catch (error) {
            console.error('Erro ao carregar incidente:', error);
            alert('Erro ao carregar incidente. Por favor, tente novamente.');
            window.location.href = 'lista.html';
        }
    }

    // Voltar para a lista
    btnVoltar.addEventListener('click', function() {
        window.location.href = 'lista.html';
    });

    // Enviar formulário
    formIncidente.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const incidenteAtualizado = {
            id: parseInt(document.getElementById('id-incidente').value),
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
            const response = await fetch(`${API_URL}/${idIncidente}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(incidenteAtualizado)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            alert('Incidente atualizado com sucesso!');
            window.location.href = 'lista.html';
        } catch (error) {
            console.error('Erro ao atualizar incidente:', error);
            alert('Erro ao atualizar incidente. Por favor, tente novamente.');
        }
    });

    // Carrega o incidente quando a página é aberta
    if (idIncidente) {
        carregarIncidente();
    } else {
        // Se não houver ID na URL, volta para a lista
        alert('ID do incidente não encontrado.');
        window.location.href = 'lista.html';
    }
});
