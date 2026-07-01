package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class PanelController {

       @Autowired
    private RestauranteRepository restauranteRepository;
@Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

 @GetMapping("/panel")
public String panel(HttpSession session, Model model) {

    if (!AuthUtil.esAdmin(session)) {
        return "redirect:/";
    }

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    model.addAttribute("usuario", usuario);

    model.addAttribute(
            "totalUsuarios",
            usuarioRepository.count());

    model.addAttribute(
            "totalRestaurantes",
            restauranteRepository.count());

    model.addAttribute(
            "totalProductos",
            productoRepository.count());

    return "panel";
}
}