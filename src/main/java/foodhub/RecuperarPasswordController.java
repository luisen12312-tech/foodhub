package foodhub;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecuperarPasswordController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CodigoRecuperacionRepository codigoRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/recuperar")
    public String mostrarFormulario() {

        return "recuperar-password";
    }

    @PostMapping("/recuperar")
    public String enviarCodigo(

            @RequestParam String correo,
            Model model) {

        Usuario usuario =
                usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {

            model.addAttribute(
                    "error",
                    "No existe ninguna cuenta con ese correo.");

            return "recuperar-password";
        }

        codigoRepository
                .findByUsuario(usuario)
                .ifPresent(codigoRepository::delete);

        String codigo =
                String.format("%06d",
                        new Random().nextInt(999999));

        CodigoRecuperacion recuperar =
                new CodigoRecuperacion();

        recuperar.setCodigo(codigo);
        recuperar.setUsuario(usuario);
        recuperar.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(10));

        codigoRepository.save(recuperar);

        emailService.enviarCorreo(

                usuario.getCorreo(),

                "Recuperación de contraseña - FoodHub",

                "Hola "
                        + usuario.getNombre()
                        + "\n\n"
                        + "Tu código de recuperación es:\n\n"
                        + codigo
                        + "\n\n"
                        + "Este código es válido por 10 minutos.");

        model.addAttribute("correo", correo);

        return "verificar-codigo";
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigo(

            @RequestParam String correo,
            @RequestParam String codigo,
            Model model) {

        Usuario usuario =
                usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return "redirect:/recuperar";
        }

        CodigoRecuperacion recuperar =
                codigoRepository.findByUsuario(usuario).orElse(null);

        if (recuperar == null) {

            model.addAttribute(
                    "error",
                    "No existe un código activo.");

            model.addAttribute("correo", correo);

            return "verificar-codigo";
        }

        if (LocalDateTime.now()
                .isAfter(recuperar.getFechaExpiracion())) {

            codigoRepository.delete(recuperar);

            model.addAttribute(
                    "error",
                    "El código ha expirado.");

            model.addAttribute("correo", correo);

            return "verificar-codigo";
        }

        if (!recuperar.getCodigo().equals(codigo)) {

            model.addAttribute(
                    "error",
                    "Código incorrecto.");

            model.addAttribute("correo", correo);

            return "verificar-codigo";
        }

        model.addAttribute("correo", correo);

        return "nueva-password";
    }

    @Transactional
    @PostMapping("/guardar-password")
    public String guardarPassword(

            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String confirmar,
            Model model) {

        if (!password.equals(confirmar)) {

            model.addAttribute("correo", correo);
            model.addAttribute(
                    "error",
                    "Las contraseñas no coinciden.");

            return "nueva-password";
        }

        Usuario usuario =
                usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return "redirect:/login";
        }

        usuario.setPassword(password);

        usuarioRepository.save(usuario);

        codigoRepository
                .findByUsuario(usuario)
                .ifPresent(codigoRepository::delete);

        return "redirect:/login";
    }

}