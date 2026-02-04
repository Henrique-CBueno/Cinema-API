# Documentação de Testes (catálogo)

## Visão geral

Este documento lista todos os testes implementados no módulo de catálogo e descreve o que cada conjunto cobre.

Test files:

- [src/test/java/com/henrique/catalog/service/MovieServiceTest.java](src/test/java/com/henrique/catalog/service/MovieServiceTest.java)
- [src/test/java/com/henrique/catalog/controller/MovieControllerTest.java](src/test/java/com/henrique/catalog/controller/MovieControllerTest.java)
- [src/test/java/com/henrique/catalog/infra/exceptionHandler/GlobalExceptionHandlerTest.java](src/test/java/com/henrique/catalog/infra/exceptionHandler/GlobalExceptionHandlerTest.java)
- [src/test/java/com/henrique/catalog/domain/mapper/MovieMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/MovieMapperTest.java)
- [src/test/java/com/henrique/catalog/service/CinemaServiceTest.java](src/test/java/com/henrique/catalog/service/CinemaServiceTest.java)
- [src/test/java/com/henrique/catalog/controller/CinemaControllerTest.java](src/test/java/com/henrique/catalog/controller/CinemaControllerTest.java)
- [src/test/java/com/henrique/catalog/domain/mapper/CinemaMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/CinemaMapperTest.java)
- [src/test/java/com/henrique/catalog/service/RoomsServiceTest.java](src/test/java/com/henrique/catalog/service/RoomsServiceTest.java)
- [src/test/java/com/henrique/catalog/controller/RoomsControllerTest.java](src/test/java/com/henrique/catalog/controller/RoomsControllerTest.java)
- [src/test/java/com/henrique/catalog/domain/mapper/RoomsMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/RoomsMapperTest.java)
- [src/test/java/com/henrique/catalog/service/SeatsServiceTest.java](src/test/java/com/henrique/catalog/service/SeatsServiceTest.java)
- [src/test/java/com/henrique/catalog/controller/SeatsControllerTest.java](src/test/java/com/henrique/catalog/controller/SeatsControllerTest.java)
- [src/test/java/com/henrique/catalog/domain/mapper/SeatMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/SeatMapperTest.java)
- [src/test/java/com/henrique/catalog/service/SessionServiceTest.java](src/test/java/com/henrique/catalog/service/SessionServiceTest.java)
- [src/test/java/com/henrique/catalog/controller/SessionControllerTest.java](src/test/java/com/henrique/catalog/controller/SessionControllerTest.java)
- [src/test/java/com/henrique/catalog/domain/mapper/SessionMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/SessionMapperTest.java)

---

## MovieServiceTest

Arquivo: [src/test/java/com/henrique/catalog/service/MovieServiceTest.java](src/test/java/com/henrique/catalog/service/MovieServiceTest.java)

### `getAllMovies(Pageable)`

- Retorna página com itens quando existem filmes.
- Retorna página vazia quando não há filmes.
- Retorna múltiplos filmes respeitando paginação.

### `getMovieById(UUID)`

- Retorna `MovieResDTO` quando o filme existe.
- Lança `NotFoundException` quando não existe.
- Mensagem de erro contém o `id` solicitado.

### `createMovie(CreateMovieReqDTO, String)`

- Cria filme com sucesso e retorna `UUID`.
- Define `createdByUserId` com o usuário informado.
- Lança `DuplicateResourceException` em violação de unicidade.
- Mensagem de erro inclui o campo duplicado.

### `deleteMovieById(UUID)`

- Exclui com sucesso quando há linhas afetadas.
- Lança `NotFoundException` quando não há linhas afetadas.
- Mensagem de erro contém o `id` solicitado.
- Lança `NotFoundException` quando retorno é negativo.

### `updatePartialMovie(UUID, UpdateMovieReqDTO)`

