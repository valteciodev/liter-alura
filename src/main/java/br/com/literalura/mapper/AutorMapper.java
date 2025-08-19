package br.com.literalura.mapper;

import br.com.literalura.dto.AutorDTO;
import br.com.literalura.model.Autor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutorMapper {

    public static Autor toEntity(AutorDTO dto){
        Autor autor = new Autor();
        autor.setNome(dto.nome());
        autor.setAnoNascimento(dto.anoNascimento());
        autor.setAnoFalecimento(dto.anoFalecimento());

        return autor;
    }

    public static AutorDTO toDto(Autor autor) {
        return new AutorDTO(autor.getNome(), autor.getAnoNascimento(), autor.getAnoFalecimento());
    }

    public static List<AutorDTO> toDtoList(List<Autor> autors) {
        return autors.stream()
                .map(AutorMapper::toDto)
                .toList();
    }
}
