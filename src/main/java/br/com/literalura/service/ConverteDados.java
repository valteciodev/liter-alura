package br.com.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Component;

/**
 * Serviço responsável pela conversão de dados JSON em objetos Java
 * Implementa a interface IConverteDados usando Jackson ObjectMapper
 * Configurado para ignorar propriedades desconhecidas e usar snake_case
 */
@Component
public class ConverteDados implements IConverteDados {

    // ObjectMapper configurado para deserialização JSON
    private final ObjectMapper mapper;

    /**
     * Construtor que configura o ObjectMapper com as configurações adequadas
     * para a API Gutendx
     */
    public ConverteDados() {
        this.mapper = new ObjectMapper()
                // Ignora propriedades JSON que não existem na classe Java
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // Ignora propriedades nulas no JSON
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                // Configura para usar snake_case (usado pela API Gutendx)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * Converte uma string JSON em um objeto da classe especificada
     * @param json String JSON a ser convertida
     * @param classe Classe de destino para a conversão
     * @param <T> Tipo genérico da classe de destino
     * @return Objeto da classe especificada populado com os dados do JSON
     * @throws RuntimeException se houver erro na conversão JSON
     */
    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            // Validação básica do JSON
            if (json == null || json.trim().isEmpty()) {
                throw new IllegalArgumentException("JSON não pode ser nulo ou vazio");
            }

            if (classe == null) {
                throw new IllegalArgumentException("Classe de destino não pode ser nula");
            }

            System.out.println("🔄 Convertendo JSON para " + classe.getSimpleName());

            T resultado = mapper.readValue(json, classe);

            System.out.println("✅ Conversão realizada com sucesso");

            return resultado;

        } catch (JsonProcessingException e) {
            System.err.println("❌ Erro ao processar JSON: " + e.getMessage());
            throw new RuntimeException("Erro na conversão JSON para " + classe.getSimpleName() +
                                     ": " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado na conversão: " + e.getMessage());
            throw new RuntimeException("Erro inesperado ao converter dados: " + e.getMessage(), e);
        }
    }
}