- Atualiza com sucesso e retorna `MovieResDTO`.
- Lança `NotFoundException` quando nenhum registro é atualizado.
- Lança `DuplicateResourceException` em violação de unicidade.
- Atualiza com dados parciais.
- Lança `NotFoundException` se o filme não existir após update.

---

## MovieControllerTest

Arquivo: [src/test/java/com/henrique/catalog/controller/MovieControllerTest.java](src/test/java/com/henrique/catalog/controller/MovieControllerTest.java)

### `getAllMovies(PaginationParams)`

- Retorna **200 OK** quando há itens.
- Retorna **204 NO_CONTENT** quando a lista está vazia.
- Envia parâmetros corretos de paginação ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).

### `getMovieById(UUID)`

- Retorna **200 OK**.
- Envia `id` correto ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).

### `createMovie(CreateMovieReqDTO, String)`

- Retorna **201 CREATED** e `Location` preenchido.
- Envia `dto` e `userId` corretos ao serviço.
- Retorna `Location` com o `id` criado.

### `deleteMovieById(UUID)`

- Retorna **204 NO_CONTENT**.
- Envia `id` correto ao serviço.
- Chama método de exclusão do serviço.

### `partialUpdateMovie(UUID, UpdateMovieReqDTO)`

- Retorna **200 OK**.
- Envia `id` e `dto` corretos ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).

---

## GlobalExceptionHandlerTest

Arquivo: [src/test/java/com/henrique/catalog/infra/exceptionHandler/GlobalExceptionHandlerTest.java](src/test/java/com/henrique/catalog/infra/exceptionHandler/GlobalExceptionHandlerTest.java)

### `movieDontExists(NotFoundException)`

- Retorna **404 NOT_FOUND**.
- Retorna mensagem de erro formatada.
- Retorna `status` com o nome do HTTP status.
- Inclui `timestamp` válido.
- Suporta diferentes mensagens/IDs.

### `duplicateResource(DuplicateResourceException)`

- Retorna **409 CONFLICT**.
- Retorna mensagem contendo o campo duplicado.
- Retorna `status` com o nome do HTTP status.
- Inclui `timestamp` válido.
- Suporta mensagem simples e múltiplos campos.

### `handleValidationErrors(MethodArgumentNotValidException)`

- Retorna **422 UNPROCESSABLE_CONTENT**.
- Retorna a mensagem do primeiro `FieldError`.
- Retorna `status` com o nome do HTTP status.
- Inclui `timestamp` válido.
- Suporta diferentes mensagens de validação.
- Valida estrutura de `ErrorGlobalResponse`.

### `handleUnprocessableEntity(UnprocessableEntityException)`

- Retorna **422 UNPROCESSABLE_CONTENT**.
- Retorna mensagem de erro correta.
- Retorna `status` com o nome do HTTP status.
- Inclui `timestamp` válido.
- Suporta diferentes mensagens de erro.

---

## MovieMapperTest

Arquivo: [src/test/java/com/henrique/catalog/domain/mapper/MovieMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/MovieMapperTest.java)

### `toResponse(MovieEntity)`

- Mapeia todos os campos da entidade para o DTO.
- Retorna `null` quando a entidade é `null`.
- Mapeia corretamente valores específicos (id e campos customizados).

### `toEntity(CreateMovieReqDTO)`

- Mapeia todos os campos do DTO para a entidade.
- Retorna `null` quando o DTO é `null`.
- Não mapeia `id` ao criar entidade.
- Não mapeia `createdByUserId` ao criar entidade.

---

## CinemaServiceTest

Arquivo: [src/test/java/com/henrique/catalog/service/CinemaServiceTest.java](src/test/java/com/henrique/catalog/service/CinemaServiceTest.java)

### `getAllCinemas(Pageable)`

- Retorna página com itens quando existem cinemas.
- Retorna página vazia quando não há cinemas.
- Retorna múltiplos cinemas respeitando paginação.
- Mapeia todas as entidades corretamente.
- Respeita parâmetros do `Pageable` (page number e size).

