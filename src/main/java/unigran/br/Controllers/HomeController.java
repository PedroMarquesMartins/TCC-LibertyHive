package unigran.br.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//Classe responsável somente por redirecionamento para o login caso o user digite localhost:8080
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }
}