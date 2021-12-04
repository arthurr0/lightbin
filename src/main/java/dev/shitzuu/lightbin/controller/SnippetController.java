package dev.shitzuu.lightbin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shitzuu.lightbin.domain.Snippet;
import dev.shitzuu.lightbin.service.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
public class SnippetController {

    private final SnippetService snippetService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SnippetController(@NonNull SnippetService snippetService, @NonNull ObjectMapper objectMapper) {
        this.snippetService = snippetService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}")
    public Object getSnippet(@PathVariable("id") String identifier) {
        return this.getSnippet(identifier, "plaintext");
    }

    @GetMapping("/{id}/{language}")
    public Object getSnippet(@PathVariable("id") String identifier, @PathVariable("language") String language) {
        Optional<Snippet> optionalSnippet = snippetService.getSnippet(identifier);
        if (optionalSnippet.isEmpty()) {
            try {
                return ResponseEntity.status(404)
                        .body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                                .put("status", 404)
                                .put("content", "Snippet with this identifier does not exist")));
            } catch (JsonProcessingException exception) {
                return ResponseEntity.status(500).build();
            }
        }
        return new ModelAndView("snippet", Map.of("snippet", optionalSnippet.get(), "language", language));
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> createSnippet(@PathVariable("id") String identifier, @RequestBody String content) throws JsonProcessingException {
        if (content == null || content.isEmpty()) {
            return ResponseEntity.status(400).body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                    .put("status", 400)
                    .put("content", "Content cannot be empty")));
        }

        Optional<Snippet> optionalSnippet = snippetService.getSnippet(identifier);
        if (optionalSnippet.isPresent()) {
            return ResponseEntity.status(409).body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                    .put("status", 409)
                    .put("content", "Snippet with this identifier already exists")));
        }

        Snippet snippet = new Snippet();
        snippet.setIdentifier(identifier);
        snippet.setContent(content);
        snippet.setCreatedAt(Instant.now());

        snippetService.saveSnippet(snippet);

        return ResponseEntity.ok(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("status", 200)
                .put("content", "Snippet created successfully")));
    }
}
