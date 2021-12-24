package dev.shitzuu.lightbin.controller;

import dev.shitzuu.lightbin.service.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class WebController {

    private final SnippetService snippetService;

    @Autowired
    public WebController(@NonNull SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @GetMapping("/")
    public String getHomeView() {
        return "index";
    }

    @GetMapping("/snippet/{id}")
    public ModelAndView getSnippetView(@PathVariable("id") String identifier) {
        return snippetService.getSnippet(identifier)
                .map(snippet -> new ModelAndView("snippet", Map.of("snippet", snippet, "language", snippet.getLanguage())))
                .orElseGet(() -> new ModelAndView("index"));
    }

    @GetMapping("/snippet/{id}/{language}")
    public ModelAndView getSnippetView(@PathVariable("id") String identifier, @PathVariable String language) {
        return snippetService.getSnippet(identifier)
                .map(snippet -> new ModelAndView("snippet", Map.of("snippet", snippet, "language", language)))
                .orElseGet(() -> new ModelAndView("index"));
    }

    @GetMapping(value = { "docs", "documentation", "swagger" })
    public String getDocumentationView() {
        return "redirect:/swagger-ui.html";
    }
}
