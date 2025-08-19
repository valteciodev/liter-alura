package br.com.literalura;

import br.com.literalura.principal.Principal;
import br.com.literalura.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação LiterAlura
 * Responsável por inicializar o contexto Spring e executar a aplicação via terminal
 */
@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	// Injeção de dependência via construtor (boa prática)
	private final LivroService livroService;

	/**
	 * Construtor com injeção de dependência
	 * @param livroService Serviço responsável pelas operações com livros
	 */
	public LiterAluraApplication(LivroService livroService) {
		this.livroService = livroService;
	}

	/**
	 * Método principal - ponto de entrada da aplicação Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}

	/**
	 * Método executado após a inicialização do Spring
	 * Delega a responsabilidade do menu para a classe Principal
	 */
	@Override
	public void run(String... args) {
		// Criação da instância responsável pela interação com o usuário
		Principal principal = new Principal(livroService);
		principal.menu(); // Inicia o menu interativo
	}
}
