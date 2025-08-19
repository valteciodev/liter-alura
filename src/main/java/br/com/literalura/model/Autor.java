package br.com.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Entidade JPA que representa um autor no banco de dados
 * Contém relacionamento many-to-many bidirecional com livros
 */
@Entity
@Getter
@Setter
@Table(name = "autores")
public class Autor {

    // Padrão regex para validação de nomes de autores
    private static final Pattern PATTERN_NOME_VALIDO = Pattern.compile("^[\\p{L}\\s\\-\\'\\.,]{2,200}$");

    /**
     * Identificador único do autor no banco de dados
     * Gerado automaticamente com estratégia IDENTITY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do autor - campo obrigatório e único
     * Garante que não haverá autores duplicados no banco
     */
    @Column(unique = true, nullable = false, length = 200)
    private String nome;

    /**
     * Ano de nascimento do autor
     * Usado para cálculos de período de vida
     */
    @Column(name = "ano_nascimento")
    private Integer anoNascimento;

    /**
     * Ano de falecimento do autor
     * Null indica que o autor ainda está vivo
     */
    @Column(name = "ano_falecimento")
    private Integer anoFalecimento;

    /**
     * Lista de livros do autor
     * Relacionamento many-to-many bidirecional (lado inverso)
     * LAZY fetch para evitar problemas de performance
     */
    @ManyToMany(mappedBy = "autores", fetch = FetchType.LAZY)
    private List<Livro> livros = new ArrayList<>();

    /**
     * Método toString customizado para exibição formatada
     * @return String formatada com as informações do autor
     */
    @Override
    public String toString() {
        String statusVida = anoFalecimento != null ?
            "Falecido em " + anoFalecimento : "Vivo";

        String periodo = calcularPeriodoVida();

        return """
                👤 ========== AUTOR ==========
                📝 Nome: %s
                🗓️ Nascimento: %s
                ⚰️ Status: %s
                📅 Período: %s
                📚 Livros: %d obra(s)
                =============================""".formatted(
                nome != null ? nome : "Não informado",
                anoNascimento != null ? anoNascimento.toString() : "Não informado",
                statusVida,
                periodo,
                livros != null ? livros.size() : 0
        );
    }

    /**
     * Calcula o período de vida do autor
     * @return String formatada com o período de vida
     */
    private String calcularPeriodoVida() {
        if (anoNascimento == null) {
            return "Período desconhecido";
        }

        if (anoFalecimento == null) {
            int anoAtual = java.time.Year.now().getValue();
            int idade = anoAtual - anoNascimento;
            return anoNascimento + " - presente (" + idade + " anos)";
        } else {
            int idadeAoFalecer = anoFalecimento - anoNascimento;
            return anoNascimento + " - " + anoFalecimento + " (" + idadeAoFalecer + " anos)";
        }
    }

    /**
     * Verifica se o autor estava vivo em um determinado ano
     * @param ano Ano a ser verificado
     * @return true se o autor estava vivo no ano especificado
     */
    public boolean estavVivoEm(int ano) {
        if (anoNascimento == null) {
            return false; // Não podemos determinar sem ano de nascimento
        }

        // Verifica se o ano está dentro do período de vida
        boolean nasceuAntes = ano >= anoNascimento;
        boolean morreu = anoFalecimento != null && ano > anoFalecimento;

        return nasceuAntes && !morreu;
    }

    /**
     * Valida se o nome do autor está em formato válido
     * @param nome Nome a ser validado
     * @return true se o nome é válido
     */
    public static boolean isNomeValido(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        return PATTERN_NOME_VALIDO.matcher(nome.trim()).matches();
    }

    /**
     * Método auxiliar para adicionar um livro à lista
     * @param livro Livro a ser adicionado
     */
    public void adicionarLivro(Livro livro) {
        if (livro != null && !livros.contains(livro)) {
            livros.add(livro);
        }
    }

    /**
     * Verifica se o autor ainda está vivo
     * @return true se o autor ainda está vivo (anoFalecimento é null)
     */
    public boolean isVivo() {
        return anoFalecimento == null;
    }
}
