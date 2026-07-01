package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDateTime;

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
        HttpSession session) {

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if (usuario == null) {
        return "redirect:/login";
    }

    Producto producto =
            productoRepository.findById(id).orElse(null);

    if (producto != null) {
CarritoItem item =
        carritoItemRepository
        .findByUsuarioAndProducto(usuario, producto)
        .orElse(null);

if(item != null){

    item.setCantidad(item.getCantidad() + 1);

}else{

    item = new CarritoItem();

    item.setProducto(producto);
    item.setCantidad(1);
    item.setUsuario(usuario);

}

carritoItemRepository.save(item);

        return "redirect:/restaurantes/" +
                producto.getRestaurante().getId();
    }

    return "redirect:/restaurantes";
}

 @GetMapping("/carrito")
public String verCarrito(
        Model model,
        HttpSession session) {

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if (usuario == null) {
        return "redirect:/login";
    }

    var items = carritoItemRepository.findByUsuario(usuario);

    double total = 0;
    int totalProductos = 0;

    for (CarritoItem item : items) {

        total += item.getProducto().getPrecio()
                * item.getCantidad();

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
        HttpSession session){

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if(usuario==null){

        return "redirect:/login";

    }

    CarritoItem item =
            carritoItemRepository
            .findByIdAndUsuario(id,usuario)
            .orElse(null);

    if(item!=null){

        item.setCantidad(item.getCantidad()+1);

        carritoItemRepository.save(item);

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

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if(usuario == null){
        return "redirect:/login";
    }

    carritoItemRepository.deleteByUsuario(usuario);

    return "redirect:/carrito";

}

@GetMapping("/carrito/confirmar")
public String confirmarPedido(HttpSession session){

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if(usuario == null){
        return "redirect:/login";
    }

    var items =
            carritoItemRepository.findByUsuario(usuario);

    if(items.isEmpty()){
        return "redirect:/carrito";
    }

    Pedido pedido = new Pedido();

    pedido.setUsuario(usuario);
    pedido.setFecha(LocalDateTime.now());
    pedido.setEstado("RECIBIDO");

    double total = 0;

    for(CarritoItem item : items){

        total += item.getProducto().getPrecio()
                * item.getCantidad();

    }

    pedido.setTotal(total);

    pedidoRepository.save(pedido);

    for(CarritoItem item : items){

        DetallePedido detalle =
                new DetallePedido();

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