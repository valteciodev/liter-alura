package br.com.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO (Data Transfer Object) para representar dados de livros da API Gutendx
 * Record imutável que mapeia os campos JSON para propriedades Java
 * Usado para transferência de dados entre a API externa e a aplicação
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LivroDTO(
        /** Título do livro - mapeado do campo "title" do JSON */
        @JsonAlias("title") String titulo,

        /** Lista de autores do livro - mapeado do campo "authors" do JSON */
        @JsonAlias("authors") List<AutorDTO> autores,

        /** Lista de códigos de idiomas - mapeado do campo "languages" do JSON */
        @JsonAlias("languages") List<String> idiomas,

        /** Número total de downloads - mapeado do campo "download_count" do JSON */
        @JsonAlias("download_count") Double numeroDownloads,

        /** Lista de resumos do livro - mapeado do campo "summaries" do JSON */
        @JsonAlias("summaries") List<String> resumo
) {

    /**
     * Método toString customizado para exibição formatada do livro
     * Formata informações dos autores incluindo período de vida
     * @return String formatada com todas as informações do livro
     */
    @Override
    public String toString() {
        // Formata lista de autores com período de vida
        String autoresFormatados = formatarAutores();

        // Formata lista de idiomas
        String idiomasFormatados = idiomas != null && !idiomas.isEmpty() ?
            String.join(", ", idiomas) : "Não especificado";

        return """
                📖 ========== LIVRO ==========
                📚 Título: %s
                👥 Autores: %s
                🌐 Idiomas: %s
                📊 Downloads: %.0f
                =============================""".formatted(
                titulo != null ? titulo : "Não informado",
                autoresFormatados,
                idiomasFormatados,
                numeroDownloads != null ? numeroDownloads : 0.0
        );
    }

    /**
     * Formata a lista de autores com informações de período de vida
     * @return String formatada com autores e seus períodos de vida
     */
    private String formatarAutores() {
        if (autores == null || autores.isEmpty()) {
            return "Nenhum autor disponível";
        }

        return autores.stream()
                .map(autor -> {
                    String falecimento = autor.anoFalecimento() != null ?
                        autor.anoFalecimento().toString() : "presente";
                    String nascimento = autor.anoNascimento() != null ?
                        autor.anoNascimento().toString() : "?";

                    return String.format("%s (%s - %s)",
                        autor.nome(), nascimento, falecimento);
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * Retorna o primeiro resumo disponível, se existir
     * @return Primeiro resumo ou mensagem indicando ausência
     */
    public String getPrimeiroResumo() {
        return resumo != null && !resumo.isEmpty() ?
            resumo.get(0) : "Resumo não disponível";
    }

    /**
     * Verifica se o livro tem autores cadastrados
     * @return true se tem pelo menos um autor
     */
    public boolean temAutores() {
        return autores != null && !autores.isEmpty();
    }

    /**
     * Conta o número total de autores
     * @return Número de autores ou 0 se lista for nula
     */
    public int numeroDeAutores() {
        return autores != null ? autores.size() : 0;
    }
}
