package com.tienda.inventario.Repository;

import com.tienda.inventario.Model.MovimientoInventario;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProductoIdAndFechaBetweenOrderByFechaDesc(Long productoId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
