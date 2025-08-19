package br.com.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO (Data Transfer Object) para representar dados de autores da API Gutendx
 * Record imutável que mapeia os campos JSON para propriedades Java
 * Usado para transferência de dados entre a API externa e a aplicação
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorDTO(
        /** Nome completo do autor - mapeado do campo "name" do JSON */
        @JsonAlias("name") String nome,

        /** Ano de nascimento - mapeado do campo "birth_year" do JSON */
        @JsonAlias("birth_year") Integer anoNascimento,

        /** Ano de falecimento - mapeado do campo "death_year" do JSON (null se vivo) */
        @JsonAlias("death_year") Integer anoFalecimento
) {

    /**
     * Método toString customizado para exibição formatada do autor
     * Calcula idade e período de vida automaticamente
     * @return String formatada com todas as informações do autor
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
                =============================""".formatted(
                nome != null ? nome : "Não informado",
                anoNascimento != null ? anoNascimento.toString() : "Não informado",
                statusVida,
                periodo
        );
    }

    /**
     * Calcula o período de vida do autor com idade
     * @return String formatada com o período de vida e idade
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
     * Verifica se o autor ainda está vivo
     * @return true se o autor ainda está vivo (anoFalecimento é null)
     */
    public boolean isVivo() {
        return anoFalecimento == null;
    }

    /**
     * Verifica se o autor estava vivo em um determinado ano
     * @param ano Ano a ser verificado
     * @return true se o autor estava vivo no ano especificado
     */
    public boolean estavVivoEm(int ano) {
        if (anoNascimento == null) {
            return false;
        }

        boolean nasceuAntes = ano >= anoNascimento;
        boolean morreu = anoFalecimento != null && ano > anoFalecimento;

        return nasceuAntes && !morreu;
    }

    /**
     * Calcula a idade atual do autor (se vivo) ou idade ao falecer
     * @return Idade em anos ou -1 se não for possível calcular
     */
    public int calcularIdade() {
        if (anoNascimento == null) {
            return -1;
        }

        int anoReferencia = anoFalecimento != null ?
            anoFalecimento : java.time.Year.now().getValue();

        return anoReferencia - anoNascimento;
    }
}
