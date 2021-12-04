package dev.shitzuu.lightbin.service;

import dev.shitzuu.lightbin.domain.Snippet;
import dev.shitzuu.lightbin.repository.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SnippetService {

    private final SnippetRepository snippetRepository;

    @Autowired
    public SnippetService(@NonNull SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    /**
     * Finds the snippet by id.
     *
     * @param identifier identifier of the snippet
     *
     * @return optional snippet
     */
    public Optional<Snippet> getSnippet(@NonNull String identifier) {
        return Optional.ofNullable(snippetRepository.findSnippetByIdentifier(identifier));
    }

    /**
     * Saves the snippet.
     *
     * @param snippet snippet to save
     */
    @CacheEvict(value = "snippets", key = "#snippet.identifier")
    public void saveSnippet(@NonNull Snippet snippet) {
        snippetRepository.save(snippet);
    }
}
