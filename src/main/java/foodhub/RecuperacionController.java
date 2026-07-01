package foodhub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecuperacionController {

    @GetMapping("/recuperar-password")
    public String recuperarPassword(){

        return "recuperar-password";

    }

}