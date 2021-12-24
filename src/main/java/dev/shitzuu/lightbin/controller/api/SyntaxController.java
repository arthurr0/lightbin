package dev.shitzuu.lightbin.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SyntaxController {

    private final ObjectMapper objectMapper;

    @Autowired
    public SyntaxController(@NonNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // https://github.com/highlightjs/highlight.js/blob/main/SUPPORTED_LANGUAGES.md
    @Operation(summary = "Gets list of supported syntaxes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets list of supported syntaxes", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/api/v1/syntaxes")
    public ResponseEntity<String> getAvailableSyntaxes() throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(objectMapper.createArrayNode()
                .add("aspectj")
                .add("bash")
                .add("basic")
                .add("csharp")
                .add("c")
                .add("h")
                .add("cpp")
                .add("hpp")
                .add("css")
                .add("clojure")
                .add("coffeescript")
                .add("docker")
                .add("dockerfile")
                .add("bat")
                .add("cmd")
                .add("groovy")
                .add("handlebars")
                .add("json")
                .add("java")
                .add("javascript")
                .add("js")
                .add("julia")
                .add("kotlin")
                .add("kt")
                .add("lua")
                .add("makefile")
                .add("nginx")
                .add("nginxconf")
                .add("powershell")
                .add("ps")
                .add("scss")
                .add("sql")
                .add("svelte")
                .add("typescript")
                .add("ts")
                .add("vala")
                .add("yml")
                .add("yaml")
                .add("markdown")
                .add("md")
                .add("properties")));
    }
}
