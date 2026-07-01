package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {

        // Si ya inició sesión, redirigir según el rol
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null) {

            if (usuario.getRol() != null &&
                    "ADMIN".equals(usuario.getRol().getNombre())) {

                return "redirect:/panel";
            }

            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Usuario usuario =
                usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {

            model.addAttribute("error",
                    "Correo no encontrado.");

            return "login";
        }

        if (!usuario.getPassword().equals(password)) {

            model.addAttribute("error",
                    "Contraseña incorrecta.");

            return "login";
        }

        session.setAttribute("usuario", usuario);

        if (usuario.getRol() != null) {

            if ("ADMIN".equals(usuario.getRol().getNombre())) {

                return "redirect:/panel";

            } else if ("USUARIO".equals(usuario.getRol().getNombre())) {

                return "redirect:/";
            }
        }

        model.addAttribute("error",
                "El usuario no tiene un rol válido.");

        session.invalidate();

        return "login";
    }
}