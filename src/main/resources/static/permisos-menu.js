// ==================== SISTEMA DE PERMISOS POR ROL ====================
// Archivo: permisos-menu.js
// Uso: <script src="permisos-menu.js"></script>

let usuarioActual = null;
let rolUsuario = null;

// ✅ DEFINICIÓN DE PERMISOS POR ROL
const PERMISOS_POR_ROL = {
    'ADMIN': ['*'], // Acceso total a todo
    
    'GERENTE': [
        'dashboard', 'clientes', 'ventas', 'compras', 'proveedores',
        'inventario', 'almacenes', 'reportes', 'facturacion', 'punto-venta'
    ],
    
    'VENDEDOR': [
        'dashboard', 'clientes', 'ventas', 'punto-venta', 'facturacion', 'inventario'
    ],
    
    'CONTADOR': [
        'dashboard', 'contabilidad', 'facturacion', 'reportes'
    ],
    
    'ALMACENISTA': [
        'dashboard', 'inventario', 'compras', 'proveedores', 'almacenes', 'logistica'
    ],
    
    'RRHH': [
        'dashboard', 'rrhh', 'nomina', 'usuarios'
    ],
    
    'EMPLEADO': [
        'dashboard'  // Solo acceso al dashboard
    ]
};

// ✅ NOMBRES LEGIBLES DE ROLES
const NOMBRES_ROLES = {
    'ADMIN': 'Administrador',
    'GERENTE': 'Gerente',
    'VENDEDOR': 'Vendedor',
    'CONTADOR': 'Contador',
    'ALMACENISTA': 'Almacenista',
    'RRHH': 'Recursos Humanos',
    'EMPLEADO': 'Empleado'
};

// ✅ VERIFICAR SI EL USUARIO TIENE PERMISO PARA UN MÓDULO
function tienePermiso(modulo) {
    if (!rolUsuario) {
        console.warn('⚠️ No hay rolUsuario definido');
        return false;
    }
    
    // ✅ NORMALIZAR ROL A MAYÚSCULAS
    const rolNormalizado = rolUsuario.toUpperCase().trim();
    
    // ✅ ADMIN siempre tiene acceso a todo
    if (rolNormalizado === 'ADMIN') {
        return true;
    }
    
    const permisos = PERMISOS_POR_ROL[rolNormalizado] || [];
    
    // Si tiene permiso total (ADMIN) - verificación adicional
    if (permisos.includes('*')) return true;
    
    // Si tiene permiso específico para el módulo
    return permisos.includes(modulo);
}

// ✅ VERIFICAR SI HAY SESIÓN ACTIVA
function verificarSesionActiva() {
    const usuarioStorage = localStorage.getItem('usuario');
    
    if (!usuarioStorage) {
        console.warn('⚠️ No hay sesión activa, redirigiendo al login...');
        // Delay para permitir que otros scripts se ejecuten antes de redirigir
        setTimeout(() => redirigirAlLogin(), 500);
        return false;
    }
    
    try {
        const usuario = JSON.parse(usuarioStorage);
        
        // Verificar que el objeto usuario tenga las propiedades necesarias
        if (!usuario.nombre || !usuario.rol) {
            console.warn('⚠️ Datos de sesión incompletos, redirigiendo al login...');
            // Delay para permitir que otros scripts se ejecuten antes de redirigir
            setTimeout(() => redirigirAlLogin(), 500);
            return false;
        }
        
        return true;
    } catch (error) {
        console.error('❌ Error al verificar sesión:', error);
        // Delay para permitir que otros scripts se ejecuten antes de redirigir
        setTimeout(() => redirigirAlLogin(), 500);
        return false;
    }
}

// ✅ REDIRIGIR AL LOGIN CON LIMPIEZA DE DATOS
function redirigirAlLogin() {
    // Limpiar cualquier dato corrupto o incompleto
    localStorage.removeItem('usuario');
    
    // Mostrar mensaje si existe la función
    if (typeof showNotification === 'function') {
        showNotification('⚠️ Sesión no válida. Por favor, inicia sesión nuevamente.', 'warning');
    }
    
    // Redirigir después de un breve delay para que se vea el mensaje
    setTimeout(() => {
        window.location.href = '/';
    }, 1000);
}