### `getCinemaById(UUID)`

- Retorna `CinemaResDTO` quando o cinema existe.
- Lança `NotFoundException` quando não existe.
- Mensagem de erro contém o `id` solicitado.
- Mapeia entidade para DTO corretamente.
- Suporta diferentes IDs de cinema.

### `createCinema(CreateCinemaReqDTO, UUID)`

- Cria cinema com sucesso e retorna `UUID`.
- Define `createdByUserId` com o usuário informado.
- Lança `DuplicateResourceException` em violação de unicidade (nome e cidade).
- Mapeia DTO para entidade corretamente.
- Retorna o `UUID` do cinema criado.

### `partialUpdate(UUID, UpdateCinemaReqDTO)`

- Atualiza cinema com sucesso e retorna `CinemaResDTO`.
- Lança `NotFoundException` quando cinema não existe para atualização.
- Lança `DuplicateResourceException` quando nome e cidade já existem.
- Atualiza cinema com dados parciais.
- Lança `NotFoundException` se cinema não for encontrado após atualização.

### `safeDeleteById(UUID)`

- Exclui cinema com sucesso quando há linhas afetadas.
- Lança `NotFoundException` quando não há linhas afetadas.
- Mensagem de erro contém o `id` solicitado.
- Lança `NotFoundException` quando retorno é negativo.

---

## CinemaControllerTest

Arquivo: [src/test/java/com/henrique/catalog/controller/CinemaControllerTest.java](src/test/java/com/henrique/catalog/controller/CinemaControllerTest.java)

### `getAllCinemas(PaginationParams)`

- Retorna **200 OK** quando há itens.
- Retorna **204 NO_CONTENT** quando a lista está vazia.
- Envia parâmetros corretos de paginação ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).
- Retorna tamanho correto do conteúdo.
- Chama serviço uma única vez.
- Suporta diferentes tamanhos de página.

### `getCinemaById(UUID)`

- Retorna **200 OK**.
- Envia `id` correto ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).
- Retorna cinema com `id` correto.
- Chama serviço uma única vez.

### `createCinema(CreateCinemaReqDTO, String)`

- Retorna **201 CREATED**.
- Envia `dto` e `userId` corretos ao serviço.
- Retorna `Location` com o `id` criado.
- Suporta criação com dados diferentes.

### `partialUpdateCinema(UUID, UpdateCinemaReqDTO)`

- Retorna **200 OK**.
- Retorna `SuccessResponse` com cinema atualizado.
- Envia `id` correto ao serviço.
- Envia `UpdateCinemaReqDTO` correto ao serviço.
- Atualiza cinema com valores diferentes.

### `safeDeleteCinema(UUID)`

- Retorna **204 NO_CONTENT**.
- Envia `id` correto ao serviço.
- Chama método de exclusão do serviço.
- Suporta exclusão de diferentes IDs de cinema.

---

## CinemaMapperTest

Arquivo: [src/test/java/com/henrique/catalog/domain/mapper/CinemaMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/CinemaMapperTest.java)

### `toDTO(CinemaEntity)`

- Mapeia todos os campos da entidade para o DTO.
- Retorna `null` quando a entidade é `null`.
- Mapeia o `id` da entidade para o `id` do DTO.
- Mantém integridade dos dados (`name`, `city`).

### `toEntity(CreateCinemaReqDTO)`

- Mapeia todos os campos do DTO para a entidade (`name`, `city`).
- Retorna `null` quando o DTO é `null`.
- Mapeia corretamente diferentes DTOs de cinema.
- Mantém integridade dos dados (`name`, `city`).
- Ignora campos gerados/gerenciados (`id`, `createdByUserId`, `active`, `createdAt`).

---

## RoomsServiceTest

