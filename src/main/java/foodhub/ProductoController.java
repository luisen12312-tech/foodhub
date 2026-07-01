package foodhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

 @Autowired
private RestauranteRepository restauranteRepository;

   @GetMapping("/productos")
public String listarProductos(
        Model model,
        HttpSession session){

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if(usuario == null){
        return "redirect:/login";
    }

    if(!usuario.getRol().getNombre().equals("ADMIN")){
        return "redirect:/";
    }

    model.addAttribute(
            "productos",
            productoRepository.findAll());

    model.addAttribute(
            "usuario",
            usuario);

    return "productos";
}
@GetMapping("/productos/nuevo")
public String nuevoProducto(
        Model model,
        HttpSession session){

    Usuario usuario =
            (Usuario) session.getAttribute("usuario");

    if(usuario == null){
        return "redirect:/login";
    }

    if(!usuario.getRol().getNombre().equals("ADMIN")){
        return "redirect:/";
    }

    model.addAttribute(
            "restaurantes",
            restauranteRepository.findAll());

    return "nuevo-producto";
}

@GetMapping("/productos/editar/{id}")
public String editarProducto(@PathVariable Long id, Model model){

    Producto producto =
            productoRepository.findById(id).orElse(null);

    model.addAttribute("producto", producto);

    model.addAttribute(
            "restaurantes",
            restauranteRepository.findAll());

    return "editar-producto";
}

@PostMapping("/productos/actualizar")
public String actualizarProducto(

        @RequestParam Long id,
        @RequestParam String nombre,
        @RequestParam String descripcion,
        @RequestParam Double precio,
        @RequestParam String imagen,
        @RequestParam Long restauranteId){

    Producto producto =
            productoRepository.findById(id).orElse(null);

    Restaurante restaurante =
            restauranteRepository.findById(restauranteId).orElse(null);

    producto.setNombre(nombre);
    producto.setDescripcion(descripcion);
    producto.setPrecio(precio);
    producto.setImagen(imagen);
    producto.setRestaurante(restaurante);

    productoRepository.save(producto);

    return "redirect:/productos";
}

@PostMapping("/productos/guardar")
public String guardarProducto(

        @RequestParam String nombre,
        @RequestParam String descripcion,
        @RequestParam Double precio,
        @RequestParam String imagen,
        @RequestParam Long restauranteId){

    Restaurante restaurante =
            restauranteRepository.findById(restauranteId).orElse(null);

    Producto producto = new Producto();

    producto.setNombre(nombre);
    producto.setDescripcion(descripcion);
    producto.setPrecio(precio);
    producto.setImagen(imagen);
    producto.setRestaurante(restaurante);

    productoRepository.save(producto);

    return "redirect:/productos";
}

@GetMapping("/productos/eliminar/{id}")
public String eliminarProducto(@PathVariable Long id){

    productoRepository.deleteById(id);

    return "redirect:/productos";
}

@GetMapping("/productos/{id}")
public String verProducto(@PathVariable Long id, Model model){

    Producto producto =
            productoRepository.findById(id).orElse(null);

    model.addAttribute("producto", producto);

    return "detalle-producto";
}


}
