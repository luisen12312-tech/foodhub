package foodhub;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface CodigoRecuperacionRepository
        extends JpaRepository<CodigoRecuperacion, Long> {

    Optional<CodigoRecuperacion> findByCodigo(String codigo);

    Optional<CodigoRecuperacion> findByUsuario(Usuario usuario);

    @Transactional
    @Modifying
    void deleteByUsuario(Usuario usuario);

}