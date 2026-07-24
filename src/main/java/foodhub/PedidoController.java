package foodhub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

   @GetMapping("/mis-pedidos")
public String misPedidos(
        @RequestParam(name = "filtro", defaultValue = "hoy") String filtro,
        HttpSession session,
        Model model,
        HttpServletResponse response) { // 🌟 NUEVO PARÁMETRO

    // 🚫 Deshabilitar caché para evitar que las flechas del navegador muestren la página congelada
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Usuario usuario = (Usuario) session.getAttribute("usuario");

    // Si no hay usuario o si el usuario guardado no tiene un rol válido asignado
    if (usuario == null || usuario.getRol() == null) {
        return "redirect:/login";
    }

    // BIFURCACIÓN DE VISTA SI ES ADMINISTRADOR
    if ("ADMIN".equals(usuario.getRol().getNombre())) {
        List<Pedido> pedidosAdmin;
        LocalDate hoy = LocalDate.now();

        switch (filtro.toLowerCase()) {
            case "manana":
                LocalDateTime inicioManana = hoy.plusDays(1).atStartOfDay();
                LocalDateTime finManana = hoy.plusDays(1).atTime(LocalTime.MAX);
                pedidosAdmin = pedidoRepository.findByFechaBetweenOrderByFechaDesc(inicioManana, finManana);
                break;
            case "todos":
                pedidosAdmin = pedidoRepository.findAllByOrderByFechaDesc();
                break;
            case "hoy":
            default:
                LocalDateTime inicioHoy = hoy.atStartOfDay();
                LocalDateTime finHoy = hoy.atTime(LocalTime.MAX);
                pedidosAdmin = pedidoRepository.findByFechaBetweenOrderByFechaDesc(inicioHoy, finHoy);
                filtro = "hoy";
                break;
        }

        model.addAttribute("pedidos", pedidosAdmin);
        model.addAttribute("filtroActivo", filtro);
        return "admin-pedidos"; 
    }

    // FLUJO NORMAL PARA CLIENTES
    model.addAttribute("pedidos", pedidoRepository.findByUsuarioOrderByFechaDesc(usuario));
    return "mis-pedidos";
}

@GetMapping("/pedido/{id}")
public String detallePedido(
        @PathVariable Long id,
        HttpSession session,
        Model model,
        HttpServletResponse response) { // 🌟 NUEVO PARÁMETRO

    // 🚫 Deshabilitar caché también aquí
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Usuario usuario = (Usuario) session.getAttribute("usuario");

    if (usuario == null || usuario.getRol() == null) {
        return "redirect:/login";
    }

    Pedido pedido = pedidoRepository.findById(id).orElse(null);

    if (pedido == null) {
        return "redirect:/mis-pedidos";
    }

    if (!"ADMIN".equals(usuario.getRol().getNombre()) && !pedido.getUsuario().getId().equals(usuario.getId())) {
        return "redirect:/mis-pedidos";
    }

    model.addAttribute("pedido", pedido);
    return "detalle-pedido";
} 
}