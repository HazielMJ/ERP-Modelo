package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import com.erp.erp.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {
    private final UsuarioService usuarioService;
    
   
    @GetMapping("/perfil")
    public ResponseEntity<PerfilDTO> obtenerPerfil(HttpSession session) {
        try {
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            if (usuarioId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            PerfilDTO perfil = usuarioService.obtenerPerfil(usuarioId);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    @PutMapping("/perfil")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(
            @RequestBody ActualizarPerfilRequest request,
            HttpSession session) {
        try {
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            if (usuarioId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("mensaje", "No autorizado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            PerfilDTO perfilActualizado = usuarioService.actualizarPerfil(usuarioId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Perfil actualizado exitosamente");
            response.put("perfil", perfilActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    
    @DeleteMapping("/perfil")
    public ResponseEntity<Map<String, String>> desactivarCuenta(HttpSession session) {
        try {
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            if (usuarioId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "No autorizado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            usuarioService.desactivarCuenta(usuarioId);
            session.invalidate();
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cuenta desactivada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        try {
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            if (usuarioId != null) {
                usuarioService.logout(usuarioId);
            }
            session.invalidate();
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Sesión cerrada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
  
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Integer id, 
            @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    
    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> passwords) {
        try {
            usuarioService.cambiarPassword(
                id, 
                passwords.get("passwordActual"), 
                passwords.get("passwordNueva")
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Contraseña actualizada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, String>> desactivarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.desactivarUsuario(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario desactivado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    
    @PutMapping("/{id}/activar")
    public ResponseEntity<Map<String, String>> activarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.activarUsuario(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario activado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
   
    @GetMapping("/activos")
    public ResponseEntity<List<Usuario>> obtenerUsuariosActivos() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerUsuariosActivos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
   
    @GetMapping("/existe-email/{email}")
    public ResponseEntity<Map<String, Boolean>> existeEmail(@PathVariable String email) {
        try {
            boolean existe = usuarioService.existeEmail(email);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
