package com.tienda.inventario.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.Model.MovimientoInventario;
import com.tienda.inventario.Repository.MovimientoRepository;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "http://localhost:5173")
public class MovimientoController {
    @Autowired
    private MovimientoRepository movimientoRepository;

    @GetMapping("/reporte")
    public ResponseEntity<List<MovimientoInventario>> obtenerReporte(
        @RequestParam Long productoId, 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(23, 59, 59);

            List<MovimientoInventario> reporte = movimientoRepository
                .findByProductoIdAndFechaBetweenOrderByFechaDesc(productoId, inicio, fin);
            
            if (reporte.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reporte);
        }
    
}
