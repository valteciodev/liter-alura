package br.com.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseAPI(
        @JsonAlias("count") Integer total,
        @JsonAlias("results") List<LivroDTO> livros) {
}
