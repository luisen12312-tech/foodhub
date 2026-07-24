package foodhub;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // 👈 Importante para los mensajes flotantes
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class CarritoController {

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @GetMapping("/carrito/agregar/{id}")
    public String agregarAlCarrito(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) { // 👈 Agregado

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto != null) {
            CarritoItem item = carritoItemRepository
                .findByUsuarioAndProducto(usuario, producto)
                .orElse(null);

            if (item != null) {
                // 🛡️ Validar límite de 10 platillos al agregar desde fuera
                if (item.getCantidad() >= 10) {
                    redirectAttributes.addFlashAttribute("error", "No puedes agregar más de 10 unidades de este platillo.");
                    return "redirect:/restaurantes/" + producto.getRestaurante().getId();
                }
                item.setCantidad(item.getCantidad() + 1);
            } else {
                item = new CarritoItem();
                item.setProducto(producto);
                item.setCantidad(1);
                item.setUsuario(usuario);
            }

            carritoItemRepository.save(item);

            return "redirect:/restaurantes/" + producto.getRestaurante().getId();
        }

        return "redirect:/restaurantes";
    }

    @GetMapping("/carrito")
    public String verCarrito(
            Model model,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        var items = carritoItemRepository.findByUsuario(usuario);

        double total = 0;
        int totalProductos = 0;

        for (CarritoItem item : items) {
            total += item.getProducto().getPrecio() * item.getCantidad();
            totalProductos += item.getCantidad();
        }

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("totalProductos", totalProductos);

        return "carrito"; 
    }

    @GetMapping("/carrito/aumentar/{id}")
    public String aumentarCantidad(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) { // 👈 Agregado

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        CarritoItem item = carritoItemRepository
                .findByIdAndUsuario(id, usuario)
                .orElse(null);

        if (item != null) {
            // 🛡️ Límite máximo de 10 platillos
            if (item.getCantidad() >= 10) {
                redirectAttributes.addFlashAttribute("error", "Límite alcanzado: solo puedes pedir un máximo de 10 platillos de " + item.getProducto().getNombre() + ".");
            } else {
                item.setCantidad(item.getCantidad() + 1);
                carritoItemRepository.save(item);
            }
        }

        return "redirect:/carrito";
    }

    // 🛡️ NUEVO MÉTODO: Maneja de forma segura el decremento sin llegar a 0 o negativos
    @GetMapping("/carrito/disminuir/{id}")
    public String disminuirCantidad(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        CarritoItem item = carritoItemRepository
                .findByIdAndUsuario(id, usuario)
                .orElse(null);

        if (item != null) {
            // 🛡️ Evitar números menores a 1
            if (item.getCantidad() <= 1) {
                redirectAttributes.addFlashAttribute("error", "La cantidad mínima permitida es 1. Si no deseas el producto, usa el botón de eliminar.");
            } else {
                item.setCantidad(item.getCantidad() - 1);
                carritoItemRepository.save(item);
            }
        }

        return "redirect:/carrito";
    }

    @GetMapping("/carrito/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id){
        carritoItemRepository.deleteById(id);
        return "redirect:/carrito";
    }

    @GetMapping("/carrito/vaciar")
    public String vaciarCarrito(HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        carritoItemRepository.deleteByUsuario(usuario);
        return "redirect:/carrito";
    }

    @GetMapping("/carrito/confirmar")
    public String confirmarPedido(HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        var items = carritoItemRepository.findByUsuario(usuario);

        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("RECIBIDO");

        double total = 0;
        for (CarritoItem item : items) {
            total += item.getProducto().getPrecio() * item.getCantidad();
        }
        pedido.setTotal(total);

        pedidoRepository.save(pedido);

        for (CarritoItem item : items) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(item.getProducto().getPrecio());
            detallePedidoRepository.save(detalle);
        }

        carritoItemRepository.deleteByUsuario(usuario);
        return "redirect:/mis-pedidos";
    }
}