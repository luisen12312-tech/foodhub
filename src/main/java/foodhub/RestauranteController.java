package foodhub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class RestauranteController {

    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProductoRepository productoRepository;

  @GetMapping("/restaurantes")
public String listarRestaurantes(
        Model model,
        HttpSession session) {

    if (!AuthUtil.esAdmin(session)) {
        return "redirect:/";
    }

    model.addAttribute(
            "restaurantes",
            restauranteRepository.findAll());

    model.addAttribute(
            "usuario",
            session.getAttribute("usuario"));

    return "restaurantes";
}
@GetMapping("/restaurantes/editar/{id}")
public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    Restaurante restaurante = restauranteRepository.findById(id).orElse(null);
    
    if (restaurante == null) {
        return "redirect:/restaurantes"; // Si el ID no existe, vuelve al listado
    }
    
    model.addAttribute("restaurante", restaurante);
    return "editar-restaurante"; // Abre el HTML de edición
}

   @GetMapping("/restaurantes/nuevo")
public String nuevoRestaurante(HttpSession session){

    if(!AuthUtil.esAdmin(session)){
        return "redirect:/";
    }

    return "nuevo-restaurante";
}

 @PostMapping("/restaurantes/guardar")
public String guardarRestaurante(
        HttpSession session,
        @RequestParam String nombre,
        @RequestParam String direccion,
        @RequestParam String telefono,
        @RequestParam String descripcion,
        @RequestParam String imagen,
        @RequestParam(required = false) Double latitud,   // 📍 Captura Latitud
        @RequestParam(required = false) Double longitud) { // 📍 Captura Longitud

    if(!AuthUtil.esAdmin(session)){
        return "redirect:/";
    }

    Restaurante restaurante = new Restaurante();
    restaurante.setNombre(nombre);
    restaurante.setDireccion(direccion);
    restaurante.setTelefono(telefono);
    restaurante.setDescripcion(descripcion);
    restaurante.setImagen(imagen);
    restaurante.setLatitud(latitud);   // 📍 Guarda en BD
    restaurante.setLongitud(longitud); // 📍 Guarda en BD

    restauranteRepository.save(restaurante);

    return "redirect:/restaurantes";
}

@PostMapping("/restaurantes/actualizar")
public String actualizarRestaurante(
        @RequestParam Long id,
        @RequestParam String nombre,
        @RequestParam String direccion,
        @RequestParam String telefono,
        @RequestParam String descripcion,
        @RequestParam(required = false) String imagen, // 👈 Puesto como opcional para evitar fallas 400/404
        @RequestParam(required = false) Double latitud,
        @RequestParam(required = false) Double longitud) {

    Restaurante restaurante = restauranteRepository.findById(id).orElse(null);

    if (restaurante != null) {
        restaurante.setNombre(nombre);
        restaurante.setDireccion(direccion);
        restaurante.setTelefono(telefono);
        restaurante.setDescripcion(descripcion);
        
        // Solo actualiza la imagen si el usuario mandó algo en el input
        if (imagen != null && !imagen.trim().isEmpty()) {
            restaurante.setImagen(imagen);
        }
        
        restaurante.setLatitud(latitud);
        restaurante.setLongitud(longitud);

        restauranteRepository.save(restaurante);
    }

    return "redirect:/restaurantes";
}

@GetMapping("/mapa")
public String mostrarMapa(Model model, HttpSession session) {
    List<Restaurante> listaRestaurantes = restauranteRepository.findAll();
    model.addAttribute("restaurantes", listaRestaurantes);
    model.addAttribute("usuario", session.getAttribute("usuario")); // Mantiene la sesión viva en la barra
    return "mapa"; 
}

@GetMapping("/restaurantes/eliminar/{id}")
public String eliminarRestaurante(@PathVariable Long id, HttpSession session) {
    // 🛡️ Seguridad: Si no es admin, no puede borrar y lo mandamos al inicio
    if (!AuthUtil.esAdmin(session)) {
        return "redirect:/";
    }

    // Ejecuta el borrado. Gracias al CASCADE que pusimos en la base de datos,
    // esto borrará el restaurante y todos sus platillos en cadena sin errores.
    restauranteRepository.deleteById(id);

    // Redirige limpiamente de vuelta al panel de gestión
    return "redirect:/restaurantes";
}

@GetMapping("/restaurantes/{id}")
public String verMenuRestaurante(@PathVariable Long id, Model model, HttpSession session) {
    // 1. Buscar el restaurante seleccionado
    Restaurante restaurante = restauranteRepository.findById(id).orElse(null);
    
    if (restaurante == null) {
        return "redirect:/"; // Si el restaurante no existe por algún motivo, redirige al inicio
    }
    
    // 2. Obtener solo los productos vinculados a este restaurante
    List<Producto> productosDelRestaurante = productoRepository.findByRestauranteId(id);
    
    // 3. Pasar los datos al modelo HTML
    model.addAttribute("restaurante", restaurante);
    model.addAttribute("productos", productosDelRestaurante);
    model.addAttribute("usuario", session.getAttribute("usuario")); // Mantiene la sesión viva en la barra
    
    return "detalle-restaurante"; // 👈 Aquí debes poner el nombre EXACTO de tu archivo HTML del menú (ej: menu.html)
}

}