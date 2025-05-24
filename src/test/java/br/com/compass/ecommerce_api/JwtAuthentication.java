package br.com.compass.ecommerce_api;

import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import br.com.compass.ecommerce_api.dtos.UserLoginDto;
import br.com.compass.ecommerce_api.jwt.JwtToken;

@SuppressWarnings("null")
public class JwtAuthentication {

    public static Consumer<HttpHeaders> getHeaderAuthorization(WebTestClient client, String email, String password) {
        String token = client
            .post()
            .uri("/api/v1/auth")
            .bodyValue(new UserLoginDto(email, password))
            .exchange()
            .expectStatus().isOk()
            .expectBody(JwtToken.class)
            .returnResult().getResponseBody().getToken();

        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
