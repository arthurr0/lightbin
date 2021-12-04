package dev.shitzuu.lightbin.repository;

import dev.shitzuu.lightbin.domain.Snippet;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, String> {

    @Cacheable(value = "snippets", key = "#identifier")
    Snippet findSnippetByIdentifier(@NonNull String identifier);
}
