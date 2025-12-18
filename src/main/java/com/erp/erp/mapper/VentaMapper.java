package com.erp.erp.mapper;

import com.erp.erp.dto.VentaDTO;
import com.erp.erp.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class VentaMapper {
    
    public VentaDTO toDTO(Venta venta) {
        if (venta == null) return null;
        
        return VentaDTO.builder()
            .idVenta(venta.getIdVenta())
            .numeroVenta(venta.getNumeroVenta())
            .fechaVenta(venta.getFechaVenta())
            .cliente(toClienteSimpleDTO(venta.getCliente()))
            .usuario(toUsuarioSimpleDTO(venta.getUsuario()))
            .subtotal(venta.getSubtotal())
            .impuestos(venta.getImpuestos())
            .descuento(venta.getDescuento())
            .totalVenta(venta.getTotalVenta())
            .estado(venta.getEstado())
            .tipoPago(venta.getTipoPago())
            .montoRecibido(venta.getMontoRecibido())
            .cambio(venta.getCambio())
            .observaciones(venta.getObservaciones())
            .detalles(venta.getDetalles().stream()
                .map(this::toDetalleDTO)
                .collect(Collectors.toList()))
            .build();
    }
    
    public Venta toEntity(VentaDTO dto) {
        if (dto == null) return null;
        
        Venta venta = Venta.builder()
            .idVenta(dto.getIdVenta())
            .numeroVenta(dto.getNumeroVenta())
            .fechaVenta(dto.getFechaVenta())
            .cliente(toClienteEntity(dto.getCliente()))
            .usuario(toUsuarioEntity(dto.getUsuario()))
            .subtotal(dto.getSubtotal())
            .impuestos(dto.getImpuestos())
            .descuento(dto.getDescuento())
            .totalVenta(dto.getTotalVenta())
            .estado(dto.getEstado())
            .tipoPago(dto.getTipoPago())
            .montoRecibido(dto.getMontoRecibido())
            .cambio(dto.getCambio())
            .observaciones(dto.getObservaciones())
            .build();
        
        if (dto.getDetalles() != null) {
            venta.setDetalles(dto.getDetalles().stream()
                .map(detalleDTO -> toDetalleEntity(detalleDTO, venta))
                .collect(Collectors.toList()));
        }
        
        return venta;
    }
    
    private VentaDTO.DetalleVentaDTO toDetalleDTO(DetalleVenta detalle) {
        return VentaDTO.DetalleVentaDTO.builder()
            .idDetalle(detalle.getIdDetalle())
            .producto(toProductoSimpleDTO(detalle.getProducto()))
            .cantidad(detalle.getCantidad())
            .precioUnitario(detalle.getPrecioUnitario())
            .descuento(detalle.getDescuento())
            .subtotal(detalle.getSubtotal())
            .impuesto(detalle.getImpuesto())
            .total(detalle.getTotal())
            .build();
    }
    
    private DetalleVenta toDetalleEntity(VentaDTO.DetalleVentaDTO dto, Venta venta) {
        DetalleVenta detalle = DetalleVenta.builder()
            .idDetalle(dto.getIdDetalle())
            .venta(venta)
            .producto(toProductoEntity(dto.getProducto()))
            .cantidad(dto.getCantidad())
            .precioUnitario(dto.getPrecioUnitario())
            .descuento(dto.getDescuento())
            .subtotal(dto.getSubtotal())
            .impuesto(dto.getImpuesto())
            .total(dto.getTotal())
            .build();
        
        return detalle;
    }
    
    private VentaDTO.ClienteSimpleDTO toClienteSimpleDTO(Cliente cliente) {
        if (cliente == null) return null;
        return new VentaDTO.ClienteSimpleDTO(
            cliente.getIdCliente(),
            cliente.getNombreRazonSocial(),
            cliente.getRfc(),
            cliente.getEmail()
        );
    }
    
    private Cliente toClienteEntity(VentaDTO.ClienteSimpleDTO dto) {
        if (dto == null || dto.getIdCliente() == null) return null;
        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.getIdCliente());
        return cliente;
    }
    
    private VentaDTO.UsuarioSimpleDTO toUsuarioSimpleDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new VentaDTO.UsuarioSimpleDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail()
        );
    }
    
    private Usuario toUsuarioEntity(VentaDTO.UsuarioSimpleDTO dto) {
        if (dto == null || dto.getId() == null) return null;
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        return usuario;
    }
    
    private VentaDTO.ProductoSimpleDTO toProductoSimpleDTO(Producto producto) {
        if (producto == null) return null;
        return new VentaDTO.ProductoSimpleDTO(
            producto.getIdProducto(),
            producto.getCodigoProducto(),
            producto.getNombreProducto(),
            producto.getPrecioVenta()
        );
    }
    
    private Producto toProductoEntity(VentaDTO.ProductoSimpleDTO dto) {
        if (dto == null || dto.getIdProducto() == null) return null;
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        return producto;
    }
}
