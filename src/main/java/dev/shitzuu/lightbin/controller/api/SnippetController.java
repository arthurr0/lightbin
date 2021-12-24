package dev.shitzuu.lightbin.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shitzuu.lightbin.domain.Snippet;
import dev.shitzuu.lightbin.service.SnippetService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
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

    @Operation(summary = "Gets snippet by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippet has been found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Snippet is missing", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(value = "/api/v1/snippets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSnippet(@PathVariable("id") String identifier) throws JsonProcessingException {
        Optional<Snippet> optionalSnippet = snippetService.getSnippet(identifier);
        if (optionalSnippet.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                            .put("status", 404)
                            .put("identifier", identifier)
                            .put("content", "Snippet with this identifier does not exist")));
        }
        return ResponseEntity.ok(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("status", 200)
                .put("identifier", identifier)
                .put("content", objectMapper.writeValueAsString(optionalSnippet.get()))));
    }

    @Operation(summary = "Creates snippet with random id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippet has been created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Snippet is empty", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Snippet with this id already exists", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(value = "/api/v1/snippets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSnippet(@RequestBody String content) throws JsonProcessingException {
        return this.createSnippet(UUID.randomUUID().toString(), content);
    }

    @Operation(summary = "Creates snippet with specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Snippet has been created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Snippet is empty", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Snippet with this id already exists", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(value = "/api/v1/snippets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSnippet(@PathVariable("id") String identifier, @RequestBody String content) throws JsonProcessingException {
        if (bucket.tryConsume(1)) {
            if (content == null || content.isEmpty()) {
                return ResponseEntity.status(400)
                        .body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                                .put("status", 400)
                                .put("identifier", identifier)
                                .put("content", "Content cannot be empty")));
            }

            Optional<Snippet> optionalSnippet = snippetService.getSnippet(identifier);
            if (optionalSnippet.isPresent()) {
                return ResponseEntity.status(409)
                        .body(objectMapper.writeValueAsString(objectMapper.createObjectNode()
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