Arquivo: [src/test/java/com/henrique/catalog/service/RoomsServiceTest.java](src/test/java/com/henrique/catalog/service/RoomsServiceTest.java)

### `getAllRooms(Pageable, UUID)`

- Retorna página com itens quando existem salas.
- Retorna página vazia quando não há salas para o cinema.
- Respeita parâmetros do `Pageable` (tamanho e número da página).
- Mapeia todas as entidades para DTOs corretamente.
- Mapeia dados do cinema para cada sala.
- Chama `findByCinemaId` com os parâmetros corretos.
- Retorna uma única sala quando apenas uma existe.
- Chama o mapper do cinema para cada sala retornada.

### `getRoomByCinemaIdAndRoomId(UUID, UUID)`

- Retorna `RoomsResDTO` quando a sala existe no cinema.
- Lança `NotFoundException` quando a sala não existe.
- Mensagem de erro contém o `roomId` e `cinemaId` solicitados.
- Chama repository com os parâmetros corretos.
- Mapeia `RoomEntity` para DTO corretamente.
- Lança exceção com formato de mensagem correto.

### `getRoomByCinemaIdAndRoomIdReturningEntity(UUID, UUID)`

- Retorna `RoomEntity` quando a sala existe no cinema.
- Lança `NotFoundException` quando a sala não existe.
- Mensagem de erro contém o `roomId` e `cinemaId` solicitados.
- Chama repository com os parâmetros corretos.

### `createRoomForCinemaId(UUID, CreateRoomReqDTO, UUID)`

- Cria sala com sucesso e retorna `UUID`.
- Define `cinema` e `createdByUserId` corretamente.
- Chama o mapper para criar a entidade.
- Lança `DuplicateResourceException` em violação de unicidade.
- Mensagem de erro contém o campo duplicado: "Ja existe uma sala com o nome".

### `updateRoom(UUID, UUID, UpdateRoomReqDTO)`

- Atualiza sala com sucesso e retorna `RoomsResDTO`.
- Lança `NotFoundException` quando nenhum registro é atualizado (affectedRows < 1).
- Lança `NotFoundException` quando a sala não é encontrada após a atualização.
- Lança `DuplicateResourceException` quando o nome já existe no cinema.
- Mensagem de erro de duplicação contém: "Ja existe uma sala com o nome".
- Passa parâmetros corretos ao repositório (cinemaId, roomId, name, totalRows, totalColumns).
- Retorna dados atualizados corretamente.

### `deleteRoomFromCinema(UUID, UUID)`

- Exclui sala com sucesso quando há linhas afetadas.
- Lança `NotFoundException` quando não há linhas afetadas (affectedRows < 1).
- Mensagem de erro contém o `roomId` e `cinemaId` solicitados.
- Lança `NotFoundException` quando retorno é negativo.

---

## SeatsServiceTest

Arquivo: [src/test/java/com/henrique/catalog/service/SeatsServiceTest.java](src/test/java/com/henrique/catalog/service/SeatsServiceTest.java)

### `getSeatsByCinemaRoom(UUID, Pageable)`

- Retorna página com itens quando existem assentos.
- Retorna página vazia quando não há assentos para a sala.
- Respeita parâmetros do `Pageable` (tamanho e número da página).
- Mapeia todas as entidades para DTOs corretamente.
- Chama `findAllByRoomId` com os parâmetros corretos.
- Retorna um único assento quando apenas um existe.

### `createSeatsInCinemaRoom(UUID, UUID, List<CreateSeatReqDTO>, UUID)`

- Cria assentos com sucesso.
- Valida posição de assento inválida (coluna) com `UnprocessableEntityException`.
- Valida posição de assento inválida (fileira) com `UnprocessableEntityException`.
- Lança `DuplicateResourceException` quando já existe assento ativo na posição.
- Define `createdByUserId` em todos os assentos criados.
- Define `room` em todos os assentos criados.

