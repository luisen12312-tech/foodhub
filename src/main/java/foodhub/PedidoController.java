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

import jakarta.servlet.http.HttpSession;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/mis-pedidos")
    public String misPedidos(
            @RequestParam(name = "filtro", defaultValue = "hoy") String filtro,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        // BIFURCACIÓN DE VISTA SI ES ADMINISTRADOR
        if (usuario.getRol() != null && "ADMIN".equals(usuario.getRol().getNombre())) {
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
                    filtro = "hoy"; // Forzar valor por defecto
                    break;
            }

            model.addAttribute("pedidos", pedidosAdmin);
            model.addAttribute("filtroActivo", filtro);
            return "admin-pedidos"; // Renderiza la nueva plantilla de control de despacho
        }

        // FLUJO NORMAL PARA CLIENTES
        model.addAttribute("pedidos", pedidoRepository.findByUsuarioOrderByFechaDesc(usuario));
        return "mis-pedidos";
    }

    @GetMapping("/pedido/{id}")
    public String detallePedido(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            return "redirect:/mis-pedidos";
        }

        // Si es admin, puede ver cualquier pedido; si es cliente, se valida que sea suyo
        if (!"ADMIN".equals(usuario.getRol().getNombre()) && !pedido.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/mis-pedidos";
        }

        model.addAttribute("pedido", pedido);
        return "detalle-pedido";
    }
}