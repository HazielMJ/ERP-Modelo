package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnvioService {
    private final EnvioRepository envioRepository;
    private final SeguimientoEnvioRepository seguimientoRepository;
    private final VentasRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    
    public Envio crearEnvio(Envio envio) {
        envio.setNumeroGuia(generarNumeroGuia());
        Envio guardado = envioRepository.save(envio);
        
        // Crear primer seguimiento
        crearSeguimiento(guardado.getIdEnvio(), "Envío creado", SeguimientoEnvio.EstadoSeguimiento.PENDIENTE);
        
        return guardado;
    }
    
    public Envio crearEnvioDesdeVenta(Integer ventaId, Envio datosEnvio) {
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        datosEnvio.setVenta(venta);
        datosEnvio.setCliente(venta.getCliente());
        
        return crearEnvio(datosEnvio);
    }
    
    public SeguimientoEnvio crearSeguimiento(Integer envioId, String descripcion, SeguimientoEnvio.EstadoSeguimiento estado) {
        Envio envio = obtenerEnvioPorId(envioId);
        
        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
            .envio(envio)
            .estadoEnvio(estado)
            .descripcion(descripcion)
            .build();
        
        return seguimientoRepository.save(seguimiento);
    }
    
    public void actualizarEstadoEnvio(Integer id, Envio.EstadoEnvio nuevoEstado, String descripcion) {
        Envio envio = obtenerEnvioPorId(id);
        envio.setEstado(nuevoEstado);
        
        if (nuevoEstado == Envio.EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(LocalDate.now());
        }
        
        envioRepository.save(envio);
        
        // Crear seguimiento
        SeguimientoEnvio.EstadoSeguimiento estadoSeg = mapearEstadoEnvio(nuevoEstado);
        crearSeguimiento(id, descripcion, estadoSeg);
    }
    
    public void asignarTransportista(Integer envioId, Integer transportistaId) {
        Envio envio = obtenerEnvioPorId(envioId);
        Transportista transportista = new Transportista();
        transportista.setIdTransportista(transportistaId);
        envio.setTransportista(transportista);
        envioRepository.save(envio);
    }
    
    private String generarNumeroGuia() {
        return "ENV-" + System.currentTimeMillis();
    }
    
    private SeguimientoEnvio.EstadoSeguimiento mapearEstadoEnvio(Envio.EstadoEnvio estado) {
        switch (estado) {
            case PENDIENTE: return SeguimientoEnvio.EstadoSeguimiento.PENDIENTE;
            case PREPARANDO: return SeguimientoEnvio.EstadoSeguimiento.PREPARANDO;
            case EN_TRANSITO: return SeguimientoEnvio.EstadoSeguimiento.EN_TRANSITO;
            case ENTREGADO: return SeguimientoEnvio.EstadoSeguimiento.ENTREGADO;
            default: return SeguimientoEnvio.EstadoSeguimiento.PENDIENTE;
        }
    }
    
    @Transactional(readOnly = true)
    public Envio obtenerEnvioPorId(Integer id) {
        return envioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Envio obtenerEnvioPorGuia(String numeroGuia) {
        return envioRepository.findByNumeroGuia(numeroGuia)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Envio> obtenerEnviosEnProceso() {
        return envioRepository.findEnviosEnProceso();
    }
    
    @Transactional(readOnly = true)
    public List<SeguimientoEnvio> obtenerSeguimientoEnvio(Integer envioId) {
        return seguimientoRepository.findByEnvioIdOrderByFechaDesc(envioId);
    }
}
