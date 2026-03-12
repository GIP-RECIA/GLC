package fr.recia.glc.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
Controlleur utilisé pour rediriger vers /ui/index.html afin de servir le front sur / et /ui
 */
@Controller
public class StaticRedirectionController {

    @GetMapping("/")
    public String root() {
        return "redirect:/ui/";
    }

    @GetMapping("/ui")
    public String uiNoSlash() {
        return "redirect:/ui/";
    }

    @GetMapping("/ui/")
    public String uiRoot() {
        return "forward:/ui/index.html";
    }
}