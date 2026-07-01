package foodhub;

import jakarta.servlet.http.HttpSession;

public class AuthUtil {

    public static boolean haySesion(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario != null;
    }

    public static boolean esAdmin(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario != null &&
               usuario.getRol() != null &&
               usuario.getRol().getNombre().equals("ADMIN");
    }

    public static boolean esUsuario(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario != null &&
               usuario.getRol() != null &&
               usuario.getRol().getNombre().equals("USUARIO");
    }

    public static Usuario getUsuario(HttpSession session) {

        return (Usuario) session.getAttribute("usuario");
    }

}