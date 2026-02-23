package com.tienda.inventario.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.Service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/stockCritico")
    public ResponseEntity<List<String>> obtenerAlertasActuales() {
        List<String> alertas = new ArrayList<>();
        return ResponseEntity.ok(alertas);
    }
    }
    
