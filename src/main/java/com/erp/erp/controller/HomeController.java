package com.erp.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Redirige la raíz a la página de login
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    // Muestra la vista de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // Dashboard principal
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    // Módulo de Contabilidad
    @GetMapping("/Contabilidad")
    public String Contabilidad() {
        return "Contabilidad";
    }
    
    // Módulo de Ventas
    @GetMapping("/ventas")
    public String ventas() {
        return "ventas";
    }
    
    // Módulo de Compras
    @GetMapping("/Compras")
    public String Compras() {
        return "Compras";
    }
    
    // Módulo de Inventario
    @GetMapping("/Inventario")
    public String Inventario() {
        return "Inventario";
    }

    // Módulo de Almacenes
    @GetMapping("/Almacenes")
    public String Almacenes() {
        return "Almacenes";
    }
    
    // Módulo de Clientes
    @GetMapping("/Clientes")
    public String Clientes() {
        return "Clientes";
    }
    
    // Módulo de Proveedores
    @GetMapping("/proveedores")
    public String proveedores() {
        return "proveedores";
    }
    
    // Módulo de Recursos Humanos
    @GetMapping("/rrhh")
    public String rrhh() {
        return "rrhh";
    }
    
    // Módulo de Logística
    @GetMapping("/logistica")
    public String logistica() {
        return "logistica";
    }
    
    // Módulo de Punto de Venta
    @GetMapping("/Punto de venta")
    public String puntoVenta() {
        return "Punto de venta";
    }
    
    // Módulo de Facturación
    @GetMapping("/facturacion")
    public String facturacion() {
        return "facturacion";
    }
    
    // Módulo de Reportes
    @GetMapping("/reportes")
    public String reportes() {
        return "reportes";
    }
    
    // Módulo de Configuración
    @GetMapping("/configuracion")
    public String configuracion() {
        return "configuracion";
    }
    
    // Página de cambiar contraseña
    @GetMapping("/cambiar-password")
    public String cambiarPassword() {
        return "cambiar-password";
    }
}