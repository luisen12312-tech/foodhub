package foodhub;// Si decides no crear la carpeta dto, cámbialo a: package foodhub;

import java.util.List;

public class ChatRequest {
    private String message;
    private List<String> contextoRestaurantes; // Aquí llegarán los nombres de los restaurantes del inicio
    private String paginaActual;       // Aquí llegará si está en "Inicio", "Explorar", etc.

    // Constructor vacío (Obligatorio para que Spring Boot procese el JSON)
    public ChatRequest() {
    }

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getContextoRestaurantes() {
        return contextoRestaurantes;
    }

    public void setContextoRestaurantes(List<String> contextoRestaurantes) {
        this.contextoRestaurantes = contextoRestaurantes;
    }

    public String getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(String paginaActual) {
        this.paginaActual = paginaActual;
    }
}