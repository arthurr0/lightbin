package dev.shitzuu.lightbin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.shitzuu.lightbin.repository.SnippetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class LightbinApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void prepare() {
        snippetRepository.deleteAll();
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.invalidate();
            }
        }
    }

    @Test
    void shouldReturn404ForNonExistingSnippet() throws Exception {
        mockMvc.perform(get("/api/v1/snippets/123abc"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCreatedSnippet() throws Exception {
        mockMvc.perform(post("/api/v1/snippets/123abc").content("Hello World"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn409ForDuplicateOfSnippet() throws Exception {
        mockMvc.perform(post("/api/v1/snippets/123abc").content("Hello World"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(result -> mockMvc.perform(post("/api/v1/snippets/123abc").content("Hello World"))
                        .andDo(print())
                        .andExpect(status().isConflict()));
    }

    @Test
    void shouldReturn200ForExistingSnippetWithoutLanguage() throws Exception {
        mockMvc.perform(post("/api/v1/snippets/123abc").content("Hello World"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(result -> mockMvc.perform(get("/api/v1/snippets/123abc"))
                        .andDo(print())
                        .andExpect(status().isOk()));
    }

    @Test
    void shouldReturn200ForExistingSnippetWithLanguage() throws Exception {
        mockMvc.perform(post("/api/v1/snippets/123abc").content("Hello World"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(result -> mockMvc.perform(get("/api/v1/snippets/123abc/java"))
                        .andDo(print())
                        .andExpect(status().isOk()));
    }
}
