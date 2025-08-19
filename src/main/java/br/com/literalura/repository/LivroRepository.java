package br.com.literalura.repository;

import br.com.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

//    Optional<Livro> findByTituloContainingIgnoreCase (String titulo);

    List<Livro> findTop10ByOrderByNumeroDownloadsDesc();

    @Query("SELECT l FROM Livro l LEFT JOIN FETCH l.autores")
    List<Livro> findAllComAutores();

//    @Query("SELECT DISTINCT l FROM Livro l LEFT JOIN FETCH l.autores WHERE :idioma MEMBER OF l.idiomas")
//    List<Livro> buscarPorIdioma(String idioma);
}
