package dev.shitzuu.lightbin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shitzuu.lightbin.domain.Snippet;
import dev.shitzuu.lightbin.service.SnippetService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class SnippetController {

    private final SnippetService snippetService;
    private final ObjectMapper objectMapper;
    private final Bucket bucket;

    @Autowired
    public SnippetController(@NonNull SnippetService snippetService, @NonNull ObjectMapper objectMapper) {
        this.snippetService = snippetService;
        this.objectMapper = objectMapper;
        this.bucket = Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofSeconds(15))))
                .build();
    }

    @GetMapping(value = { "/api/v1/snippets/{id}", "/snippet/{id}" })
    public Object getSnippet(@PathVariable("id") String identifier) {
        return this.getSnippet(identifier, "plaintext");
    }

    @GetMapping(value = { "/api/v1/snippets/{id}/{language}", "/snippet/{id}/{language}" })
    public Object getSnippet(@PathVariable("id") String identifier, @PathVariable("language") String language) {
        Optional<Snippet> optionalSnippet = snippetService.getSnippet(identifier);
        if (optionalSnippet.isEmpty()) {
            try {
                return ResponseEntity.status(404)
                        .body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                                .put("status", 404)
                                .put("identifier", identifier)
                                .put("content", "Snippet with this identifier does not exist")));
            } catch (JsonProcessingException exception) {
                return ResponseEntity.status(500).build();
            }
        }
        return new ModelAndView("snippet", Map.of("snippet", optionalSnippet.get(), "language", language));
    }

    @PostMapping(value = "/api/v1/snippets/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSnippet(@RequestBody String content) throws JsonProcessingException {
        return this.createSnippet(UUID.randomUUID().toString(), content);
    }

    @PostMapping(value = "/api/v1/snippets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSnippet(@PathVariable("id") String identifier, @RequestBody String content) throws JsonProcessingException {
        if (bucket.tryConsume(1)) {
            if (content == null || content.isEmpty()) {
                return ResponseEntity.status(400).body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                        .put("status", 400)
                        .put("identifier", identifier)
                        .put("content", "Content cannot be empty")));
            }

            Optional<Snippet> optionalSnippet = snippetService.getSnippet(identifier);
            if (optionalSnippet.isPresent()) {
                return ResponseEntity.status(409).body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                        .put("status", 409)
                        .put("identifier", identifier)
                        .put("content", "Snippet with this identifier already exists")));
            }

            Snippet snippet = new Snippet();
            snippet.setIdentifier(identifier);
            snippet.setContent(content);
            snippet.setCreatedAt(Instant.now());

            snippetService.saveSnippet(snippet);

            return ResponseEntity.ok(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                    .put("status", 200)
                    .put("identifier", identifier)
                    .put("content", "Snippet created successfully")));
        }

        return ResponseEntity.status(429)
                .body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                        .put("status", 429)
                        .put("identifier", identifier)
                        .put("content", "Too many requests")));
    }
}
