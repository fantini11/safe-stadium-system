document.addEventListener('DOMContentLoaded', function () {
    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.INCIDENTES);
    const { jsPDF } = window.jspdf;

    const detalhesContainer = document.getElementById('detalhes-incidente');
    const btnVoltar = document.getElementById('btn-voltar');
    const btnImprimirPDF = document.getElementById('btn-imprimir-pdf');
    const incidenteId = new URLSearchParams(window.location.search).get('id');

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
            window.location.href = '/modulos/login/login.html'; // Manda pro login ao sair
        });
    } else {
        // Usuário NÃO está logado
        userInfo.textContent = '';
        btnLogout.style.display = 'none';
        btnLogin.style.display = 'inline-flex';
    }
    // --- FIM DA LÓGICA DE AUTENTICAÇÃO ---

    btnVoltar.addEventListener('click', function() {
        window.location.href = 'lista.html';
    });

    function gerarPDFDetalhes(incidente) {
        const doc = new jsPDF();
        
        doc.setFontSize(18);
        doc.text(`Relatório do Incidente #${incidente.id}`, 15, 15);
        
        doc.setFontSize(10);
        doc.text(`Emitido em: ${new Date().toLocaleDateString()}`, 15, 25);
        
        let y = 35;
        doc.setFontSize(12);
        doc.text(`Data: ${formatarData(incidente.data)}`, 15, y); y += 10;
        doc.text(`Horário: ${incidente.horario || '--:--'}`, 15, y); y += 10;
        doc.text(`Setor: ${incidente.setor || 'Não informado'}`, 15, y); y += 10;
        doc.text(`Tipo: ${incidente.tipo || 'Não especificado'}`, 15, y); y += 10;
        doc.text(`Nível: ${incidente.nivel || 'Desconhecido'}`, 15, y); y += 10;
        doc.text(`Policiamento: ${incidente.policiamento ? 'Sim' : 'Não'}`, 15, y); y += 10;
        doc.text(`Status: ${incidente.resolvido ? 'Resolvido' : 'Pendente'}`, 15, y); y += 15;
        
        doc.text('Descrição:', 15, y); y += 7;
        const descricao = incidente.descricao || 'Nenhuma descrição fornecida.';
        const splitDesc = doc.splitTextToSize(descricao, 180);
        doc.text(splitDesc, 15, y);
        
        doc.setFontSize(8);
        doc.setTextColor(100);
        doc.text('SafeStadium - Sistema de Monitoramento de Incidentes', 105, 285, { align: 'center' });
        
        doc.save(`incidente_${incidente.id}_${new Date().toISOString().slice(0,10)}.pdf`);
    }
    
    async function carregarDetalhesIncidente() {
        if (!incidenteId) {
            detalhesContainer.innerHTML = '<div class="erro">Nenhum incidente selecionado.</div>';
            return;
        }
        try {
            const response = await fetch(`${API_URL}/${incidenteId}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const incidente = await response.json();

            detalhesContainer.innerHTML = `...`;
            
            btnImprimirPDF.addEventListener('click', () => {
                gerarPDFDetalhes(incidente);
            });
        } catch (error) {
            console.error('Erro ao carregar detalhes:', error);
            detalhesContainer.innerHTML = `<div class="erro"><p>Falha ao carregar os detalhes.</p><button onclick="location.reload()">Tentar novamente</button></div>`;
        }
    }

    function formatarData(dataString) {
        if (!dataString) return '--/--/----';
        const [ano, mes, dia] = dataString.split('-');
        return `${dia.padStart(2, '0')}/${mes.padStart(2, '0')}/${ano}`;
    }

    carregarDetalhesIncidente();
});
