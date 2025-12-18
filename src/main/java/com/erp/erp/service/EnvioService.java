package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EnvioService {
    
    private final EnvioRepository envioRepository;
    private final VentasRepository ventasRepository;  // ⚠️ Verifica que este sea el nombre correcto
    private final TransportistaRepository transportistaRepository;
    private final SeguimientoEnvioRepository seguimientoEnvioRepository;
    
    @Transactional(readOnly = true)
    public List<Envio> obtenerTodos() {
        return envioRepository.findAllWithRelations();
    }
    
    @Transactional(readOnly = true)
    public Envio obtenerEnvioPorId(Integer id) {
        return envioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado: " + id));
    }
    
    @Transactional(readOnly = true)
    public Envio obtenerEnvioPorGuia(String numeroGuia) {
        return envioRepository.findByNumeroGuia(numeroGuia)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado con guía: " + numeroGuia));
    }
    
    @Transactional(readOnly = true)
    public List<Envio> obtenerEnviosEnProceso() {
        return envioRepository.findEnviosEnProceso();
    }
    
    public Envio crearEnvio(Envio envio) {
        if (envio.getNumeroGuia() == null || envio.getNumeroGuia().isEmpty()) {
            envio.setNumeroGuia(generarNumeroGuia());
        }
        envio.setFechaCreacion(LocalDateTime.now());
        envio.setEstado(Envio.EstadoEnvio.PENDIENTE);
        
        Envio guardado = envioRepository.save(envio);
        
        crearSeguimiento(guardado.getIdEnvio(), "Envío creado", SeguimientoEnvio.EstadoSeguimiento.PENDIENTE);
        
        return guardado;
    }
    
    public Envio crearEnvioDesdeVenta(Integer ventaId, Envio datosEnvio) {
        Venta venta = ventasRepository.findById(ventaId)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + ventaId));
        
        datosEnvio.setVenta(venta);
        datosEnvio.setCliente(venta.getCliente());
        
        return crearEnvio(datosEnvio);
    }
    
    public Envio actualizarEnvio(Integer id, Envio envioActualizado) {
        Envio envioExistente = obtenerEnvioPorId(id);
        
        envioExistente.setDireccionEntrega(envioActualizado.getDireccionEntrega());
        envioExistente.setCiudad(envioActualizado.getCiudad());
        envioExistente.setEstadoDestino(envioActualizado.getEstadoDestino());
        envioExistente.setCodigoPostal(envioActualizado.getCodigoPostal());
        envioExistente.setTelefonoContacto(envioActualizado.getTelefonoContacto());
        envioExistente.setReferenciaDireccion(envioActualizado.getReferenciaDireccion());
        envioExistente.setFechaEstimadaEntrega(envioActualizado.getFechaEstimadaEntrega());
        envioExistente.setCostoEnvio(envioActualizado.getCostoEnvio());
        envioExistente.setPesoKg(envioActualizado.getPesoKg());
        envioExistente.setObservaciones(envioActualizado.getObservaciones());
        
        if (envioActualizado.getMetodoEnvio() != null) {
            envioExistente.setMetodoEnvio(envioActualizado.getMetodoEnvio());
        }
        if (envioActualizado.getRuta() != null) {
            envioExistente.setRuta(envioActualizado.getRuta());
        }
        
        return envioRepository.save(envioExistente);
    }
    
    public void actualizarEstadoEnvio(Integer envioId, Envio.EstadoEnvio nuevoEstado, String descripcion) {
        Envio envio = obtenerEnvioPorId(envioId);
        envio.setEstado(nuevoEstado);
        
        if (nuevoEstado == Envio.EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(java.time.LocalDate.now());
        }
        
        envioRepository.save(envio);
        
        SeguimientoEnvio.EstadoSeguimiento estadoSeguimiento = 
            SeguimientoEnvio.EstadoSeguimiento.valueOf(nuevoEstado.name());
        crearSeguimiento(envioId, descripcion != null ? descripcion : "Estado actualizado a " + nuevoEstado, estadoSeguimiento);
    }
    
    public void eliminarEnvio(Integer id) {
        Envio envio = obtenerEnvioPorId(id);
        envioRepository.delete(envio);
    }
    
    public void asignarTransportista(Integer envioId, Integer transportistaId) {
        Envio envio = obtenerEnvioPorId(envioId);
        Transportista transportista = transportistaRepository.findById(transportistaId)
            .orElseThrow(() -> new RuntimeException("Transportista no encontrado: " + transportistaId));
        
        envio.setTransportista(transportista);
        envioRepository.save(envio);
        
        crearSeguimiento(envioId, "Transportista asignado: " + transportista.getNombre(), 
            SeguimientoEnvio.EstadoSeguimiento.PREPARANDO);
    }
    
    public SeguimientoEnvio crearSeguimiento(Integer envioId, String descripcion, SeguimientoEnvio.EstadoSeguimiento estado) {
        Envio envio = obtenerEnvioPorId(envioId);
        
        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
            .envio(envio)
            .fechaHora(LocalDateTime.now())
            .estadoEnvio(estado)
            .descripcion(descripcion)
            .build();
        
        return seguimientoEnvioRepository.save(seguimiento);
    }
    
    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientoEnvio(Integer envioId) {
        Envio envio = obtenerEnvioPorId(envioId);
        return seguimientoEnvioRepository.findByEnvioOrderByFechaHoraDesc(envio);
    }
    
    private String generarNumeroGuia() {
        return "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
