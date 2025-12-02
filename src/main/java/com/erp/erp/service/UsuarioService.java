package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import com.erp.erp.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public PerfilDTO obtenerPerfil(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        PerfilDTO perfil = PerfilDTO.builder()
            .id(usuario.getId())
            .nombre(usuario.getNombre())
            .email(usuario.getEmail())
            .activo(usuario.getActivo())
            .cambiarPassword(usuario.getCambiarPassword())
            .fechaCreacion(usuario.getFechaCreacion())
            .fechaActualizacion(usuario.getFechaActualizacion())
            .logout(usuario.getLogout())
            .build();
        
        if (usuario.getEmpleado() != null) {
            try {
                Empleado empleado = empleadoRepository.findById(usuario.getEmpleado().getIdEmpleado())
                    .orElse(null);
                
                if (empleado != null) {
                    perfil.setIdEmpleado(empleado.getIdEmpleado());
                    perfil.setCodigoEmpleado(empleado.getCodigoEmpleado());
                    perfil.setNombreEmpleado(empleado.getNombreCompleto());
                    perfil.setRol(empleado.getRol() != null ? empleado.getRol().name() : "EMPLEADO");
                    
                    if (empleado.getDepartamento() != null) {
                        perfil.setDepartamento(empleado.getDepartamento().getNombreDepartamento());
                    }
                    
                    if (empleado.getPuesto() != null) {
                        perfil.setPuesto(empleado.getPuesto().getNombrePuesto());
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error cargando empleado: " + e.getMessage());
            }
        }
        
        return perfil;
    }
    
  
    public PerfilDTO actualizarPerfil(Integer usuarioId, ActualizarPerfilRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!usuario.getEmail().equals(request.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            usuario.setEmail(request.getEmail());
        }
        
        usuario.setNombre(request.getNombre());
        usuario.setActivo(request.getActivo());
        usuario.setCambiarPassword(request.getCambiarPassword());
        
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new RuntimeException("Debes proporcionar la contraseña actual");
            }
            
            boolean passwordValida = false;
            if (usuario.getPassword().startsWith("$2a$") || usuario.getPassword().startsWith("$2b$")) {
                passwordValida = passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword());
            } else {
                passwordValida = usuario.getPassword().equals(request.getCurrentPassword());
            }
            
            if (!passwordValida) {
                throw new RuntimeException("La contraseña actual es incorrecta");
            }
            
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("Las contraseñas no coinciden");
            }
            
            if (request.getNewPassword().length() < 6) {
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
            }
            
            usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
            usuario.setCambiarPassword(false);
        }
        
        usuarioRepository.save(usuario);
        return obtenerPerfil(usuarioId);
    }
    
    
    public void desactivarCuenta(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(false);
        usuario.setLogout(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
    
    
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador");
        }
        
        boolean passwordValida = false;
        boolean necesitaEncriptar = false;
        
        if (usuario.getPassword().startsWith("$2a$") || usuario.getPassword().startsWith("$2b$")) {
            passwordValida = passwordEncoder.matches(request.getPassword(), usuario.getPassword());
        } else {
            passwordValida = usuario.getPassword().equals(request.getPassword());
            necesitaEncriptar = true; 
        }
        
        if (!passwordValida) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        if (necesitaEncriptar) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setCambiarPassword(false);
            usuarioRepository.save(usuario);
        }
        
        String rol = "EMPLEADO";
        String nombreEmpleado = usuario.getNombre();
        Integer idEmpleado = null;
        String codigoEmpleado = null;
        
        if (usuario.getEmpleado() != null) {
            try {
                Empleado empleado = empleadoRepository.findById(usuario.getEmpleado().getIdEmpleado())
                    .orElse(null);
                
                if (empleado != null) {
                    rol = empleado.getRol() != null ? empleado.getRol().name() : "EMPLEADO";
                    nombreEmpleado = empleado.getNombreCompleto();
                    idEmpleado = empleado.getIdEmpleado();
                    codigoEmpleado = empleado.getCodigoEmpleado();
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error cargando empleado: " + e.getMessage());
            }
        }
        
        return LoginResponse.builder()
            .id(usuario.getId())
            .nombre(usuario.getNombre())
            .email(usuario.getEmail())
            .activo(usuario.getActivo())
            .cambiarPassword(usuario.getCambiarPassword())
            .mensaje("Login exitoso")
            .rol(rol)
            .idEmpleado(idEmpleado)
            .nombreEmpleado(nombreEmpleado)
            .codigoEmpleado(codigoEmpleado)
            .build();
    }
    
    
    public void logout(Integer usuarioId) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        usuario.setLogout(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
    
    
    public Usuario registrarUsuario(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        Usuario usuario = Usuario.builder()
            .nombre(request.getNombre())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .activo(true)
            .cambiarPassword(false)
            .build();
        
        return usuarioRepository.save(usuario);
    }
    
    
    public void cambiarPasswordConValidacion(CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        boolean passwordValida = false;
        if (usuario.getPassword().startsWith("$2a$") || usuario.getPassword().startsWith("$2b$")) {
            passwordValida = passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword());
        } else {
            passwordValida = usuario.getPassword().equals(request.getPasswordActual());
        }
        
        if (!passwordValida) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuario.setCambiarPassword(false);
        usuarioRepository.save(usuario);
    }
    
    
    public void resetearPassword(Integer usuarioId, String nuevaPassword) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setCambiarPassword(true);
        usuarioRepository.save(usuario);
    }
    
    
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (!usuario.getPassword().startsWith("$2a$") && !usuario.getPassword().startsWith("$2b$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }
    
    
    public Usuario actualizarUsuario(Integer id, Usuario usuario) {
        Usuario existente = obtenerUsuarioPorId(id);
        existente.setNombre(usuario.getNombre());
        existente.setActivo(usuario.getActivo());
        
        if (!existente.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            existente.setEmail(usuario.getEmail());
        }
        
        return usuarioRepository.save(existente);
    }
    
    
    public void cambiarPassword(Integer id, String passwordActual, String passwordNueva) {
        Usuario usuario = obtenerUsuarioPorId(id);
        
        boolean passwordValida = false;
        if (usuario.getPassword().startsWith("$2a$") || usuario.getPassword().startsWith("$2b$")) {
            passwordValida = passwordEncoder.matches(passwordActual, usuario.getPassword());
        } else {
            passwordValida = usuario.getPassword().equals(passwordActual);
        }
        
        if (!passwordValida) {
            throw new RuntimeException("Password actual incorrecta");
        }
        
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuario.setCambiarPassword(false);
        usuarioRepository.save(usuario);
    }
    
    
    public void desactivarUsuario(Integer id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    
    public void activarUsuario(Integer id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
    
    
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivo(true);
    }
    
    
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
