package br.com.compass.ecommerce_api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import br.com.compass.ecommerce_api.dtos.UserLoginDto;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;
import br.com.compass.ecommerce_api.jwt.JwtToken;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("null")
public class AuthenticationTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void authenticate_ValidCredentials_ReturnTokenStatus200() {
        JwtToken responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(JwtToken.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
    }

    @Test
    public void authenticate_InvalidCredentials_ReturnErrorMessageStatus400() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("invalid@gmail.com", "123456"))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("yugi@gmail.com", "000000"))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void authenticate_InvalidEmail_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    
        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("@gmail.com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void authenticate_InvalidPassword_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("yugi@gmail.com", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    
        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("yugi@gmail.com", "123"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDto("yugi@gmail.com", "123456789"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

    }
}
