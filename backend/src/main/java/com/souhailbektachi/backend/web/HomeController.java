package com.souhailbektachi.backend.web;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden // Hide this controller from Swagger documentation
public class HomeController {

    @GetMapping("/")
    public String redirectToSwaggerUi() {
        // Redirect to the standard Swagger UI path
        return "redirect:/swagger-ui.html";
    }
}