// ✅ CARGAR DATOS DEL USUARIO DESDE LOCALSTORAGE
function cargarUsuarioActual() {
    // Primero verificar que haya sesión activa
    if (!verificarSesionActiva()) {
        return false;
    }
    
    const usuarioStorage = localStorage.getItem('usuario');
    
    try {
        usuarioActual = JSON.parse(usuarioStorage);
        // ✅ NORMALIZAR ROL A MAYÚSCULAS para evitar problemas de comparación
        rolUsuario = (usuarioActual.rol || 'EMPLEADO').toUpperCase().trim();
        
        // ✅ ACTUALIZAR UI CON DATOS DEL USUARIO
        actualizarDatosUsuarioUI();
        
        console.log('✅ Usuario cargado:', {
            nombre: usuarioActual.nombre,
            rol: rolUsuario,
            empleado: usuarioActual.nombreEmpleado
        });
        
        return true;
    } catch (error) {
        console.error('❌ Error al cargar usuario:', error);
        redirigirAlLogin();
        return false;
    }
}

// ✅ ACTUALIZAR INTERFAZ CON DATOS DEL USUARIO
function actualizarDatosUsuarioUI() {
    const userAvatar = document.getElementById('userAvatar');
    const userName = document.getElementById('userName');
    const userRole = document.getElementById('userRole');
    
    if (userAvatar && usuarioActual) {
        const iniciales = usuarioActual.nombre.substring(0, 2).toUpperCase();
        userAvatar.textContent = iniciales;
    }
    
    if (userName && usuarioActual) {
        userName.textContent = usuarioActual.nombreEmpleado || usuarioActual.nombre;
    }
    
    if (userRole && rolUsuario) {
        userRole.textContent = NOMBRES_ROLES[rolUsuario] || rolUsuario;
    }
}

// ✅ APLICAR PERMISOS AL MENÚ DEL SIDEBAR
function aplicarPermisosMenu() {
    const navItems = document.querySelectorAll('.nav-item[data-modulo]');
    
    navItems.forEach(item => {
        const modulo = item.getAttribute('data-modulo');
        
        // Si el usuario NO tiene permiso, ocultar el item
        if (!tienePermiso(modulo)) {
            item.style.display = 'none';
        } else {
            item.style.display = 'list-item';
        }
    });
    
    // ✅ OCULTAR SECCIONES VACÍAS (sin items visibles)
    ocultarSeccionesVacias();
    
    console.log(`🔒 Permisos aplicados para rol: ${rolUsuario}`);
}

// ✅ OCULTAR SECCIONES DEL MENÚ QUE NO TIENEN ITEMS VISIBLES
function ocultarSeccionesVacias() {
    const secciones = document.querySelectorAll('.nav-section[data-seccion]');
    
    secciones.forEach(seccion => {
        let siguienteElemento = seccion.nextElementSibling;
        let tieneItemsVisibles = false;
        
        // Revisar los siguientes elementos hasta la próxima sección
        while (siguienteElemento && !siguienteElemento.hasAttribute('data-seccion')) {
            if (siguienteElemento.classList.contains('nav-item') && 
                siguienteElemento.style.display !== 'none') {
                tieneItemsVisibles = true;
                break;
            }
            siguienteElemento = siguienteElemento.nextElementSibling;
        }
        
        // Si no tiene items visibles, ocultar la sección
        if (!tieneItemsVisibles) {
            seccion.style.display = 'none';
        } else {
            seccion.style.display = 'list-item';
        }
    });
}

// ✅ PROTEGER NAVEGACIÓN - VERIFICAR PERMISOS ANTES DE CAMBIAR DE PÁGINA
function protegerNavegacion() {
    // ✅ NORMALIZAR ROL A MAYÚSCULAS
    const rolNormalizado = (rolUsuario || '').toUpperCase().trim();
    
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (!href || href === '#' || href === '/dashboard') return;
            
            // ✅ ADMIN: Permitir acceso a todo sin verificación
            if (rolNormalizado === 'ADMIN') {
                console.log('✅ ADMIN: Acceso permitido a', href);
                return; // Permitir navegación sin verificación adicional
            }
            
            // Obtener el data-modulo del elemento padre
            const navItem = this.closest('.nav-item');
            if (!navItem) return; // Si no está dentro de un nav-item, permitir navegación
            
            const modulo = navItem.getAttribute('data-modulo');
            if (!modulo) return; // Si no tiene data-modulo, permitir navegación
            
            console.log('Verificando permiso para módulo:', modulo);
            
            if (!tienePermiso(modulo)) {
                e.preventDefault();
                if (typeof showNotification === 'function') {
                    showNotification('⛔ No tienes permisos para acceder a este módulo', 'error');
                } else {
                    alert('⛔ No tienes permisos para acceder a este módulo');
                }
                console.warn(`Acceso denegado a: ${modulo}`);
            } else {
                console.log('✅ Permiso concedido para:', modulo);
            }
        });
    });
}

