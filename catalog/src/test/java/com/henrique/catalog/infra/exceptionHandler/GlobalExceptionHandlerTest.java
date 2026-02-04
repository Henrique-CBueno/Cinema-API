package com.henrique.catalog.infra.exceptionHandler;

import com.henrique.catalog.factory.MethodArgumentNotValidExceptionFactory;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.infra.exceptions.UnprocessableEntityException;
import com.henrique.catalog.infra.padronize.ErrorGlobalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

        private GlobalExceptionHandler exceptionHandler;

        @BeforeEach
        void setUp() {
                exceptionHandler = new GlobalExceptionHandler();
        }

        @Nested
        class MovieDontExists {

                @Test
                void shouldReturnNotFoundStatusWhenMovieNotFound() {
                        // Arrange
                        UUID movieId = UUID.randomUUID();
                        String errorMessage = String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, movieId);
                        NotFoundException exception = new NotFoundException(errorMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.movieDontExists(exception);

                        // Assert
                        assertNotNull(response);
                        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                        assertNotNull(response.getBody());
                }

                @Test
                void shouldReturnCorrectErrorMessageInResponse() {
                        // Arrange
                        UUID movieId = UUID.randomUUID();
                        String errorMessage = String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, movieId);
                        NotFoundException exception = new NotFoundException(errorMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.movieDontExists(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(errorMessage, response.getBody().error());
                        assertTrue(response.getBody().error().contains(movieId.toString()));
                }

                @Test
                void shouldReturnNotFoundStatusNameInResponse() {
                        // Arrange
                        String errorMessage = "Não existe filme com id 123";
                        NotFoundException exception = new NotFoundException(errorMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.movieDontExists(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(HttpStatus.NOT_FOUND.name(), response.getBody().status());
                }

                @Test
                void shouldIncludeTimestampInResponse() {
                        // Arrange
                        String errorMessage = "Não existe filme com id 456";
                        NotFoundException exception = new NotFoundException(errorMessage);
                        LocalDateTime beforeTest = LocalDateTime.now();

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.movieDontExists(exception);
                        LocalDateTime afterTest = LocalDateTime.now();

                        // Assert
                        assert response.getBody() != null;
                        assertNotNull(response.getBody().timestamp());
                        assertTrue(response.getBody().timestamp().isAfter(beforeTest.minusSeconds(1)));
                        assertTrue(response.getBody().timestamp().isBefore(afterTest.plusSeconds(1)));
                }

                @Test
                void shouldHandleMultipleDifferentNotFoundExceptions() {
                        // Arrange
                        UUID movieId1 = UUID.randomUUID();
                        UUID movieId2 = UUID.randomUUID();
                        String message1 = String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, movieId1);
                        String message2 = String.format(ExceptionsConstants.MOVIE_DONT_EXISTS, movieId2);

                        // Act & Assert
                        ResponseEntity<ErrorGlobalResponse> response1 = exceptionHandler
                                        .movieDontExists(new NotFoundException(message1));
                        ResponseEntity<ErrorGlobalResponse> response2 = exceptionHandler
                                        .movieDontExists(new NotFoundException(message2));

                        assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());
                        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
                        assert response1.getBody() != null;
                        assertEquals(message1, response1.getBody().error());
                        assert response2.getBody() != null;
                        assertEquals(message2, response2.getBody().error());
                }
        }

        @Nested
        class DuplicateResource {

                @Test
                void shouldReturnConflictStatusWhenDuplicateResourceFound() {
                        // Arrange
                        String fieldName = "titulo";
                        DuplicateResourceException exception = new DuplicateResourceException(
                                        ExceptionsConstants.DUPLICATE_RESOURCE,
                                        fieldName);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.duplicateResource(exception);

                        // Assert
                        assertNotNull(response);
                        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                        assertNotNull(response.getBody());
                }

                @Test
                void shouldReturnCorrectErrorMessageForDuplicateResource() {
                        // Arrange
                        String fieldName = "titulo";
                        DuplicateResourceException exception = new DuplicateResourceException(
                                        ExceptionsConstants.DUPLICATE_RESOURCE,
                                        fieldName);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.duplicateResource(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertNotNull(response.getBody().error());
                        assertTrue(response.getBody().error().contains(fieldName));
                }

                @Test
                void shouldReturnConflictStatusNameInResponse() {
                        // Arrange
                        String fieldName = "email";
                        DuplicateResourceException exception = new DuplicateResourceException(
                                        ExceptionsConstants.DUPLICATE_RESOURCE,
                                        fieldName);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.duplicateResource(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(HttpStatus.CONFLICT.name(), response.getBody().status());
                }

                @Test
                void shouldIncludeTimestampForDuplicateResource() {
                        // Arrange
                        String fieldName = "username";
                        DuplicateResourceException exception = new DuplicateResourceException(
                                        ExceptionsConstants.DUPLICATE_RESOURCE,
                                        fieldName);
                        LocalDateTime beforeTest = LocalDateTime.now();

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.duplicateResource(exception);
                        LocalDateTime afterTest = LocalDateTime.now();

                        // Assert
                        assert response.getBody() != null;
                        assertNotNull(response.getBody().timestamp());
                        assertTrue(response.getBody().timestamp().isAfter(beforeTest.minusSeconds(1)));
                        assertTrue(response.getBody().timestamp().isBefore(afterTest.plusSeconds(1)));
                }

                @Test
                void shouldHandleDuplicateResourceWithSimpleMessage() {
                        // Arrange
                        DuplicateResourceException exception = new DuplicateResourceException(
                                        "This resource already exists");

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.duplicateResource(exception);

                        // Assert
                        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                        assert response.getBody() != null;
                        assertEquals("This resource already exists", response.getBody().error());
                }

                @Test
                void shouldHandleDuplicateResourceForDifferentFields() {
                        // Arrange
                        String[] fields = { "titulo", "email", "username", "cpf" };

                        // Act & Assert
                        for (String field : fields) {
                                DuplicateResourceException exception = new DuplicateResourceException(
                                                ExceptionsConstants.DUPLICATE_RESOURCE,
                                                field);

                                ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                                .duplicateResource(exception);

                                assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                                assert response.getBody() != null;
                                assertTrue(response.getBody().error().contains(field));
                        }
                }
        }

        @Nested
        class HandleValidationErrors {

                @Test
                void shouldReturnUnprocessableContentStatusForValidationError() {
                        // Arrange
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationException("O título é obrigatório");

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);

                        // Assert
                        assertNotNull(response);
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
                        assertNotNull(response.getBody());
                }

                @Test
                void shouldReturnValidationErrorMessage() {
                        // Arrange
                        String validationMessage = "O título é obrigatório";
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationException(validationMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(validationMessage, response.getBody().error());
                }

                @Test
                void shouldReturnUnprocessableContentStatusNameInResponse() {
                        // Arrange
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationException("A descrição é obrigatória");

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT.name(), response.getBody().status());
                }

                @Test
                void shouldIncludeTimestampForValidationError() {
                        // Arrange
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationException("A duração é obrigatória");
                        LocalDateTime beforeTest = LocalDateTime.now();

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);
                        LocalDateTime afterTest = LocalDateTime.now();

                        // Assert
                        assert response.getBody() != null;
                        assertNotNull(response.getBody().timestamp());
                        assertTrue(response.getBody().timestamp().isAfter(beforeTest.minusSeconds(1)));
                        assertTrue(response.getBody().timestamp().isBefore(afterTest.plusSeconds(1)));
                }

                @Test
                void shouldReturnFirstFieldErrorWhenMultipleErrorsExist() {
                        // Arrange
                        List<String> fieldNames = Arrays.asList("titulo", "descricao");
                        List<String> messages = Arrays.asList(
                                        "O título é obrigatório",
                                        "A descrição é obrigatória");
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationExceptionWithMultipleErrors(
                                                        fieldNames,
                                                        messages);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals("O título é obrigatório", response.getBody().error());
                }

                @Test
                void shouldHandleMultipleDifferentValidationMessages() {
                        // Arrange
                        String[] validationMessages = {
                                        "O título é obrigatório",
                                        "A descrição é obrigatória",
                                        "A duração deve ser de pelo menos 1 minuto",
                                        "A classificação é obrigatória"
                        };

                        // Act & Assert
                        for (String message : validationMessages) {
                                MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                                .createValidationException(message);

                                ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                                .handleValidationErrors(exception);

                                assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
                                assert response.getBody() != null;
                                assertEquals(message, response.getBody().error());
                        }
                }

                @Test
                void shouldHandleValidationWithSpecificFieldName() {
                        // Arrange
                        String fieldName = "titulo";
                        String validationMessage = "O título é obrigatório";
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationException(fieldName, validationMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(validationMessage, response.getBody().error());
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
                }

                @Test
                void shouldHandleValidationErrorsWithBodyStructure() {
                        // Arrange
                        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionFactory
                                        .createValidationException(
                                                        "durationMinutes",
                                                        "A duração deve ser de pelo menos 1 minuto");

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleValidationErrors(exception);

                        // Assert
                        assertNotNull(response.getBody());
                        assertNotNull(response.getBody().error());
                        assertNotNull(response.getBody().status());
                        assertNotNull(response.getBody().timestamp());
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT.name(), response.getBody().status());
                }
        }

        @Nested
        class UnprocessableEntity {

                @Test
                void shouldReturnUnprocessableContentStatusWhenInvalidSeatPosition() {
                        // Arrange
                        String errorMessage = ExceptionsConstants.IMPOSSIBLE_SEAT_POSITION;
                        UnprocessableEntityException exception = new UnprocessableEntityException(errorMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleUnprocessableEntity(exception);

                        // Assert
                        assertNotNull(response);
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
                        assertNotNull(response.getBody());
                }

                @Test
                void shouldReturnCorrectErrorMessageForUnprocessableEntity() {
                        // Arrange
                        String errorMessage = ExceptionsConstants.IMPOSSIBLE_SEAT_POSITION;
                        UnprocessableEntityException exception = new UnprocessableEntityException(errorMessage);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleUnprocessableEntity(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(errorMessage, response.getBody().error());
                }

                @Test
                void shouldReturnUnprocessableContentStatusName() {
                        // Arrange
                        UnprocessableEntityException exception = new UnprocessableEntityException(
                                        ExceptionsConstants.IMPOSSIBLE_SEAT_POSITION);

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleUnprocessableEntity(exception);

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT.name(), response.getBody().status());
                }

                @Test
                void shouldIncludeTimestampForUnprocessableEntity() {
                        // Arrange
                        UnprocessableEntityException exception = new UnprocessableEntityException(
                                        ExceptionsConstants.IMPOSSIBLE_SEAT_POSITION);
                        LocalDateTime beforeTest = LocalDateTime.now();

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler
                                        .handleUnprocessableEntity(exception);
                        LocalDateTime afterTest = LocalDateTime.now();

                        // Assert
                        assert response.getBody() != null;
                        assertNotNull(response.getBody().timestamp());
                        assertTrue(response.getBody().timestamp().isAfter(beforeTest.minusSeconds(1)));
                        assertTrue(response.getBody().timestamp().isBefore(afterTest.plusSeconds(1)));
                }

                @Test
                void shouldHandleMultipleDifferentUnprocessableExceptions() {
                        // Arrange
                        String message1 = ExceptionsConstants.IMPOSSIBLE_SEAT_POSITION;
                        String message2 = ExceptionsConstants.DUPLICATE_SEAT_POSITION;

                        // Act & Assert
                        ResponseEntity<ErrorGlobalResponse> response1 = exceptionHandler
                                        .handleUnprocessableEntity(new UnprocessableEntityException(message1));
                        ResponseEntity<ErrorGlobalResponse> response2 = exceptionHandler
                                        .handleUnprocessableEntity(new UnprocessableEntityException(message2));

                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response1.getStatusCode());
                        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response2.getStatusCode());
                        assert response1.getBody() != null;
                        assertEquals(message1, response1.getBody().error());
                        assert response2.getBody() != null;
                        assertEquals(message2, response2.getBody().error());
                }
        }

        @Nested
        class HandleTypeMismatch {

                @Test
                void shouldReturnBadRequestStatusForTypeMismatch() {
                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.handleTypeMismatch();

                        // Assert
                        assertNotNull(response);
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());
                }

                @Test
                void shouldReturnDefaultErrorMessageForTypeMismatch() {
                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.handleTypeMismatch();

                        // Assert
                        assert response.getBody() != null;
                        assertEquals("BODY NAO COMPATIVEL", response.getBody().error());
                }

                @Test
                void shouldReturnBadRequestStatusNameInResponse() {
                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.handleTypeMismatch();

                        // Assert
                        assert response.getBody() != null;
                        assertEquals(HttpStatus.BAD_REQUEST.name(), response.getBody().status());
                }

                @Test
                void shouldIncludeTimestampForTypeMismatch() {
                        // Arrange
                        LocalDateTime beforeTest = LocalDateTime.now();

                        // Act
                        ResponseEntity<ErrorGlobalResponse> response = exceptionHandler.handleTypeMismatch();
                        LocalDateTime afterTest = LocalDateTime.now();

                        // Assert
                        assert response.getBody() != null;
                        assertNotNull(response.getBody().timestamp());
                        assertTrue(response.getBody().timestamp().isAfter(beforeTest.minusSeconds(1)));
                        assertTrue(response.getBody().timestamp().isBefore(afterTest.plusSeconds(1)));
                }
        }
}
