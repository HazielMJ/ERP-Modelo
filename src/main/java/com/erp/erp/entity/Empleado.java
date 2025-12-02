    private String nombre;
    
    @Column(name = "apellido", length = 100)
    private String apellido;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;
    
    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;
    
    @Column(name = "rfc", length = 13)
    private String rfc;
    
    @Column(name = "curp", length = 18)
    private String curp;
    
    @Column(name = "nss", length = 11)
    private String nss;
    
    @Column(name = "cp", length = 10)
    private String cp;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "correo", length = 100)
    private String correo;
    
    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;
    
    @Column(name = "fecha_termino")
    private LocalDate fechaTermino;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puesto_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Puesto puesto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Departamento departamento;
    
    @Column(name = "salario_base", precision = 12, scale = 2)
    private BigDecimal salarioBase;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contrato")
    private TipoContrato tipoContrato;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "jornada", columnDefinition = "ENUM('COMPLETA','PARCIAL','POR_HORAS') DEFAULT 'COMPLETA'")
    private Jornada jornada = Jornada.COMPLETA;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('ACTIVO','INACTIVO','BAJA') DEFAULT 'ACTIVO'")
    private EstadoEmpleado estado = EstadoEmpleado.ACTIVO;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", columnDefinition = "ENUM('ADMIN','GERENTE','VENDEDOR','CONTADOR','ALMACENISTA','RRHH','EMPLEADO') DEFAULT 'EMPLEADO'")
    private RolEmpleado rol = RolEmpleado.EMPLEADO;
    
    
    public enum TipoDocumento {
        DNI, CURP, RFC, PASAPORTE, OTRO
    }
    
    public enum Genero {
        M, F, OTRO
    }
    
    public enum TipoContrato {
        INDEFINIDO, TEMPORAL, PRACTICAS, HONORARIOS
    }
    
    public enum Jornada {
        COMPLETA, PARCIAL, POR_HORAS
    }
    
    public enum EstadoEmpleado {
        ACTIVO, INACTIVO, BAJA
    }
    
    public enum RolEmpleado {
        ADMIN,         
        GERENTE,    
        VENDEDOR,    
        CONTADOR,     
        ALMACENISTA,   
        RRHH,          
        EMPLEADO       
    }
    

    public String getNombreCompleto() {
        return nombre + (apellido != null ? " " + apellido : "");
    }
}
