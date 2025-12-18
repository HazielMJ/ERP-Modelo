
function obtenerURLBase() {
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
        return 'http://localhost:8082/api';
    }
    
    return window.location.origin + '/api';
}

const API_BASE_URL = obtenerURLBase();

const CONFIG = {
    API_BASE_URL: API_BASE_URL,
    TIMEOUT: 30000, // 30 segundos
    RETRY_ATTEMPTS: 3,
    DEBUG: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
};

if (CONFIG.DEBUG) {
    console.log('%c🔧 Configuración del Sistema', 'color: #4a90e2; font-size: 14px; font-weight: bold;');
    console.log('API Base URL:', CONFIG.API_BASE_URL);
    console.log('Ambiente:', CONFIG.DEBUG ? 'Desarrollo' : 'Producción');
}

window.API_BASE_URL = API_BASE_URL;
window.CONFIG = CONFIG;
