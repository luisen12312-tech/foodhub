package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {

    @Autowired
private RestauranteRepository restauranteRepository;


   @GetMapping("/")
public String inicio(Model model, HttpSession session) {

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    model.addAttribute("usuario", usuario);

    model.addAttribute(
            "restaurantes",
            restauranteRepository.findAll());

    return "index";
}
    

    @GetMapping("/acerca")
    public String acerca(Model model, HttpSession session) {

        model.addAttribute("usuario",
                session.getAttribute("usuario"));

        return "acerca";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, HttpSession session) {

        model.addAttribute("usuario",
                session.getAttribute("usuario"));

        return "contacto";
    }

 @GetMapping("/explorar/{id}")
public String verMenuRestaurante(
        @PathVariable Long id,
        Model model,
        HttpSession session) {

    Restaurante restaurante =
            restauranteRepository.findById(id).orElse(null);

    model.addAttribute("restaurante", restaurante);

    model.addAttribute(
            "usuario",
            session.getAttribute("usuario"));

    return "detalle-restaurante";
}



}