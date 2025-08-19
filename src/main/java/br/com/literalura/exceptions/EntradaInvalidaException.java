package br.com.literalura.exceptions;

/**
 * Exceção customizada para tratar entradas inválidas do usuário
 * Extends RuntimeException para ser uma exceção não verificada
 * Utilizada quando o usuário fornece dados em formato incorreto ou inválido
 */
public class EntradaInvalidaException extends RuntimeException {

    /**
     * Construtor padrão com mensagem personalizada
     * @param mensagem Descrição específica do erro de validação
     */
    public EntradaInvalidaException(String mensagem) {
        super("❌ Entrada inválida: " + mensagem);
    }

    /**
     * Construtor com mensagem e causa raiz
     * @param mensagem Descrição do erro
     * @param causa Exceção que originou este erro
     */
    public EntradaInvalidaException(String mensagem, Throwable causa) {
        super("❌ Entrada inválida: " + mensagem, causa);
    }

    /**
     * Método estático para criar exceção de campo obrigatório
     * @param nomeCampo Nome do campo que está faltando
     * @return Nova instância da exceção
     */
    public static EntradaInvalidaException campoObrigatorio(String nomeCampo) {
        return new EntradaInvalidaException("O campo '" + nomeCampo + "' é obrigatório");
    }

    /**
     * Método estático para criar exceção de formato inválido
     * @param nomeCampo Nome do campo com formato inválido
     * @param formatoEsperado Formato esperado para o campo
     * @return Nova instância da exceção
     */
    public static EntradaInvalidaException formatoInvalido(String nomeCampo, String formatoEsperado) {
        return new EntradaInvalidaException("O campo '" + nomeCampo + "' deve estar no formato: " + formatoEsperado);
    }
}
