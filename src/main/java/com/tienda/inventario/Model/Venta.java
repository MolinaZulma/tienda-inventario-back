package com.tienda.inventario.Model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Data
@Table(name = "ventas")

public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFactura;
    private LocalDateTime fecha = LocalDateTime.now();
    private Double total;

    //Muchos registros de venta pueden ser de un solo cliente
     @ManyToOne 
     @JoinColumn(name = "cliente_id")
     private Cliente cliente;

     @ManyToOne
     @JoinColumn(name = "usuario_id")
     private Usuario usuario;

     //relación con los detalles
     @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
     private List<DetalleVenta> detalles;

    
}
