package foodhub;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Historial para clientes normales
    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);

    // Filtro para Administrador: Pedidos entre dos momentos del tiempo (ideal para Hoy o Mañana)
    List<Pedido> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);

    // Filtro para Administrador: Ver absolutamente todo el historial global
    List<Pedido> findAllByOrderByFechaDesc();
}