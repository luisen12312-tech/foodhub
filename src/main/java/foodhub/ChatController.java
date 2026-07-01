package foodhub;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> procesarChat(@RequestBody ChatRequest request) {
        // 1. Limpiamos el mensaje: pasamos a minúsculas, quitamos espacios y eliminamos comillas
        String mensajeUsuario = request.getMessage().toLowerCase().trim().replace("\"", "").replace("'", "");
        String paginaActual = request.getPaginaActual();
        
        String respuestaBot = "¡Hola! Soy tu asistente de GourmetRoots. No estoy muy seguro de cómo responder a eso, pero puedes intentar preguntarme sobre cómo iniciar sesión, ver restaurantes o conocer más sobre nosotros.";

        // --- LÓGICA DE RESPUESTAS PERSONALIZADAS ---

        // Palabras clave para Inicio de Sesión / Login
        if (mensajeUsuario.contains("inicio") || mensajeUsuario.contains("sesion") || mensajeUsuario.contains("login") || mensajeUsuario.contains("entrar")) {
            respuestaBot = "¡Claro! Para iniciar sesión, dirígete a la parte superior derecha de la barra de navegación y haz clic en **'Iniciar sesión'**. Allí podrás ingresar tu correo electrónico y contraseña en el formulario.";
        } 
        // Palabras clave para Restaurantes, Comida o Búsqueda
        else if (mensajeUsuario.contains("restaurante") || mensajeUsuario.contains("explorar") || mensajeUsuario.contains("comida") || mensajeUsuario.contains("buscar")) {
            if (request.getContextoRestaurantes() != null && !request.getContextoRestaurantes().isEmpty()) {
                respuestaBot = "Actualmente en tu pantalla estás viendo estos increíbles restaurantes: " + 
                               String.join(", ", request.getContextoRestaurantes()) + ". ¡Haz clic en cualquiera para ver su menú!";
            } else {
                respuestaBot = "Puedes hacer clic en el botón naranja **'Explorar Restaurantes'** en el inicio o ir a la sección de **Restaurantes** en el menú de arriba para buscar tus platillos favoritos.";
            }
        } 
        // Palabras clave para la sección Acerca de
        else if (mensajeUsuario.contains("acerca") || mensajeUsuario.contains("quienes somos") || mensajeUsuario.contains("gourmetroots")) {
            respuestaBot = "En **GourmetRoots** nos apasiona conectar a los amantes del buen comer con los mejores sabores locales. Nacimos con la idea de simplificar tus comidas diarias a un solo clic de distancia.";
        } 
        // Saludos
        else if (mensajeUsuario.contains("hola") || mensajeUsuario.contains("buenos dias") || mensajeUsuario.contains("buenas tardes")) {
            respuestaBot = "¡Hola! Bienvenido de nuevo a GourmetRoots. ¿En qué te puedo colaborar hoy? Puedes preguntarme por el inicio de sesión, los restaurantes disponibles o sobre nuestra historia.";
        }

        // Preparamos la respuesta en formato JSON para el frontend
        Map<String, String> response = new HashMap<>();
        response.put("response", respuestaBot);

        return ResponseEntity.ok(response);
    }
}