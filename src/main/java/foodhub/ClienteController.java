package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class ClienteController {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @GetMapping("/explorar")
    public String explorarRestaurantes(
            Model model,
            HttpSession session) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuario");

        model.addAttribute("usuario", usuario);

        model.addAttribute(
                "restaurantes",
                restauranteRepository.findAll());

        return "explorar-restaurantes";
    }

}