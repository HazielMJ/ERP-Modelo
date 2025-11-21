package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeguimientoEnvioService {
    private final SeguimientoEnvioRepository seguimientoEnvioRepository;
    private final EnvioRepository envioRepository;
    private final UsuarioRepository usuarioRepository;

    public SeguimientoEnvio crearSeguimiento(SeguimientoEnvio seguimiento) {
        if (seguimiento.getFechaHora() == null) {
            seguimiento.setFechaHora(LocalDateTime.now());
        }
        return seguimientoEnvioRepository.save(seguimiento);
    }

    public SeguimientoEnvio crearSeguimientoParaEnvio(Integer envioId, SeguimientoEnvio.EstadoSeguimiento estado, String descripcion, Integer usuarioId) {
        Envio envio = envioRepository.findById(envioId)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        
        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId).orElse(null);
        }
        
        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
            .envio(envio)
            .estadoEnvio(estado)
            .descripcion(descripcion)
            .usuario(usuario)
            .build();
        
        return seguimientoEnvioRepository.save(seguimiento);
    }

    public SeguimientoEnvio actualizarSeguimiento(Integer id, SeguimientoEnvio seguimiento) {
        SeguimientoEnvio existente = obtenerSeguimientoPorId(id);
        existente.setEstadoEnvio(seguimiento.getEstadoEnvio());
        existente.setUbicacionActual(seguimiento.getUbicacionActual());
        existente.setLatitud(seguimiento.getLatitud());
        existente.setLongitud(seguimiento.getLongitud());
        existente.setDescripcion(seguimiento.getDescripcion());
        return seguimientoEnvioRepository.save(existente);
    }

    public void eliminarSeguimiento(Integer id) {
        seguimientoEnvioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SeguimientoEnvio obtenerSeguimientoPorId(Integer id) {
        return seguimientoEnvioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Seguimiento no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> listarTodos() {
        return seguimientoEnvioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientosPorEnvio(Integer envioId) {
        return seguimientoEnvioRepository.findByEnvioIdOrderByFechaDesc(envioId);
    }

    @Transactional(readOnly = true)
    public SeguimientoEnvio obtenerUltimoSeguimiento(Integer envioId) {
        List<SeguimientoEnvio> seguimientos = seguimientoEnvioRepository
            .findByEnvioIdOrderByFechaDesc(envioId);
        
        if (seguimientos.isEmpty()) {
            throw new RuntimeException("No hay seguimientos para este envío");
        }
        
        return seguimientos.get(0);
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientosPorEstado(SeguimientoEnvio.EstadoSeguimiento estado) {
        return seguimientoEnvioRepository.findByEstadoEnvio(estado);
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientosPorEnvioYEstado(Integer envioId, SeguimientoEnvio.EstadoSeguimiento estado) {
        return seguimientoEnvioRepository.findByEnvioIdAndEstado(envioId, estado);
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientosPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return seguimientoEnvioRepository.findByFechaHoraBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientosPorUsuario(Integer usuarioId) {
        return seguimientoEnvioRepository.findByUsuarioId(usuarioId);
    }
}
