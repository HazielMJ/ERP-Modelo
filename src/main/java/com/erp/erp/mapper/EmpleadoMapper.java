package com.erp.erp.mapper;

import com.erp.erp.dto.EmpleadoDTO;
import com.erp.erp.entity.Empleado;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmpleadoMapper {
    
    public EmpleadoDTO toDTO(Empleado empleado) {
        if (empleado == null) return null;
        
        EmpleadoDTO dto = EmpleadoDTO.builder()
            .idEmpleado(empleado.getIdEmpleado())
            .codigoEmpleado(empleado.getCodigoEmpleado())
            .nombre(empleado.getNombre())
            .apellido(empleado.getApellido())
            .numeroDocumento(empleado.getNumeroDocumento())
            .fechaNacimiento(empleado.getFechaNacimiento())
            .rfc(empleado.getRfc())
            .curp(empleado.getCurp())
            .nss(empleado.getNss())
            .cp(empleado.getCp())
            .direccion(empleado.getDireccion())
            .telefono(empleado.getTelefono())
            .correo(empleado.getCorreo())
            .fechaContratacion(empleado.getFechaContratacion())
            .fechaTermino(empleado.getFechaTermino())
            .salarioBase(empleado.getSalarioBase())
            .build();
        
      
        if (empleado.getTipoDocumento() != null) {
            dto.setTipoDocumento(empleado.getTipoDocumento().name());
        }
        if (empleado.getGenero() != null) {
            dto.setGenero(empleado.getGenero().name());
        }
        if (empleado.getTipoContrato() != null) {
            dto.setTipoContrato(empleado.getTipoContrato().name());
        }
        if (empleado.getJornada() != null) {
            dto.setJornada(empleado.getJornada().name());
        }
        if (empleado.getEstado() != null) {
            dto.setEstado(empleado.getEstado().name());
        }
        if (empleado.getRol() != null) {
            dto.setRol(empleado.getRol().name());
        }
        
        if (empleado.getPuesto() != null) {
            dto.setPuesto(EmpleadoDTO.PuestoDTO.builder()
                .idPuesto(empleado.getPuesto().getIdPuesto())
                .codigoPuesto(empleado.getPuesto().getCodigoPuesto())
                .nombrePuesto(empleado.getPuesto().getNombrePuesto())
                .build());
        }
        
        if (empleado.getDepartamento() != null) {
            dto.setDepartamento(EmpleadoDTO.DepartamentoDTO.builder()
                .idDepartamento(empleado.getDepartamento().getIdDepartamento())
                .codigoDepartamento(empleado.getDepartamento().getCodigoDepartamento())
                .nombreDepartamento(empleado.getDepartamento().getNombreDepartamento())
                .build());
        }
        
        return dto;
    }
    
    public List<EmpleadoDTO> toDTOList(List<Empleado> empleados) {
        if (empleados == null) return null;
        return empleados.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
