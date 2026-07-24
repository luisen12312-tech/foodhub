package foodhub;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // 🔍 Filtra automáticamente los productos que pertenecen al ID de un restaurante específico
    List<Producto> findByRestauranteId(Long restauranteId);
}