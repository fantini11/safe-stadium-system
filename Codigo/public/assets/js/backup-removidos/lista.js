document.addEventListener('DOMContentLoaded', function () {
    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.INCIDENTES);
    const { jsPDF } = window.jspdf;

    const tabelaIncidentes = document.getElementById('corpo-tabela');
    const btnFiltrar = document.getElementById('btn-aplicar-filtros');
    const btnExportarPDF = document.getElementById('btn-exportar-pdf');
    const btnCadastrar = document.getElementById('btn-cadastrar-incidente');
    
    // --- INÍCIO DA LÓGICA DE AUTENTICAÇÃO ATUALIZADA ---
    const userInfo = document.getElementById('userInfo');
    const btnLogout = document.getElementById('btnLogout');
    const btnLogin = document.getElementById('btnLogin');
    const usuarioCorrente = JSON.parse(sessionStorage.getItem('usuarioCorrente'));

    if (usuarioCorrente && usuarioCorrente.nome) {
        // Usuário está logado
        userInfo.textContent = `Bem-vindo, ${usuarioCorrente.nome}`;
        btnLogout.style.display = 'inline-flex';
        btnLogin.style.display = 'none';

        btnLogout.addEventListener('click', function() {
            sessionStorage.removeItem('usuarioCorrente');
            window.location.reload();
        });
    } else {
        // Usuário NÃO está logado
        userInfo.textContent = '';
        btnLogout.style.display = 'none';
        btnLogin.style.display = 'inline-flex';
        // Esconde os botões que exigem login
        btnCadastrar.style.display = 'none';
        btnExportarPDF.style.display = 'none';
    }
    // --- FIM DA LÓGICA DE AUTENTICAÇÃO ---

    // Cadastrar novo incidente (só funciona se o botão estiver visível)
    btnCadastrar.addEventListener('click', function() {
        window.location.href = 'cadastrar.html';
    });

    // Carrega incidentes
    carregarIncidentes();

    // Gerar PDF
    function gerarRelatorioPDF(incidentes) {
        const doc = new jsPDF();
        
        doc.setFontSize(18);
        doc.text('Relatório de Incidentes - SafeStadium', 15, 15);
        
        doc.setFontSize(10);
        doc.text(`Emitido em: ${new Date().toLocaleDateString()}`, 15, 25);
        
        doc.autoTable({
            head: [['ID', 'Data', 'Horário', 'Setor', 'Tipo', 'Nível', 'Policiamento', 'Status']],
            body: incidentes.map(inc => [
                inc.id,
                formatarData(inc.data),
                inc.horario,
                inc.setor,
                inc.tipo,
                inc.nivel,
                inc.policiamento ? 'Sim' : 'Não',
                inc.resolvido ? 'Resolvido' : 'Pendente'
            ]),
            startY: 30,
            styles: { cellPadding: 3, fontSize: 8, valign: 'middle' },
            headStyles: { fillColor: [26, 26, 46], textColor: 255 },
            alternateRowStyles: { fillColor: [240, 240, 240] }
        });
        
        doc.save(`relatorio_incidentes_${new Date().toISOString().slice(0,10)}.pdf`);
    }
    
    // Exportar PDF (só funciona se o botão estiver visível)
    btnExportarPDF.addEventListener('click', async function() {
        try {
            const response = await fetch(API_URL);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const incidentes = await response.json();
            gerarRelatorioPDF(incidentes);
        } catch (error) {
            console.error('Falha ao gerar PDF:', error);
            alert('Erro ao gerar o relatório em PDF');
        }
    });

    // Função para excluir incidente
    async function excluirIncidente(id) {
        if (!confirm('Tem certeza que deseja excluir este incidente?')) {
            return;
        }
        try {
            const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            alert('Incidente excluído com sucesso!');
            carregarIncidentes();
        } catch (error) {
            console.error('Falha ao excluir incidente:', error);
            alert('Erro ao excluir o incidente');
        }
    }

    // Carregar incidentes
    async function carregarIncidentes(filtros = {}) {
        let url = API_URL;
        const params = new URLSearchParams();
        if (filtros.resolvido !== undefined) params.append('resolvido', filtros.resolvido);
        if (filtros.nivel) params.append('nivel', filtros.nivel);
        if (params.toString()) {
            url += `?${params.toString()}`;
        }

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const incidentes = await response.json();
            exibirIncidentes(incidentes);
        } catch (error) {
            console.error('Falha ao carregar dados:', error);
            tabelaIncidentes.innerHTML = `<tr><td colspan="9" class="erro">Falha na conexão com o servidor. <button onclick="location.reload()">Tentar novamente</button></td></tr>`;
        }
    }
