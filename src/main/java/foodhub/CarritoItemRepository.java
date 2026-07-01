package foodhub;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional; 

public interface CarritoItemRepository
        extends JpaRepository<CarritoItem, Long>{

    List<CarritoItem> findByUsuario(Usuario usuario);

    Optional<CarritoItem> findByUsuarioAndProducto(
            Usuario usuario,
            Producto producto);

    Optional<CarritoItem> findByIdAndUsuario(
            Long id,
            Usuario usuario);

    @Transactional
    void deleteByIdAndUsuario(
            Long id,
            Usuario usuario);

    @Transactional 
    void deleteByUsuario(Usuario usuario);

}