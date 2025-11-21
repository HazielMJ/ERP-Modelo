package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransportistaService {
    private final TransportistaRepository transportistaRepository;
    
    public Transportista crearTransportista(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }
    
    public Transportista actualizarTransportista(Integer id, Transportista transportista) {
        Transportista existente = obtenerTransportistaPorId(id);
        existente.setNombre(transportista.getNombre());
        existente.setEmpresa(transportista.getEmpresa());
        existente.setTelefono(transportista.getTelefono());
        existente.setEmail(transportista.getEmail());
        existente.setTipoVehiculo(transportista.getTipoVehiculo());
        existente.setPlacaVehiculo(transportista.getPlacaVehiculo());
        existente.setLicencia(transportista.getLicencia());
        return transportistaRepository.save(existente);
    }
    
    @Transactional(readOnly = true)
    public Transportista obtenerTransportistaPorId(Integer id) {
        return transportistaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transportista no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Transportista> obtenerTransportistasActivos() {
        return transportistaRepository.findByEstado(Transportista.EstadoTransportista.ACTIVO);
    }
}
