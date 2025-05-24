package br.com.compass.ecommerce_api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.UserPasswordDto;
import br.com.compass.ecommerce_api.dtos.UserResponseDto;
import br.com.compass.ecommerce_api.dtos.UserSaveDto;
import br.com.compass.ecommerce_api.enums.UserRole;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("null")
public class UserTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void save_ValidCredentials_ReturnCreatedUserStatus201() {
        UserResponseDto responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "kaiba@gmail.com", "123456"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getName()).isEqualTo("Seto Kaiba");
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("kaiba@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(UserRole.CLIENT);
    }

    @Test
    public void save_InvalidName_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("", "kaiba@gmail.com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void save_InvalidEmail_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "kaiba@", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "kaiba@gmail.", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void save_InvalidPassword_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "kaiba@gmail.com", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "kaiba@gmail.com", "123"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Seto Kaiba", "kaiba@gmail.com", "123456789"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void save_RepeatedEmail_ReturnErrorMessageStatus409() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("Tea Gardner", "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void findById_ExistentId_ReturnUserStatus200() {
        UserResponseDto responseBody = webTestClient
            .get()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(1);
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("yugi@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(UserRole.ADMIN);

        responseBody = webTestClient
            .get()
            .uri("/api/v1/users/3")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(3);
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("tea@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(UserRole.CLIENT);       

        responseBody = webTestClient
            .get()
            .uri("/api/v1/users/3")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(3);
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("tea@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(UserRole.CLIENT);
    }

    @Test
    public void findById_NonExistentId_ReturnUserStatus404() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/users/0")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findById_ClientSearchForOtherClient_ReturnUserStatus403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/users/2")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updatePassword_ValidPassword_ReturnStatus204() {
        webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isNoContent();

        webTestClient
            .patch()
            .uri("/api/v1/users/3")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    public void updatePassword_DifferentUsers_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/2")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updatePassword_InvalidFields_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("", "", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("12345", "12345", "12345"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("12345678", "12345678", "12345678"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updatePassword_InvalidPassword_ReturnErrorMessageStatus400() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "000000"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("000000", "123456", "123456"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void findAll_PaginationAdmin_ReturnClientsStatus200() {
        PageableDto responseBody = webTestClient
            .get()
            .uri("/api/v1/users")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(3);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = webTestClient
            .get()
            .uri("/api/v1/users?size=1&page=1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(3);
    }

    @Test
    public void findAll_PaginationClient_ReturnErrorMessage403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/users")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
