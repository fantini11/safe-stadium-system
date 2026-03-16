document.addEventListener('DOMContentLoaded', () => {
    const formFluxo = document.getElementById('form-fluxo');
    const tabelaMovimentacoes = document.getElementById('corpo-tabela-fluxo');
    const totalEntradas = document.getElementById('total-entradas');
    const totalSaidas = document.getElementById('total-saidas');
    const totalPresentes = document.getElementById('total-presentes');

    const btnLogout = document.getElementById('btnLogout');
    const userInfo = document.getElementById('userInfo');
    const btnLogin = document.getElementById('btnLogin');

    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.MOVIMENTACOES);
    let cacheMovimentacoes = [];

    function verificarUsuarioLogado() {
        const usuarioCorrente = JSON.parse(sessionStorage.getItem('usuarioCorrente'));

        if (usuarioCorrente && usuarioCorrente.nome) {
            userInfo.textContent = `Bem-vindo, ${usuarioCorrente.nome}`;
            btnLogout.style.display = 'inline-flex';
            btnLogin.style.display = 'none';

            btnLogout.addEventListener('click', () => {
                sessionStorage.removeItem('usuarioCorrente');
                window.location.reload();
            });
        } else {
            userInfo.textContent = '';
            btnLogout.style.display = 'none';
            btnLogin.style.display = 'inline-flex';
        }
    }

    async function carregarMovimentacoes() {
        try {
            const response = await fetch(API_URL);
            if (!response.ok) {
                throw new Error('Erro ao carregar movimentações');
            }
            cacheMovimentacoes = await response.json();
            renderizarMovimentacoes();
        } catch (error) {
            console.error('Erro ao carregar movimentações:', error);
            tabelaMovimentacoes.innerHTML = '<tr><td colspan="5">Erro ao carregar movimentações</td></tr>';
        }
    }

    function renderizarMovimentacoes() {
        tabelaMovimentacoes.innerHTML = '';

        if (!cacheMovimentacoes || cacheMovimentacoes.length === 0) {
            tabelaMovimentacoes.innerHTML = '<tr><td colspan="5">Nenhuma movimentação registrada.</td></tr>';
            return;
        }

        const ordenadas = [...cacheMovimentacoes]
            .sort((a, b) => {
                const tsA = a.timestamp ? new Date(a.timestamp).getTime() : 0;
                const tsB = b.timestamp ? new Date(b.timestamp).getTime() : 0;
                return tsB - tsA;
            })
            .slice(0, 10);

        ordenadas.forEach(mov => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${formatarData(mov.data)} ${mov.hora ?? '--:--'}</td>
                <td>${mov.nome}</td>
                <td>${mov.cpf}</td>
                <td class="${mov.tipo === 'entrada' ? 'entrada' : 'saida'}">
                    ${mov.tipo === 'entrada' ? 'Entrada' : 'Saída'}
                </td>
                <td>${mov.portao}</td>
            `;
            tabelaMovimentacoes.appendChild(row);
        });
    }

    function atualizarEstatisticas() {
        const entradas = cacheMovimentacoes.filter(m => m.tipo === 'entrada').length;
        const saidas = cacheMovimentacoes.filter(m => m.tipo === 'saida').length;
        const presentes = Math.max(entradas - saidas, 0);

        totalEntradas.textContent = entradas;
        totalSaidas.textContent = saidas;
        totalPresentes.textContent = presentes;
    }

    function formatarData(valor) {
        if (!valor) {
            return '--/--/----';
        }
        if (valor.includes('/')) {
            return valor;
        }
        const [ano, mes, dia] = valor.split('-');
        return `${dia}/${mes}/${ano}`;
    }

    formFluxo.addEventListener('submit', async (event) => {
        event.preventDefault();

        if (!sessionStorage.getItem('usuarioCorrente')) {
            alert('Você precisa estar logado para registrar uma movimentação.');
            window.location.href = '/modulos/login/login.html';
            return;
        }

        const tipo = document.getElementById('tipo-movimentacao').value;
        const portao = document.getElementById('portao').value;
        const cpf = document.getElementById('cpf-torcedor').value.replace(/\D/g, '');
        const nome = document.getElementById('nome-torcedor').value.trim();

        if (!tipo || !portao || !cpf || !nome) {
            alert('Por favor, preencha todos os campos.');
            return;
        }

        if (cpf.length !== 11) {
            alert('CPF deve conter exatamente 11 dígitos.');
            return;
        }

        const agora = new Date();
        const payload = {
            tipo,
            portao,
            cpf,
            nome,
            data: agora.toISOString().slice(0, 10),
            hora: agora.toTimeString().slice(0, 5),
            timestamp: agora.toISOString()
        };

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error(`Erro ${response.status}`);
            }

            formFluxo.reset();
            await carregarMovimentacoes();
            atualizarEstatisticas();
            alert(`Movimentação registrada com sucesso: ${nome} (${tipo === 'entrada' ? 'Entrada' : 'Saída'})`);
        } catch (error) {
            console.error('Erro ao registrar movimentação:', error);
            alert('Ocorreu um erro ao registrar a movimentação. Tente novamente.');
        }
    });

    async function init() {
        verificarUsuarioLogado();
        await carregarMovimentacoes();
        atualizarEstatisticas();
    }

    init();
});

