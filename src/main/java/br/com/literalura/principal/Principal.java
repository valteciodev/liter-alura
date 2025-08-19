package br.com.literalura.principal;

import br.com.literalura.dto.AutorDTO;
import br.com.literalura.dto.LivroDTO;
import br.com.literalura.exceptions.EntradaInvalidaException;
import br.com.literalura.model.Livro;
import br.com.literalura.service.LivroService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Classe Principal - Interface de usuário via terminal
 * Responsável pela interação com o usuário e delegação das operações para o LivroService
 */
public class Principal {

    // Injeção de dependência via construtor
    private final LivroService livroService;

    // Scanner para captura de entrada do usuário (único para toda a classe)
    private final Scanner sc = new Scanner(System.in);

    // Padrões regex para validação de entradas
    private static final Pattern NOME_VALIDO = Pattern.compile("^[\\p{L}\\s\\-\\'\\.,]{2,100}$");
    private static final Pattern OPCAO_CONFIRMAR = Pattern.compile("^[sSnN]$");

    /**
     * Construtor da classe Principal
     * @param livroService Serviço responsável pelas operações com livros
     */
    public Principal(LivroService livroService) {
        this.livroService = livroService;
    }

    /**
     * Método principal que exibe o menu e gerencia as opções do usuário
     * Loop principal da aplicação que permanece ativo até o usuário escolher sair
     */
    public void menu(){
        var opcao = -1;

        while (opcao!= 0) {
            exibirMenu();

            try{
                opcao = sc.nextInt();
                sc.nextLine(); // Limpa o buffer do scanner
            } catch (InputMismatchException e) {
                sc.nextLine(); // Limpa entrada inválida
                System.out.println("⚠️ Entrada inválida! Digite o número referente ao que deseja.");
                continue;
            }

            // Processamento das opções com tratamento de exceções
            try {
                processarOpcao(opcao);
            } catch (EntradaInvalidaException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (Exception e) {
                System.out.println("💥 Erro inesperado: " + e.getMessage());
            }
        }
    }

    /**
     * Exibe o menu principal com todas as opções disponíveis
     */
    private void exibirMenu() {
        var menu = """
            \n📚 ========================================
            📖 LITERALURA - Sistema de Gerenciamento de Livros
            ========================================
            
            🔍 1- Buscar livro pelo título
            👤 2- Buscar autor pelo nome
            📖 3- Buscar livro e seu resumo, pelo autor
            📋 4- Listar livros cadastrados
            👥 5- Listar autores cadastrados
            📅 6- Listar autores vivos em algum ano
            🌐 7- Listar livros em algum idioma
            🏆 8- Listar Top 10 livros cadastrados
            
            🚪 0- Sair
            =========================================
            Digite sua opção: """;
        System.out.print(menu);
    }

    /**
     * Processa a opção escolhida pelo usuário
     * @param opcao Número da opção escolhida
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> buscarLivroPeloTitulo();
            case 2 -> buscarAutorPeloNome();
            case 3 -> buscarLivroPeloAutor();
            case 4 -> listarLivrosCadastrados();
            case 5 -> listarAutoresCadastrados();
            case 6 -> listarAutoresVivosAno();
            case 7 -> listarLivrosEmIdioma();
            case 8 -> listarTop10Livros();
            case 0 -> System.out.println("👋 Encerrando a aplicação!");
            default -> System.out.println("❌ Opção inválida");
        }
    }

    /**
     * Busca um livro pelo título na API e oferece opção de salvamento
     * Valida a entrada usando expressão regular
     */
    private void buscarLivroPeloTitulo() {
        System.out.print("\n🔍 Digite o nome do livro que deseja buscar: ");
        String titulo = sc.nextLine().trim();

        // Validação com regex - aceita letras, números, espaços e pontuação básica
        if (!validarEntradaTexto(titulo)) {
            throw new EntradaInvalidaException("Título deve conter apenas letras, números e pontuação básica (2-100 caracteres)");
        }

        List<Livro> livros = livroService.buscarLivrosAPI(titulo);

        if (livros.isEmpty()){
            System.out.println("❌ Nenhum livro encontrado");
            return;
        }

        Livro primeiroLivro = livros.get(0);
        System.out.println("📖 Livro encontrado:\n" + primeiroLivro);

        System.out.print("\n💾 Deseja salvar o livro encontrado? (s/n): ");
        String opcaoSalvarLivro = sc.nextLine().trim();

        // Validação com regex para s/n
        if (OPCAO_CONFIRMAR.matcher(opcaoSalvarLivro).matches() &&
            opcaoSalvarLivro.toLowerCase().equals("s")) {
            Livro livroSalvo = livroService.salvarLivro(primeiroLivro);
            System.out.println("\n✅ Livro salvo com sucesso:\n" + livroSalvo);
        }
    }

    /**
     * Busca um autor pelo nome no banco de dados local
     * Valida a entrada usando expressão regular
     */
    private void buscarAutorPeloNome() {
        System.out.print("\n👤 Digite o nome do autor que deseja buscar: ");
        String nomeAutor = sc.nextLine().trim();

        if (!validarNomeAutor(nomeAutor)) {
            throw new EntradaInvalidaException("Nome do autor inválido. Use apenas letras, espaços e caracteres especiais básicos");
        }

        Optional<AutorDTO> autor = livroService.buscarAutorPeloNome(nomeAutor);

        if (autor.isEmpty()){
            System.out.println("\n❌ Nenhum autor encontrado com este nome.");
        } else {
            System.out.println("\n✅ Dados do autor: " + autor.get());
        }
    }

    /**
     * Busca livros de um autor específico e permite visualizar resumo
     */
    private void buscarLivroPeloAutor() {
        System.out.print("\n👤 Digite o nome do autor que deseja: ");
        String nomeAutor = sc.nextLine().trim();

        if (!validarNomeAutor(nomeAutor)) {
            throw new EntradaInvalidaException("Nome do autor inválido");
        }

        List<LivroDTO> livroPeloAutor = livroService.buscarLivroPeloAutor(nomeAutor);
        if (livroPeloAutor.isEmpty()){
            System.out.println("\n❌ Nenhum autor encontrado com este nome!");
            return;
        }

        System.out.println("\n📚 Autor: " + nomeAutor + ", livros: ");
        livroPeloAutor.forEach(livro -> System.out.println("📖 - " + livro.titulo()));

        System.out.print("\n📝 Digite o título do livro que deseja ver o resumo: ");
        String titulo = sc.nextLine().trim();

        try {
            String resumo = livroService.buscarResumoPorTitulo(titulo);
            System.out.println("\n📄 Resumo do livro: " + resumo);
        } catch (RuntimeException e) {
            System.out.println("\n❌ Erro ao encontrar o resumo!");
        }
    }

    /**
     * Lista todos os livros cadastrados no banco de dados
     */
    private void listarLivrosCadastrados() {
        System.out.println("\n📚 === LIVROS CADASTRADOS ===");
        List<LivroDTO> listaLivros = livroService.listarLivrosCadastrados();
        if (listaLivros.isEmpty()) {
            System.out.println("❌ Nenhum livro cadastrado.");
        } else {
            listaLivros.forEach(livro -> System.out.println("📖 " + livro));
        }
    }

    /**
     * Lista todos os autores cadastrados no banco de dados
     */
    private void listarAutoresCadastrados() {
        System.out.println("\n👥 === AUTORES CADASTRADOS ===");
        List<AutorDTO> listaAutores = livroService.listarAutoresCadastrados();
        if (listaAutores.isEmpty()) {
            System.out.println("❌ Nenhum autor cadastrado.");
        } else {
            listaAutores.forEach(autor -> System.out.println("👤 " + autor));
        }
    }

    /**
     * Lista autores que estavam vivos em um determinado ano
     * Valida se o ano informado é válido
     */
    private void listarAutoresVivosAno() {
        System.out.print("\n📅 Digite o ano em que deseja listar os autores vivos: ");
        try {
            Integer ano = sc.nextInt();
            sc.nextLine(); // Limpa o buffer

            // Validação básica do ano (entre 1 e ano atual + 10)
            if (ano < 1 || ano > java.time.Year.now().getValue() + 10) {
                throw new EntradaInvalidaException("Ano deve estar entre 1 e " + (java.time.Year.now().getValue() + 10));
            }

            List<AutorDTO> autoresVivos = livroService.listarAutoresVivosAno(ano);

            if(autoresVivos.isEmpty()) {
                System.out.println("\n❌ Nenhum autor vivo encontrado para o ano: " + ano);
            } else {
                System.out.println("\n✅ Autores vivos em " + ano + ":");
                autoresVivos.forEach(autor -> System.out.println("👤 " + autor));
            }
        } catch (InputMismatchException e) {
            sc.nextLine(); // Limpa entrada inválida
            throw new EntradaInvalidaException("Por favor, digite um ano válido (apenas números)");
        }
    }

    /**
     * Lista livros filtrados por idioma
     */
    private void listarLivrosEmIdioma() {
        livroService.menuIdioma();
        String idioma = sc.nextLine().trim().toLowerCase();

        List<LivroDTO> livrosPorIdioma = livroService.listarLivrosEmIdioma(idioma);

        if (livrosPorIdioma.isEmpty()) {
            System.out.println("\n❌ Idioma escolhido: " + idioma +
                                "\nNenhum livro encontrado neste idioma!");
        } else {
            System.out.println("\n✅ Idioma escolhido: " + idioma +
                                "\n📚 Livros cadastrados neste idioma:");
            livrosPorIdioma.forEach(livro -> System.out.println("📖 " + livro));
        }
    }

    /**
     * Lista os top 10 livros mais baixados
     */
    private void listarTop10Livros() {
        System.out.println("\n🏆 === TOP 10 LIVROS MAIS BAIXADOS ===");
        List<LivroDTO> top10Livros = livroService.listarTop10Livros();
        if (top10Livros.isEmpty()) {
            System.out.println("❌ Nenhum livro disponível no ranking.");
        } else {
            for (int i = 0; i < top10Livros.size(); i++) {
                System.out.println((i + 1) + "º 📖 " + top10Livros.get(i));
            }
        }
    }

    // ============ MÉTODOS DE VALIDAÇÃO COM REGEX ============

    /**
     * Valida entrada de texto usando expressão regular
     * @param texto Texto a ser validado
     * @return true se válido, false caso contrário
     */
    private boolean validarEntradaTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        // Aceita letras (unicode), números, espaços e pontuação básica
        Pattern pattern = Pattern.compile("^[\\p{L}\\p{N}\\s\\-\\'\\.,!?;:()\\[\\]\"]{2,100}$");
        return pattern.matcher(texto.trim()).matches();
    }

    /**
     * Valida nome de autor usando expressão regular
     * @param nome Nome do autor a ser validado
     * @return true se válido, false caso contrário
     */
    private boolean validarNomeAutor(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        // Aceita apenas letras, espaços, hífen, apóstrofo e ponto
        return NOME_VALIDO.matcher(nome.trim()).matches();
    }
}