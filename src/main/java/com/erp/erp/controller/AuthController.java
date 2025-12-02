package com.erp.erp.controller;

import com.erp.erp.dto.*;
import com.erp.erp.entity.Usuario;
import com.erp.erp.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UsuarioService usuarioService;
    
    /**
     * POST /api/auth/login
     * Login de usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        try {
            LoginResponse response = usuarioService.login(request);
            
            // 🔑 GUARDAR INFORMACIÓN EN LA SESIÓN
            session.setAttribute("usuarioId", response.getId());
            session.setAttribute("nombreUsuario", response.getNombre());
            session.setAttribute("emailUsuario", response.getEmail());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("idEmpleado", response.getIdEmpleado());
            
            // 📌 Log para verificar
            System.out.println("════════════════════════════════════════");
            System.out.println("✅ LOGIN EXITOSO");
            System.out.println("📌 Usuario ID: " + response.getId());
            System.out.println("📌 Nombre: " + response.getNombre());
            System.out.println("📌 Email: " + response.getEmail());
            System.out.println("📌 Rol: " + response.getRol());
            System.out.println("📌 Session ID: " + session.getId());
            System.out.println("📌 UsuarioId guardado en sesión: " + session.getAttribute("usuarioId"));
            System.out.println("════════════════════════════════════════");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("success", "false");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * POST /api/auth/registro
     * Registro de nuevo usuario
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest request) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Usuario registrado exitosamente");
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail()
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * POST /api/auth/logout
     * Logout de usuario (usando sesión)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            
            System.out.println("════════════════════════════════════════");
            System.out.println("🚪 LOGOUT - Usuario ID: " + usuarioId);
            
            if (usuarioId != null) {
                usuarioService.logout(usuarioId);
                System.out.println("✅ Logout registrado en base de datos");
            }
            
            // Invalidar sesión
            session.invalidate();
            System.out.println("✅ Sesión invalidada");
            System.out.println("════════════════════════════════════════");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Logout exitoso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error en logout: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * GET /api/auth/verificar-sesion
     * Verificar si hay una sesión activa
     */
    @GetMapping("/verificar-sesion")
    public ResponseEntity<?> verificarSesion(HttpSession session) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");
        
        System.out.println("════════════════════════════════════════");
        System.out.println("🔍 VERIFICAR SESIÓN");
        System.out.println("📌 Session ID: " + session.getId());
        System.out.println("📌 Usuario ID: " + usuarioId);
        System.out.println("📌 Nombre Usuario: " + nombreUsuario);
        System.out.println("════════════════════════════════════════");
        
        Map<String, Object> response = new HashMap<>();
        
        if (usuarioId != null) {
            response.put("autenticado", true);
            response.put("usuarioId", usuarioId);
            response.put("nombreUsuario", nombreUsuario);
            response.put("sessionId", session.getId());
            return ResponseEntity.ok(response);
        } else {
            response.put("autenticado", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * POST /api/auth/cambiar-password
     * Cambiar contraseña del usuario
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        try {
            usuarioService.cambiarPasswordConValidacion(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Contraseña actualizada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * POST /api/auth/resetear-password/{id}
     * Resetear contraseña (solo para administradores)
     */
    @PostMapping("/resetear-password/{id}")
    public ResponseEntity<?> resetearPassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            String nuevaPassword = body.get("nuevaPassword");
            if (nuevaPassword == null || nuevaPassword.isEmpty()) {
                throw new RuntimeException("La nueva contraseña es obligatoria");
            }
            
            if (nuevaPassword.length() < 6) {
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
            }
            
            usuarioService.resetearPassword(id, nuevaPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Contraseña reseteada. El usuario deberá cambiarla en el próximo login");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * GET /api/auth/verificar/{email}
     * Verificar si un email ya está registrado
     */
    @GetMapping("/verificar/{email}")
    public ResponseEntity<?> verificarEmail(@PathVariable String email) {
        boolean existe = usuarioService.existeEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("existe", existe);
        response.put("disponible", !existe);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/auth/test
     * Endpoint de prueba
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("mensaje", "Auth API funcionando correctamente");
        return ResponseEntity.ok(response);
    }
}
