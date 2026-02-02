package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.factory.CinemaFactory;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.CinemaService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaControllerTest {

    @Mock
    private CinemaService cinemaService;

    @InjectMocks
    private CinemaController cinemaController;

    @Captor
    ArgumentCaptor<PageRequest> paginationCapture;

    @Captor
    ArgumentCaptor<UUID> uuidCapture;

    @Nested
    class GetAllCinemas {

        @Test
        void shouldGetAllCinemasWithHttpOK() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 5);
            CinemaResDTO cinemaDTO = CinemaFactory.createCinemaResponseDTO();
            Page<CinemaResDTO> cinemaPage = new PageImpl<>(List.of(cinemaDTO));

            when(cinemaService.getAllCinemas(paginationParams.toPageable()))
                    .thenReturn(cinemaPage);

            // Act
            ResponseEntity<SuccessListDataResponse> response = cinemaController.getAllCinemas(paginationParams);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void shouldGetAllCinemasWithHttpNO_CONTENT() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 5);
            Page<CinemaResDTO> emptyPage = new PageImpl<>(List.of());

            when(cinemaService.getAllCinemas(paginationParams.toPageable()))
                    .thenReturn(emptyPage);

            // Act
            ResponseEntity<SuccessListDataResponse> response = cinemaController.getAllCinemas(paginationParams);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParametersToService() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 10);
            Page<CinemaResDTO> emptyPage = new PageImpl<>(List.of());

            when(cinemaService.getAllCinemas(paginationCapture.capture()))
                    .thenReturn(emptyPage);

            // Act
            ResponseEntity<SuccessListDataResponse> response = cinemaController.getAllCinemas(paginationParams);

            // Assert
            assertEquals(1, paginationCapture.getAllValues().size());
            assertEquals(paginationParams.page(), paginationCapture.getValue().getPageNumber());
            assertEquals(paginationParams.pageSize(), paginationCapture.getValue().getPageSize());
        }

        @Test
        void shouldReturnResponseBodyCorrect() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 5);
            CinemaResDTO cinema1 = CinemaFactory.createCinemaResponseDTO("Cinemark", "São Paulo");
            CinemaResDTO cinema2 = CinemaFactory.createCinemaResponseDTO("UCI", "Rio de Janeiro");
            Page<CinemaResDTO> cinemaPage = new PageImpl<>(List.of(cinema1, cinema2));

            when(cinemaService.getAllCinemas(paginationParams.toPageable()))
                    .thenReturn(cinemaPage);

            var expectedResponse = ResponseEntity.ok(new SuccessListDataResponse(
                    cinemaPage.getContent(),
                    cinemaPage.getNumber(),
                    cinemaPage.getSize(),
                    cinemaPage.getTotalElements()));

            // Act
            ResponseEntity<SuccessListDataResponse> response = cinemaController.getAllCinemas(paginationParams);

            // Assert
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);
        }

        @Test
        void shouldReturnCorrectContentSize() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 3);
            CinemaResDTO cinema1 = CinemaFactory.createCinemaResponseDTO("Cinema 1", "Cidade 1");
            CinemaResDTO cinema2 = CinemaFactory.createCinemaResponseDTO("Cinema 2", "Cidade 2");
            CinemaResDTO cinema3 = CinemaFactory.createCinemaResponseDTO("Cinema 3", "Cidade 3");
            Page<CinemaResDTO> cinemaPage = new PageImpl<>(List.of(cinema1, cinema2, cinema3));

            when(cinemaService.getAllCinemas(paginationParams.toPageable()))
                    .thenReturn(cinemaPage);

            // Act
            ResponseEntity<SuccessListDataResponse> response = cinemaController.getAllCinemas(paginationParams);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(3, response.getBody().data().size());
        }

        @Test
        void shouldCallServiceOnce() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(1, 10);
            Page<CinemaResDTO> cinemaPage = new PageImpl<>(List.of());

            when(cinemaService.getAllCinemas(any()))
                    .thenReturn(cinemaPage);

            // Act
            cinemaController.getAllCinemas(paginationParams);

            // Assert
            verify(cinemaService, times(1)).getAllCinemas(any());
        }

        @Test
        void shouldHandleDifferentPageSizes() {
            // Arrange
            PaginationParams params5 = new PaginationParams(0, 5);
            PaginationParams params10 = new PaginationParams(0, 10);
            Page<CinemaResDTO> page = new PageImpl<>(List.of());

            when(cinemaService.getAllCinemas(any()))
                    .thenReturn(page);

            // Act
            cinemaController.getAllCinemas(params5);
            cinemaController.getAllCinemas(params10);

            // Assert
            verify(cinemaService, times(2)).getAllCinemas(any());
        }
    }


    @Nested
    class GetCinemaById {

        @Test
        void shouldReturnHttpOK() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaResDTO cinemaDTO = CinemaFactory.createCinemaResponseDTO(cinemaId, "Cinemark", "São Paulo");

            when(cinemaService.getCinemaById(cinemaId))
                    .thenReturn(cinemaDTO);

            // Act
            ResponseEntity<SuccessResponse> response = cinemaController.getCinemaById(cinemaId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParametersToService() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaResDTO cinemaDTO = CinemaFactory.createCinemaResponseDTO(cinemaId, "UCI", "Rio de Janeiro");

            when(cinemaService.getCinemaById(uuidCapture.capture()))
                    .thenReturn(cinemaDTO);

            // Act
            ResponseEntity<SuccessResponse> response = cinemaController.getCinemaById(cinemaId);

            // Assert
            assertEquals(cinemaId, uuidCapture.getValue());
            assertEquals(1, uuidCapture.getAllValues().size());
        }

        @Test
        void shouldReturnCorrectResponseBody() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaResDTO cinemaDTO = CinemaFactory.createCinemaResponseDTO(cinemaId, "Kinoplex", "Brasília");
            var expectedResponse = ResponseEntity.ok(new SuccessResponse(cinemaDTO));

            when(cinemaService.getCinemaById(cinemaId))
                    .thenReturn(cinemaDTO);

            // Act
            ResponseEntity<SuccessResponse> response = cinemaController.getCinemaById(cinemaId);

            // Assert
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);
        }

        @Test
        void shouldReturnCinemaWithCorrectId() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaResDTO cinemaDTO = CinemaFactory.createCinemaResponseDTO(cinemaId, "Cinépolis", "Curitiba");

            when(cinemaService.getCinemaById(cinemaId))
                    .thenReturn(cinemaDTO);

            // Act
            ResponseEntity<SuccessResponse> response = cinemaController.getCinemaById(cinemaId);

            // Assert
            assertNotNull(response.getBody());
            CinemaResDTO returnedCinema = (CinemaResDTO) response.getBody().data();
            assertEquals(cinemaId, returnedCinema.id());
        }

        @Test
        void shouldCallServiceOnce() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaResDTO cinemaDTO = CinemaFactory.createCinemaResponseDTO(cinemaId, "Cine Araújo", "Belo Horizonte");

            when(cinemaService.getCinemaById(any()))
                    .thenReturn(cinemaDTO);

            // Act
            cinemaController.getCinemaById(cinemaId);

            // Assert
            verify(cinemaService, times(1)).getCinemaById(cinemaId);
        }
    }

}