// ✅ MARCAR LINK ACTIVO EN EL MENÚ
function marcarLinkActivo() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// ✅ FUNCIÓN DE CERRAR SESIÓN
function cerrarSesion() {
    if (confirm('¿Está seguro que desea cerrar sesión?')) {
        if (typeof showNotification === 'function') {
            showNotification('Cerrando sesión...', 'info');
        }
        
        // Limpiar localStorage
        localStorage.removeItem('usuario');
        
        // Registrar logout en el backend
        fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(() => {
            console.log('✅ Logout registrado en backend');
        })
        .catch(error => {
            console.error('❌ Error al registrar logout:', error);
        })
        .finally(() => {
            setTimeout(() => {
                if (typeof showNotification === 'function') {
                    showNotification('Sesión cerrada exitosamente. ¡Hasta pronto!', 'success');
                }
                setTimeout(() => {
                    window.location.href = '/';
                }, 1000);
            }, 500);
        });
    }
}

// ✅ VERIFICAR SESIÓN PERIÓDICAMENTE (opcional - detecta si borran localStorage)
function iniciarVerificacionPeriodica() {
    // Verificar cada 30 segundos si la sesión sigue activa
    setInterval(() => {
        if (!verificarSesionActiva()) {
            console.warn('⚠️ Sesión perdida, redirigiendo al login...');
            redirigirAlLogin();
        }
    }, 30000); // 30 segundos
}

// ==================== INICIALIZACIÓN AUTOMÁTICA ====================
// ✅ Usar setTimeout para permitir que otros scripts se ejecuten primero
document.addEventListener('DOMContentLoaded', function() {
    // Pequeño delay para permitir que otros scripts se inicialicen
    setTimeout(function() {
        // ✅ 0. VERIFICAR SESIÓN ANTES DE TODO
        if (!verificarSesionActiva()) {
            return; // Ya se redirigió al login
        }
        
        // ✅ 1. CARGAR USUARIO Y VERIFICAR SESIÓN
        if (!cargarUsuarioActual()) {
            return; // Si no hay sesión, ya se redirigió
        }
        
        // ✅ 2. APLICAR PERMISOS AL MENÚ
        aplicarPermisosMenu();
        
        // ✅ 3. PROTEGER NAVEGACIÓN
        protegerNavegacion();
        
        // ✅ 4. MARCAR LINK ACTIVO
        marcarLinkActivo();
        
        // ✅ 5. INICIAR VERIFICACIÓN PERIÓDICA (opcional)
        // Descomenta la siguiente línea si quieres verificación continua
        // iniciarVerificacionPeriodica();
        
        // ✅ 6. LOG DE INFORMACIÓN
        console.log('%c🔒 Sistema de Permisos Cargado', 'color: #4a90e2; font-size: 14px; font-weight: bold;');
        console.log('%c👤 Usuario:', 'color: #333; font-size: 12px; font-weight: bold;', usuarioActual?.nombre);
        console.log('%c🎭 Rol:', 'color: #333; font-size: 12px; font-weight: bold;', NOMBRES_ROLES[rolUsuario]);
    }, 100); // Delay de 100ms para permitir que otros scripts se ejecuten
});

// ==================== EXPORTAR FUNCIONES (opcional para módulos ES6) ====================
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        tienePermiso,
        cargarUsuarioActual,
        aplicarPermisosMenu,
        protegerNavegacion,
        cerrarSesion,
        verificarSesionActiva,
        redirigirAlLogin,
        PERMISOS_POR_ROL,
        NOMBRES_ROLES
    };
}