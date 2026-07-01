package foodhub;

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
        @RequestParam String imagen){

    if(!AuthUtil.esAdmin(session)){
        return "redirect:/";
    }

    Restaurante restaurante = new Restaurante();

    restaurante.setNombre(nombre);
    restaurante.setDireccion(direccion);
    restaurante.setTelefono(telefono);
    restaurante.setDescripcion(descripcion);
    restaurante.setImagen(imagen);

    restauranteRepository.save(restaurante);

    return "redirect:/restaurantes";
}

@GetMapping("/restaurantes/eliminar/{id}")
public String eliminarRestaurante(@PathVariable Long id) {

    restauranteRepository.deleteById(id);

    return "redirect:/restaurantes";
}

@GetMapping("/restaurantes/{id}")
public String verRestaurante(
        @PathVariable Long id,
        Model model,
        HttpSession session){

    Restaurante restaurante =
            restauranteRepository.findById(id).orElse(null);

    model.addAttribute("restaurante", restaurante);

    model.addAttribute(
            "usuario",
            session.getAttribute("usuario"));

    return "detalle-restaurante";
}
@GetMapping("/restaurantes/editar/{id}")
public String editarRestaurante(
        @PathVariable Long id,
        Model model,
        HttpSession session){

    Restaurante restaurante =
            restauranteRepository.findById(id).orElse(null);

    model.addAttribute("restaurante", restaurante);

    model.addAttribute(
            "usuario",
            session.getAttribute("usuario"));

    return "editar-restaurante";
}

@PostMapping("/restaurantes/actualizar")
public String actualizarRestaurante(

        @RequestParam Long id,
        @RequestParam String nombre,
        @RequestParam String direccion,
        @RequestParam String telefono,
        @RequestParam String descripcion) {

    Restaurante restaurante =
            restauranteRepository.findById(id).orElse(null);

    if(restaurante != null){

        restaurante.setNombre(nombre);
        restaurante.setDireccion(direccion);
        restaurante.setTelefono(telefono);
        restaurante.setDescripcion(descripcion);

        restauranteRepository.save(restaurante);
    }

    return "redirect:/restaurantes";
}

}