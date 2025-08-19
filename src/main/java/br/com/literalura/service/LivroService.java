package br.com.literalura.service;

import br.com.literalura.dto.AutorDTO;
import br.com.literalura.dto.LivroDTO;
import br.com.literalura.dto.ResponseAPI;
import br.com.literalura.mapper.AutorMapper;
import br.com.literalura.mapper.LivroMapper;
import br.com.literalura.model.Autor;
import br.com.literalura.model.Livro;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Serviço principal para operações relacionadas a livros e autores
 * Centraliza a lógica de negócio e faz a ponte entre a API externa, banco de dados e interface do usuário
 */
@Service
public class LivroService {

    // Repositories para acesso aos dados
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    // Serviço para consumo da API externa (Gutendx)
    private final ConsumoApi consumoApi;

    // Padrões regex para validação e normalização
    private static final Pattern PATTERN_DIACRITICOS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]");
    private static final Pattern PATTERN_ESPACOS_MULTIPLOS = Pattern.compile("\\s+");
    private static final Pattern PATTERN_CARACTERES_ESPECIAIS = Pattern.compile("[^\\p{L}\\p{N}\\s]");

    /**
     * Construtor com injeção de dependências
     * @param autorRepository Repository para operações com autores
     * @param livroRepository Repository para operações com livros
     * @param consumoApi Serviço para consumo da API externa
     */
    public LivroService(AutorRepository autorRepository, LivroRepository livroRepository, ConsumoApi consumoApi) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
        this.consumoApi = consumoApi;
    }

    /**
     * Busca livros na API externa do Gutendx pelo título
     * @param titulo Título do livro a ser buscado
     * @return Lista de livros encontrados na API
     */
    public List<Livro> buscarLivrosAPI(String titulo) {
        try {
            // Normaliza o título para melhorar a busca
            String tituloNormalizado = normalizarTextoParaBusca(titulo);

            ResponseAPI responseAPI = consumoApi.obterDados(tituloNormalizado);
            List<LivroDTO> livrosDTO = responseAPI.livros();

            // Converte DTOs para entidades usando mapper
            return livrosDTO.stream()
                    .map(LivroMapper::toEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao buscar livros na API: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Salva um livro no banco de dados
     * Verifica se os autores já existem para evitar duplicação
     * @param livro Livro a ser salvo
     * @return Livro salvo com IDs gerados
     */
    public Livro salvarLivro(Livro livro) {
        // Processa autores: verifica se já existem ou cria novos
        List<Autor> autores = livro.getAutores().stream()
                .map(autor -> {
                    // Busca autor existente por nome (case-insensitive)
                    return autorRepository.findByNomeContainingIgnoreCase(autor.getNome())
                            .orElseGet(() -> {
                                // Se não existe, salva novo autor
                                System.out.println("💾 Salvando novo autor: " + autor.getNome());
                                return autorRepository.save(autor);
                            });
                })
                .toList();

        // Atualiza a lista de autores do livro com as entidades persistidas
        livro.setAutores(autores);

        // Salva o livro
        Livro livroSalvo = livroRepository.save(livro);
        System.out.println("✅ Livro salvo com sucesso: " + livroSalvo.getTitulo());

        return livroSalvo;
    }

    /**
     * Busca um autor pelo nome no banco de dados local
     * @param nomeAutor Nome do autor a ser buscado
     * @return Optional contendo o AutorDTO se encontrado
     */
    public Optional<AutorDTO> buscarAutorPeloNome(String nomeAutor) {
        return autorRepository.findByNomeContainingIgnoreCase(nomeAutor)
                .map(AutorMapper::toDto);
    }

    /**
     * Busca livros de um autor específico no banco de dados local
     * @param nomeAutor Nome do autor
     * @return Lista de livros do autor
     */
    public List<LivroDTO> buscarLivroPeloAutor(String nomeAutor) {
        return autorRepository.findByNomeContainingIgnoreCase(nomeAutor)
                .map(Autor::getLivros) // Obtém os livros do autor
                .map(LivroMapper::toDtoList) // Converte para DTOs
                .orElse(Collections.emptyList());
    }

    /**
     * Busca o resumo de um livro específico na API externa
     * Utiliza normalização de texto para melhorar a precisão da busca
     * @param titulo Título do livro
     * @return Resumo do livro
     * @throws RuntimeException se o resumo não for encontrado
     */
    public String buscarResumoPorTitulo(String titulo) {
        try {
            ResponseAPI response = consumoApi.obterDados(titulo);
            String tituloNormalizado = normalizar(titulo);

            return response.livros().stream()
                    .filter(livro -> normalizar(livro.titulo()).contains(tituloNormalizado))
                    .map(livro -> {
                        List<String> resumos = livro.resumo();
                        if (resumos != null && !resumos.isEmpty()) {
                            return resumos.get(0);
                        }
                        return null;
                    })
                    .filter(resumo -> resumo != null && !resumo.isBlank())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Resumo não encontrado para: " + titulo));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar resumo: " + e.getMessage());
        }
    }

    /**
     * Normaliza texto removendo acentos e convertendo para minúsculas
     * Usado para comparações de texto mais flexíveis
     * @param texto Texto a ser normalizado
     * @return Texto normalizado
     */
    private String normalizar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }
        return Normalizer
                .normalize(texto, Normalizer.Form.NFD)
                .replaceAll(PATTERN_DIACRITICOS.pattern(), "")
                .toLowerCase()
                .trim();
    }

    /**
     * Normaliza texto para busca na API, removendo caracteres especiais e espaços múltiplos
     * @param texto Texto a ser normalizado
     * @return Texto otimizado para busca
     */
    private String normalizarTextoParaBusca(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }

        String textoNormalizado = normalizar(texto);

        // Remove caracteres especiais mantendo apenas letras, números e espaços
        textoNormalizado = PATTERN_CARACTERES_ESPECIAIS.matcher(textoNormalizado).replaceAll(" ");

        // Substitui múltiplos espaços por um único espaço
        textoNormalizado = PATTERN_ESPACOS_MULTIPLOS.matcher(textoNormalizado).replaceAll(" ");

        return textoNormalizado.trim();
    }

    /**
     * Lista todos os livros cadastrados no banco de dados
     * @return Lista de DTOs dos livros cadastrados
     */
    public List<LivroDTO> listarLivrosCadastrados() {
        List<Livro> livros = livroRepository.findAllComAutores();
        return LivroMapper.toDtoList(livros);
    }

    /**
     * Lista todos os autores cadastrados no banco de dados
     * @return Lista de DTOs dos autores cadastrados
     */
    public List<AutorDTO> listarAutoresCadastrados() {
        List<Autor> autores = autorRepository.findAll();
        return AutorMapper.toDtoList(autores);
    }

    /**
     * Lista autores que estavam vivos em um determinado ano
     * @param ano Ano de referência
     * @return Lista de autores vivos no ano especificado
     */
    public List<AutorDTO> listarAutoresVivosAno(Integer ano) {
        List<Autor> autores = autorRepository.findAutoresVivosEmAno(ano);
        return autores.stream()
                .map(AutorMapper::toDto)
                .toList();
    }

    /**
     * Exibe menu de idiomas disponíveis para filtragem
     * Poderia ser movido para uma classe de constantes ou configuração
     */
    public void menuIdioma() {
        var menu = """
                \n🌐 ========================================
                📖 FILTRO POR IDIOMA
                ========================================
                Digite a abreviação do idioma desejado:
                
                🇪🇸 es - Espanhol
                🇺🇸 en - Inglês  
                🇫🇷 fr - Francês
                🇧🇷 pt - Português
                
                =========================================
                """;

        System.out.println(menu);
    }

    /**
     * Lista livros filtrados por idioma
     * @param idioma Código do idioma (ex: "pt", "en", "es", "fr")
     * @return Lista de livros no idioma especificado
     */
    public List<LivroDTO> listarLivrosEmIdioma(String idioma) {
        List<Livro> livros = livroRepository.findAll();

        return livros.stream()
                .filter(livro -> livro.getIdiomas() != null &&
                        livro.getIdiomas().stream()
                                .anyMatch(id -> id.equalsIgnoreCase(idioma)))
                .map(LivroMapper::toDto)
                .toList();
    }

    /**
     * Lista os top 10 livros mais baixados
     * Utiliza query customizada do repository ordenada por número de downloads
     * @return Lista dos 10 livros mais baixados
     */
    public List<LivroDTO> listarTop10Livros() {
        return LivroMapper.toDtoList(livroRepository.findTop10ByOrderByNumeroDownloadsDesc());
    }
}