### `deleteSeatFromRoom(UUID, UUID, UUID)`

- Exclui assento com sucesso quando há linhas afetadas.
- Lança `NotFoundException` quando não há linhas afetadas (affectedRows < 1).
- Mensagem de erro contém `seatId`, `roomId` e `cinemaId`.
- Lança `NotFoundException` quando retorno é negativo.

---

## SeatsControllerTest

Arquivo: [src/test/java/com/henrique/catalog/controller/SeatsControllerTest.java](src/test/java/com/henrique/catalog/controller/SeatsControllerTest.java)

### `getAllSeatsByCinemaRoom(String, String, PaginationParams)`

- Retorna **200 OK** quando há assentos.
- Retorna **204 NO_CONTENT** quando a lista de assentos está vazia.
- Envia parâmetros corretos de paginação ao serviço.
- Envia `roomId` correto ao serviço.
- Retorna múltiplos assentos com tamanho correto.
- Retorna `SuccessListDataResponse` com estrutura correta (content, page, pageSize, totalElements).
- Suporta diferentes tamanhos de página.
- Retorna assentos com informações detalhadas (`id`, `roomId`, `rowLabel`, `columnNumber`).
- Chama o serviço uma única vez por requisição.

### `createSeatsInCinemaRoom(String, String, List<CreateSeatReqDTO>, String)`

- Retorna **201 CREATED**.
- Envia `cinemaId`, `roomId`, lista de assentos e `userId` corretos ao serviço.
- Retorna `Location` preenchido.
- Suporta criação de múltiplos assentos.

### `deleteSeat(String, String, String)`

- Retorna **204 NO_CONTENT**.
- Envia `cinemaId`, `roomId` e `seatId` corretos ao serviço.
- Chama método de exclusão do serviço.
- Suporta exclusão de diferentes IDs de assentos.

---

## SeatMapperTest

Arquivo: [src/test/java/com/henrique/catalog/domain/mapper/SeatMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/SeatMapperTest.java)

### `toDTO(SeatEntity)`

- Mapeia todos os campos da entidade para o DTO (`id`, `roomId`, `rowLabel`, `columnNumber`).
- Retorna `null` quando a entidade é `null`.
- Mapeia assento com `id` específico corretamente.
- Mapeia `roomId` corretamente a partir da entidade `Room`.
- Mapeia todos os campos sem perda de dados.
- Mapeia diferentes `rowLabel` (A, B, C, Z, etc.).
- Mapeia `columnNumber` como `String` corretamente.
- Preserva integridade de todos os dados mapeados.

### `toEntity(CreateSeatReqDTO)`

- Mapeia todos os campos do DTO para a entidade (`rowLabel`, `columnNumber`).
- Retorna `null` quando o DTO é `null`.
- Não mapeia `id` ao criar entidade.
- Não mapeia `room` ao criar entidade.
- Não mapeia `createdByUserId` ao criar entidade.
- Mapeia diferentes `rowLabel` e `columnNumber` corretamente.

---

## SessionServiceTest

Arquivo: [src/test/java/com/henrique/catalog/service/SessionServiceTest.java](src/test/java/com/henrique/catalog/service/SessionServiceTest.java)

### `getSessions(Pageable, GetAllSessionParamsDTO)`

- Retorna página com itens quando existem sessões.
- Retorna página vazia quando não há sessões.
- Calcula `startOfDay` e `endOfDay` quando `date` é informado.
- Envia `startOfDay` e `endOfDay` como `null` quando `date` é `null`.
- Chama o repositório com parâmetros corretos.
- Mapeia todas as entidades para DTOs corretamente.

### `getSessionById(UUID)`

- Retorna `SessionResDTO` quando a sessão existe.
- Lança `NotFoundException` quando a sessão não existe.
- Mensagem de erro contém o `id` solicitado.
- Chama o repositório com o `id` correto.

---

## SessionControllerTest

