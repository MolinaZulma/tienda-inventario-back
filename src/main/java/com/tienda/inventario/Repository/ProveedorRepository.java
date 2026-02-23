package com.tienda.inventario.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.inventario.Model.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long>{
    
}
