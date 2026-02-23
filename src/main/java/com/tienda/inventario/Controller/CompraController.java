package com.tienda.inventario.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.Service.CompraService;


@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "http://localhost:5173")
public class CompraController {
    @Autowired
    private CompraService compraService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarFactura(@RequestBody Map<String, Object> payload) {
        try {
            Long productoId = Long.valueOf(payload.get("productoId").toString());
            Integer cantidad = Integer.valueOf(payload.get("cantidad").toString());
            Long proveedorId = Long.valueOf(payload.get("proveedorId").toString());

            compraService.registrarEntrada(productoId, cantidad, proveedorId);
            return ResponseEntity.ok(Map.of("message", "Entrada registrada en inventario"));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
        
    }
    
}