Arquivo: [src/test/java/com/henrique/catalog/controller/SessionControllerTest.java](src/test/java/com/henrique/catalog/controller/SessionControllerTest.java)

### `getAllSession(PaginationParams, GetAllSessionParamsDTO)`

- Retorna **200 OK** quando há sessões.
- Retorna **204 NO_CONTENT** quando a lista está vazia.
- Envia parâmetros corretos de paginação e filtros ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).

### `getSession(String)`

- Retorna **200 OK**.
- Envia `sessionId` correto ao serviço.
- Retorna corpo correto (comparação ignorando `timestamp`).

---

## SessionMapperTest

Arquivo: [src/test/java/com/henrique/catalog/domain/mapper/SessionMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/SessionMapperTest.java)

### `toDTO(SessionEntity)`

- Mapeia todos os campos da entidade para o DTO (`id`, `movie`, `room`, `startTime`, `endTime`, `price`, `sessionStatus`).
- Retorna `null` quando a entidade é `null`.
- Mapeia diferentes `SessionStatus` corretamente.

---

## RoomsControllerTest

Arquivo: [src/test/java/com/henrique/catalog/controller/RoomsControllerTest.java](src/test/java/com/henrique/catalog/controller/RoomsControllerTest.java)

### `getAllRoomsFromCinemaId(UUID, PaginationParams)`

- Retorna **200 OK** quando há salas.
- Retorna **204 NO_CONTENT** quando a lista de salas está vazia.
- Envia parâmetros corretos de paginação ao serviço.
- Envia `cinemaId` correto ao serviço.
- Retorna múltiplas salas com tamanho correto.
- Retorna `SuccessListDataResponse` com estrutura correta (content, page, pageSize, totalElements).
- Suporta diferentes tamanhos de página.
- Retorna salas com informações detalhadas (`id`, `name`, `totalRows`, `totalColumns`, cinema).
- Chama o serviço uma única vez por requisição.

### `getRoomFromCinemaByRoomId(UUID, UUID)`

- Retorna **200 OK** quando a sala existe.
- Passa `cinemaId` e `roomId` corretos ao serviço.
- Retorna dados corretos da sala (`id`, `name`).
- Retorna `SuccessResponse` com estrutura correta (data, timestamp).
- Chama o serviço apenas uma vez.

### `createRoomForCinemaId(UUID, CreateRoomReqDTO, String)`

- Retorna **201 CREATED**.
- Envia `cinemaId`, `dto` e `userId` corretos ao serviço.
- Retorna `Location` com o `id` criado.
- Suporta criação com dados diferentes.

### `deleteRoomFromCinema(UUID, UUID)`

- Retorna **204 NO_CONTENT**.
- Envia `roomId` e `cinemaId` corretos ao serviço.
- Chama método de exclusão do serviço.
- Suporta exclusão de diferentes IDs de salas.

---

## RoomsMapperTest

Arquivo: [src/test/java/com/henrique/catalog/domain/mapper/RoomsMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/RoomsMapperTest.java)

### `toDTO(RoomEntity)`

- Mapeia todos os campos da entidade para o DTO (`id`, `name`, `totalRows`, `totalColumns`).
- Retorna `null` quando a entidade é `null`.
- Mapeia sala com `id` específico corretamente.
- Mapeia dados do cinema corretamente (`cinemaResDTO`).
- Mapeia todos os campos sem perda de dados.
- Mapeia diferentes nomes de sala.
- Mapeia capacidade da sala corretamente (`totalRows`, `totalColumns`).
- Preserva integridade de todos os dados mapeados.

### `toEntity(CreateRoomReqDTO)`

- Mapeia todos os campos do DTO para a entidade (`name`, `totalRows`, `totalColumns`).
- Retorna `null` quando o DTO é `null`.
- Não mapeia `id` ao criar entidade.
- Não mapeia `createdByUserId` ao criar entidade.
