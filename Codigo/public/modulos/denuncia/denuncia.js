document.addEventListener('DOMContentLoaded', function () {
    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.INCIDENTES);

    const form = document.getElementById('form-denuncia');
    const btnCancelar = document.getElementById('btn-cancelar');
    const mensagemEl = document.getElementById('mensagem');
    const btnIdentificar = document.getElementById('btn-identificar');

    // Guarda o ultimo upload para exibir se o backend nao retornar foto
    let ultimaFotoBase64 = null;

    // Funcao auxiliar para converter arquivo em Base64
    const convertBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const fileReader = new FileReader();
            fileReader.readAsDataURL(file);
            fileReader.onload = () => resolve(fileReader.result);
            fileReader.onerror = (error) => reject(error);
        });
    };

    // Logica de Identificacao com IA
    if (btnIdentificar) {
        btnIdentificar.addEventListener('click', async function() {
            const inputFoto = document.getElementById('fotoSuspeito');
            const divResultado = document.getElementById('resultadoIA');
            const spanTexto = document.getElementById('textoResultado');
            
            if (inputFoto.files.length === 0) {
                alert("Por favor, selecione uma foto do suspeito primeiro!");
                return;
            }

            // Mostra que esta carregando
            divResultado.style.display = 'block';
            divResultado.style.backgroundColor = '#fff3cd';
            divResultado.style.color = '#856404';
            spanTexto.innerText = "Analisando rosto com IA... Aguarde...";

            try {
                const fotoBase64 = await convertBase64(inputFoto.files[0]);
                ultimaFotoBase64 = fotoBase64;

                const response = await fetch(window.API_CONFIG.getUrl('/denuncias/identificar'), {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ imagem: fotoBase64 })
                });

                const resultado = await response.json();

                if (resultado.nome) { // Se retornou um objeto com nome, achou!
                    divResultado.style.backgroundColor = '#f8d7da';
                    divResultado.style.color = '#721c24';
                    divResultado.style.border = '1px solid #f5c6cb';
                    const foto = resultado.foto
                        ? (resultado.foto.startsWith('data:') ? resultado.foto : `data:image/jpeg;base64,${resultado.foto}`)
                        : ultimaFotoBase64;
                    spanTexto.innerHTML = `
                        <strong>SUSPEITO IDENTIFICADO!</strong><br>
                        <strong>Nome:</strong> ${resultado.nome}<br>
                        <strong>Clube:</strong> ${resultado.clubeCoracao || 'N/A'}<br>
                        ${foto ? `<div style="margin-top:10px;"><img src="${foto}" alt="Foto do suspeito" style="max-width:180px;border-radius:6px;border:1px solid #ccc;"></div>` : ''}
                    `;
                } else {
                    divResultado.style.backgroundColor = '#d4edda';
                    divResultado.style.color = '#155724';
                    divResultado.style.border = '1px solid #c3e6cb';
                    spanTexto.innerText = "Nenhuma correspondencia encontrada no banco de dados.";
                }

            } catch (error) {
                console.error(error);
                divResultado.style.backgroundColor = '#f8d7da';
                divResultado.style.color = '#721c24';
                spanTexto.innerText = "Erro ao conectar com a IA. Verifique se o backend esta rodando.";
            }
        });
    }

    // Voltar ao indice do site
    btnCancelar.addEventListener('click', function () {
        window.location.href = '../../index.html';
    });

    // Define data padrao (hoje)
    const hoje = new Date().toISOString().split('T')[0];
    const dataInput = document.getElementById('data');
    if (dataInput) dataInput.value = hoje;

    // Define horario padrao (hora atual) sem segundos
    const horarioInput = document.getElementById('horario');
    if (horarioInput) {
        const now = new Date();
        const hh = String(now.getHours()).padStart(2, '0');
        const mm = String(now.getMinutes()).padStart(2, '0');
        horarioInput.value = `${hh}:${mm}`;
    }

    form.addEventListener('submit', async function (e) {
        e.preventDefault();

        // Recolhe valores do formulario
        const novo = {
            data: document.getElementById('data').value || null,
            horario: document.getElementById('horario').value || null,
            setor: document.getElementById('setor').value || null,
            tipo: document.getElementById('tipo').value || null,
            descricao: document.getElementById('descricao').value || null,
            nivel: document.getElementById('nivel').value || null,
            policiamento: document.getElementById('policiamento').checked || false,
            resolvido: document.getElementById('resolvido') ? document.getElementById('resolvido').checked : false
        };

        // Validacoes minimas
        if (!novo.descricao || novo.descricao.trim().length < 5) {
            alert('Por favor, descreva o incidente com pelo menos 5 caracteres.');
            return;
        }

        try {
            const resp = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(novo)
            });

            if (resp.ok) {
                mensagemEl.style.display = 'block';
                mensagemEl.style.color = '#0b6623';
                mensagemEl.textContent = 'Denuncia enviada com sucesso. Obrigado pela colaboracao.';
                form.reset();
                // Reaplica data/hora e checkbox padrao
                dataInput.value = hoje;
                if (horarioInput) horarioInput.value = `${String(new Date().getHours()).padStart(2,'0')}:${String(new Date().getMinutes()).padStart(2,'0')}`;
                const poli = document.getElementById('policiamento'); if (poli) poli.checked = false;
                const res = document.getElementById('resolvido'); if (res) res.checked = false;
            } else {
                const txt = await resp.text();
                console.error('Resposta do servidor:', resp.status, txt);
                mensagemEl.style.display = 'block';
                mensagemEl.style.color = '#8b0000';
                mensagemEl.textContent = 'Erro ao enviar denuncia. Tente novamente mais tarde.';
            }
        } catch (err) {
            console.error('Erro ao enviar denuncia:', err);
            mensagemEl.style.display = 'block';
            mensagemEl.style.color = '#8b0000';
            mensagemEl.textContent = 'Erro de conexao ao enviar denuncia. Verifique sua rede e tente novamente.';
        }
    });
});

