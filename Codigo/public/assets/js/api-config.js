// Configuração centralizada das APIs do SafeStadium
// Backend Java rodando na porta 8081

const resolveBackendBaseUrl = () => {
    const host = window.location.hostname || '127.0.0.1';
    return `http://${host}:8081`;
};

const API_CONFIG = {
    // URL base do backend Java
    BASE_URL: resolveBackendBaseUrl(),
    
    // Endpoints
    ENDPOINTS: {
        // Usuários
        USUARIOS: '/usuarios',
        USUARIOS_LOGIN: '/usuarios/login',
        
        // Eventos
        EVENTOS: '/eventos',

        // Incidentes
        INCIDENTES: '/incidentes',

        // Movimentações
        MOVIMENTACOES: '/movimentacoes',

        // Denúncias
        DENUNCIAS: '/denuncias',
        
        // Reconhecimento Facial
        RECONHECIMENTOS: '/reconhecimentos',
        RECONHECIMENTO_IA: '/reconhecimento',

        // Admins
        ADMINS: '/admins',
        
        // Health Check
        HEALTH: '/health'
    },
    
    // Helper para construir URLs completas
    getUrl: function(endpoint) {
        return this.BASE_URL + endpoint;
    }
};

// Exporta para uso global
window.API_CONFIG = API_CONFIG;

console.log('✅ API Config carregada - Backend Java em:', API_CONFIG.BASE_URL);
