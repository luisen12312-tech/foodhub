package foodhub;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

@GetMapping("/usuarios")
public String listarUsuarios(
        Model model,
        HttpSession session){

    if(!AuthUtil.esAdmin(session)){
        return "redirect:/";
    }

    model.addAttribute(
            "usuarios",
            usuarioRepository.findAll());

    return "usuarios";
}
@GetMapping("/usuarios/editar/{id}")
public String editarUsuario(
        @PathVariable Long id,
        Model model) {

    Usuario usuario =
            usuarioRepository.findById(id).orElse(null);

    model.addAttribute("usuario", usuario);

    model.addAttribute(
            "roles",
            rolRepository.findAll());

    return "editar-usuario";
}

@GetMapping("/usuarios/eliminar/{id}")
public String eliminarUsuario(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

    Usuario usuarioSesion =
            (Usuario) session.getAttribute("usuario");

    if (usuarioSesion != null &&
            usuarioSesion.getId().equals(id)) {

        redirectAttributes.addFlashAttribute(
                "error",
                "No puedes eliminar tu propia cuenta.");

        return "redirect:/usuarios";
    }

    usuarioRepository.deleteById(id);

    redirectAttributes.addFlashAttribute(
            "mensaje",
            "Usuario eliminado correctamente.");

    return "redirect:/usuarios";
}
@PostMapping("/usuarios/actualizar")
public String actualizarUsuario(

        @RequestParam Long id,
        @RequestParam String nombre,
        @RequestParam String correo,
        @RequestParam Long rolId) {

    Usuario usuario =
            usuarioRepository.findById(id).orElse(null);

    Rol rol =
            rolRepository.findById(rolId).orElse(null);

    usuario.setNombre(nombre);
    usuario.setCorreo(correo);
    usuario.setRol(rol);

    usuarioRepository.save(usuario);

    return "redirect:/usuarios";
}

    @PostMapping("/registro")
public String guardarUsuario(

        @RequestParam String nombre,
        @RequestParam String correo,
        @RequestParam String password,
        Model model) {

    nombre = nombre.trim();
    correo = correo.trim().toLowerCase();

    // Validar nombre

    if(nombre.length() < 3 || nombre.length() > 50){

        model.addAttribute(
                "error",
                "El nombre debe tener entre 3 y 50 caracteres.");

        return "registro";
    }

    if(!Pattern.matches(
            "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$",
            nombre)){

        model.addAttribute(
                "error",
                "El nombre solo puede contener letras y espacios.");

        return "registro";
    }

    // Validar correo

    if(!Pattern.matches(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            correo)){

        model.addAttribute(
                "error",
                "El correo electrónico no es válido.");

        return "registro";
    }

    if(usuarioRepository.findByCorreo(correo).isPresent()){

        model.addAttribute(
                "error",
                "Ese correo ya se encuentra registrado.");

        return "registro";
    }

    // Validar contraseña

    if(password.length() < 8){

        model.addAttribute(
                "error",
                "La contraseña debe tener mínimo 8 caracteres.");

        return "registro";
    }

    if(password.length() > 30){

        model.addAttribute(
                "error",
                "La contraseña es demasiado larga.");

        return "registro";
    }

    Usuario usuario = new Usuario();

    usuario.setNombre(nombre);

    usuario.setCorreo(correo);

    usuario.setPassword(password);

    Rol rol =
            rolRepository
            .findByNombre("USUARIO")
            .orElse(null);

    usuario.setRol(rol);

    usuarioRepository.save(usuario);

    return "redirect:/login";
}
}