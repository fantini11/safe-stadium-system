document.addEventListener('DOMContentLoaded', () => {
    // --- Início da Lógica de Autenticação Atualizada ---
    const userInfo = document.getElementById('userInfo');
    const btnLogout = document.getElementById('btnLogout');
    const btnLogin = document.getElementById('btnLogin');
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
    // --- Fim da Lógica de Autenticação ---

    const API_URL = window.API_CONFIG.getUrl(window.API_CONFIG.ENDPOINTS.EVENTOS);

    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro na requisição: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Dados recebidos da API /eventos:', data);

            const eventosLista = document.getElementById('eventos-lista');

            if (!Array.isArray(data)) {
                throw new Error('Formato inválido: esperava um array de eventos');
            }

            const mapMes = {
                jan: '01', fev: '02', mar: '03', abr: '04', mai: '05', jun: '06',
                jul: '07', ago: '08', set: '09', out: '10', nov: '11', dez: '12'
            };

            const normalizaData = (valor) => {
                if (!valor) return null;

                if (/^\d{4}-\d{2}-\d{2}$/.test(valor)) {
                    return valor;
                }

                const matchPt = valor
                    .toLowerCase()
                    .match(/([a-zç\.]+)\s+(\d{1,2}),?\s+(\d{4})/);

                if (matchPt) {
                    const mesChave = matchPt[1].replace('.', '').slice(0, 3);
                    const mes = mapMes[mesChave];
                    if (mes) {
                        const dia = matchPt[2].padStart(2, '0');
                        return `${matchPt[3]}-${mes}-${dia}`;
                    }
                }

                return null;
            };

            const normalizaHorario = (valor) => {
                if (!valor) return '';
                if (/^\d{2}:\d{2}:\d{2}$/.test(valor)) {
                    return valor;
                }
                if (/^\d{2}:\d{2}$/.test(valor)) {
                    return `${valor}:00`;
                }
                const match = valor.match(/(\d{1,2}):(\d{2})(?::(\d{2}))?\s*(AM|PM)/i);
                if (!match) return '';
                let hora = parseInt(match[1], 10);
                const minuto = match[2];
                const segundo = match[3] || '00';
                const periodo = (match[4] || '').toUpperCase();
                if (periodo === 'PM' && hora < 12) {
                    hora += 12;
                } else if (periodo === 'AM' && hora === 12) {
                    hora = 0;
                }
                return `${hora.toString().padStart(2, '0')}:${minuto}:${segundo}`;
            };

            const criaDataSegura = (dataNormalizada, horarioNormalizado) => {
                if (!dataNormalizada) {
                    return null;
                }
                const [ano, mes, dia] = dataNormalizada.split('-').map(Number);
                let hora = 0;
                let minuto = 0;
                let segundo = 0;
                if (horarioNormalizado) {
                    const partesHora = horarioNormalizado.split(':');
                    hora = Number(partesHora[0] ?? 0);
                    minuto = Number(partesHora[1] ?? 0);
                    segundo = Number(partesHora[2] ?? 0);
                }
                return new Date(ano, mes - 1, dia, hora, minuto, segundo);
            };

            const eventosNormalizados = data.map(evento => {
                const dataBruta = evento.data || evento.data_evento || evento.dataEvento;
                const horarioBruto = evento.horario || evento.horario_evento || '';

                const dataNormalizada = normalizaData(dataBruta);
                const horarioNormalizado = normalizaHorario(horarioBruto);

                const dataEvento = criaDataSegura(dataNormalizada, horarioNormalizado);

                const dia = dataEvento ? dataEvento.getDate().toString().padStart(2, '0') : '--';
                const mes = dataEvento
                    ? dataEvento.toLocaleDateString('pt-BR', { month: 'short' })
                    : '--';
                const ano = dataEvento ? dataEvento.getFullYear() : '--';
                const dataFormatada = dataEvento
                    ? dataEvento.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
                    : 'Data não informada';
                const horarioFormatado = horarioNormalizado
                    ? horarioNormalizado.substring(0, 5)
                    : '--:--';

                return {
                    ...evento,
                    dataEvento,
                    dia,
                    mes,
                    ano,
                    dataFormatada,
                    horarioFormatado
                };
            }).sort((a, b) => {
                const timeA = a.dataEvento ? a.dataEvento.getTime() : 0;
                const timeB = b.dataEvento ? b.dataEvento.getTime() : 0;
                return timeA - timeB;
            });

            eventosNormalizados.forEach(evento => {
                const eventoDiv = document.createElement('div');
                eventoDiv.classList.add('evento-card');

                let iconeEvento = 'fas fa-calendar';
                const nomeNormalizado = (evento.nome || '')
                    .toLowerCase()
                    .normalize('NFD')
                    .replace(/[\u0300-\u036f]/g, '');

                if (nomeNormalizado.includes('jogo') || nomeNormalizado.includes('futebol')) {
                    iconeEvento = 'fas fa-futbol';
                } else if (nomeNormalizado.includes('show') || nomeNormalizado.includes('musica')) {
                    iconeEvento = 'fas fa-music';
                }

                eventoDiv.innerHTML = `
                    <h2 class="evento-nome"><i class="${iconeEvento}"></i> ${evento.nome}</h2>
                    <div class="evento-info">
                        <div class="evento-detail">
                            <i class="fas fa-calendar-day"></i>
                            <strong>Data:</strong>
                            <span>${evento.dataFormatada}</span>
                        </div>
                        <div class="evento-detail">
                            <i class="fas fa-clock"></i>
                            <strong>Horário:</strong>
                            <span>${evento.horarioFormatado}</span>
                        </div>
                        <div class="evento-detail">
                            <i class="fas fa-map-marker-alt"></i>
                            <strong>Local:</strong>
                            <span>${evento.local}</span>
                        </div>
                    </div>
                    <div class="evento-descricao">
                        <i class="fas fa-info-circle"></i> ${evento.descricao}
                    </div>
                `;

                eventosLista.appendChild(eventoDiv);
            });
        })
        .catch(error => {
            console.error('Erro ao carregar eventos:', error.message);
            const eventosLista = document.getElementById('eventos-lista');
            eventosLista.innerHTML = `
                <div style="text-align: center; padding: 2rem; color: #e74c3c;">
                    <i class="fas fa-exclamation-triangle" style="font-size: 2em; margin-bottom: 1rem;"></i>
                    <p>Erro ao carregar os eventos: ${error.message}</p>
                </div>
            `;
        });
});

