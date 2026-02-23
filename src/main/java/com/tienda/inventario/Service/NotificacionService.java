package com.tienda.inventario.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tienda.inventario.Model.Producto;
import com.tienda.inventario.Repository.ProductoRepository;

@Service
public class NotificacionService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<String> obtenerAlertasActuales() {
        List<String> alertas = new ArrayList<>();
        List<Producto> productos = productoRepository.findAll();

        for (Producto producto : productos) {
            Integer stock = (producto.getStock() != null) ? producto.getStock() : 0;
            Integer min = (producto.getStockMinimo() != null) ? producto.getStockMinimo() : 0;

            if (stock <= min) {
                alertas.add("Stock bajo, Producto: " + producto.getNombre() +
                           ", Stock actual: " + stock + ", Mínimo: " + min);
            }
        }
        return alertas;
    }


    @Scheduled(fixedRate = 60000)
    public void verificarStockCritico() {
        List<String> alertas = obtenerAlertasActuales();
        if (alertas.isEmpty()) {
            System.out.println("Sincronización de stock: Todo normal.");
        } else {
            alertas.forEach(System.out::println);
        }
    }
}
