package foodhub;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    // 🌟 Fuerza a traer al usuario junto con su Rol mapeado instantáneamente en la nube
    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol")
    List<Usuario> findAllConRoles();
}