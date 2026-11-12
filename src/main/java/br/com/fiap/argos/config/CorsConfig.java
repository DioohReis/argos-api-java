package br.com.fiap.argos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS: permissao para o navegador chamar a API a partir de outro endereco (o front na Vercel).
// Sem isso a chamada funciona no Insomnia e falha no navegador.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origens;

    // Os enderecos liberados vem da variavel de ambiente CORS_ORIGINS
    public CorsConfig(@Value("${argos.cors.origins}") String[] origens) {
        this.origens = origens;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(origens)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*");
    }
}
