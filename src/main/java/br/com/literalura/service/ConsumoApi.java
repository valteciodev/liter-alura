package br.com.literalura.service;

import br.com.literalura.dto.ResponseAPI;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Serviço responsável pelo consumo da API externa Gutendx
 * Faz requisições HTTP para buscar informações de livros no Project Gutenberg
 */
@Component
public class ConsumoApi {

    // URL base da API Gutendx
    private static final String ENDPOINT = "https://gutendx.com/books/";

    // Timeout para requisições HTTP (em segundos)
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    // Cliente HTTP configurado com timeout
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    // Serviço para conversão de dados JSON
    private final ConverteDados converteDados;

    // Padrões regex para validação e sanitização
    private static final Pattern PATTERN_ESPACOS_MULTIPLOS = Pattern.compile("\\s+");
    private static final Pattern PATTERN_CARACTERES_ESPECIAIS_URL = Pattern.compile("[^\\p{L}\\p{N}\\s\\-]");

    /**
     * Construtor com injeção de dependência
     * @param converteDados Serviço para conversão de dados JSON
     */
    public ConsumoApi(ConverteDados converteDados) {
        this.converteDados = converteDados;
    }

    /**
     * Busca livros na API Gutendx pelo título
     * Sanitiza e codifica o título para uso seguro na URL
     * @param tituloLivro Título do livro a ser buscado
     * @return ResponseAPI com os dados dos livros encontrados
     * @throws RuntimeException em caso de erro na requisição ou resposta
     */
    public ResponseAPI obterDados(String tituloLivro) {
        try {
            // Sanitiza e prepara o título para a busca
            String tituloSanitizado = sanitizarTituloParaBusca(tituloLivro);
            String tituloCodeificado = URLEncoder.encode(tituloSanitizado, StandardCharsets.UTF_8);

            // Constrói a URL da requisição
            String url = ENDPOINT + "?search=" + tituloCodeificado;

            System.out.println("🔍 Buscando na API: " + url);

            // Constrói a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .header("Accept", "application/json")
                    .header("User-Agent", "LiterAlura/1.0")
                    .GET()
                    .build();

            // Executa a requisição
            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // Valida o status da resposta
            if (response.statusCode() != 200) {
                throw new RuntimeException("❌ Erro na API: Status " + response.statusCode() +
                                         " - " + getStatusMessage(response.statusCode()));
            }

            String json = response.body();

            // Valida se a resposta não está vazia
            if (json == null || json.trim().isEmpty()) {
                throw new RuntimeException("❌ Resposta vazia da API");
            }

            System.out.println("✅ Dados recebidos da API com sucesso");

            // Converte JSON para objeto ResponseAPI
            return converteDados.obterDados(json, ResponseAPI.class);

        } catch (IOException e) {
            throw new RuntimeException("❌ Erro de conectividade ao acessar a API Gutendx: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaura o status de interrupção
            throw new RuntimeException("❌ Requisição interrompida", e);
        } catch (Exception e) {
            throw new RuntimeException("❌ Erro inesperado ao consumir a API: " + e.getMessage(), e);
        }
    }

    /**
     * Sanitiza o título para uso seguro na URL de busca
     * Remove caracteres especiais e normaliza espaços
     * @param titulo Título original
     * @return Título sanitizado para busca
     */
    private String sanitizarTituloParaBusca(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser nulo ou vazio");
        }

        String tituloSanitizado = titulo.trim();

        // Remove caracteres especiais mantendo apenas letras, números, espaços e hífens
        tituloSanitizado = PATTERN_CARACTERES_ESPECIAIS_URL.matcher(tituloSanitizado).replaceAll(" ");

        // Substitui múltiplos espaços por um único espaço
        tituloSanitizado = PATTERN_ESPACOS_MULTIPLOS.matcher(tituloSanitizado).replaceAll(" ");

        return tituloSanitizado.trim();
    }

    /**
     * Retorna mensagem descritiva para códigos de status HTTP
     * @param statusCode Código de status HTTP
     * @return Descrição do status
     */
    private String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Requisição inválida";
            case 401 -> "Não autorizado";
            case 403 -> "Acesso proibido";
            case 404 -> "Recurso não encontrado";
            case 429 -> "Muitas requisições - tente novamente mais tarde";
            case 500 -> "Erro interno do servidor";
            case 502 -> "Gateway inválido";
            case 503 -> "Serviço indisponível";
            case 504 -> "Timeout do gateway";
            default -> "Status HTTP " + statusCode;
        };
    }
}