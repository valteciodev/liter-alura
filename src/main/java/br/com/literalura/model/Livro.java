package br.com.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade JPA que representa um livro no banco de dados
 * Contém relacionamento many-to-many com autores e armazena metadados do livro
 */
@Entity
@Getter
@Setter
@Table(name = "livros")
public class Livro {

    /**
     * Identificador único do livro no banco de dados
     * Gerado automaticamente com estratégia IDENTITY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título do livro - campo obrigatório e único
     * Garante que não haverá livros duplicados no banco
     */
    @Column(unique = true, nullable = false, length = 500)
    private String titulo;

    /**
     * Relacionamento many-to-many com autores
     * Um livro pode ter múltiplos autores e um autor pode ter múltiplos livros
     * EAGER fetch para carregar autores junto com o livro
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    /**
     * Lista de idiomas do livro (ex: ["en", "pt", "es"])
     * Armazenado como ElementCollection para múltiplos valores
     */
    @ElementCollection
    @CollectionTable(name = "livro_idiomas", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "idioma")
    private List<String> idiomas = new ArrayList<>();

    /**
     * Número de downloads do livro no Project Gutenberg
     * Usado para ranking de popularidade
     */
    @Column(name = "numero_downloads")
    private Double numeroDownloads;

    /**
     * Método toString customizado para exibição formatada
     * @return String formatada com as informações principais do livro
     */
    @Override
    public String toString() {
        // Concatena nomes dos autores separados por vírgula
        String nomesAutores = autores.stream()
                .map(Autor::getNome)
                .collect(Collectors.joining(", "));

        // Concatena idiomas separados por vírgula
        String idiomasFormatados = idiomas != null ?
                String.join(", ", idiomas) : "Não especificado";

        return """
                📖 ========== LIVRO ==========
                📚 Título: %s
                👤 Autor(es): %s
                🌐 Idioma(s): %s
                📊 Downloads: %.0f
                ==============================""".formatted(
                titulo != null ? titulo : "Não informado",
                !nomesAutores.isEmpty() ? nomesAutores : "Não informado",
                idiomasFormatados,
                numeroDownloads != null ? numeroDownloads : 0.0
        );
    }

    /**
     * Método auxiliar para adicionar um autor à lista
     * @param autor Autor a ser adicionado
     */
    public void adicionarAutor(Autor autor) {
        if (autor != null && !autores.contains(autor)) {
            autores.add(autor);
        }
    }

    /**
     * Método auxiliar para adicionar um idioma à lista
     * @param idioma Código do idioma a ser adicionado
     */
    public void adicionarIdioma(String idioma) {
        if (idioma != null && !idioma.trim().isEmpty() && !idiomas.contains(idioma.toLowerCase())) {
            idiomas.add(idioma.toLowerCase());
        }
    }
}
