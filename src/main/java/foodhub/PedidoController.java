package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/mis-pedidos")
    public String misPedidos(
            HttpSession session,
            Model model){

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if(usuario == null){
            return "redirect:/login";
        }

        // CORREGIDO: Ahora coincide exactamente con tu PedidoRepository
        model.addAttribute(
                "pedidos",
                pedidoRepository.findByUsuarioOrderByFechaDesc(usuario));

        return "mis-pedidos";
    }

    @GetMapping("/pedido/{id}")
    public String detallePedido(
            @PathVariable Long id,
            HttpSession session,
            Model model){

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if(usuario == null){
            return "redirect:/login";
        }

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if(pedido == null){
            return "redirect:/mis-pedidos";
        }

        if(!pedido.getUsuario().getId().equals(usuario.getId())){
            return "redirect:/mis-pedidos";
        }

        model.addAttribute("pedido", pedido);

        return "detalle-pedido";
    }
}