package com.tienda.inventario.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tienda.inventario.Model.Venta;
import java.util.List;


@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>{

    @Query("SELECT v FROM Venta v WHERE YEAR(v.fecha) =:year")
    List<Venta> findByFechaYear(@Param("year") int year);

}
