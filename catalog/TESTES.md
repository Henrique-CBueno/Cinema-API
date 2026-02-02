# Documentação de Testes (catálogo)

## Visão geral
Este documento lista todos os testes implementados no módulo de catálogo e descreve o que cada conjunto cobre.

Test files:
- [src/test/java/com/henrique/catalog/service/MovieServiceTest.java](src/test/java/com/henrique/catalog/service/MovieServiceTest.java)
- [src/test/java/com/henrique/catalog/controller/MovieControllerTest.java](src/test/java/com/henrique/catalog/controller/MovieControllerTest.java)
- [src/test/java/com/henrique/catalog/infra/exceptionHandler/GlobalExceptionHandlerTest.java](src/test/java/com/henrique/catalog/infra/exceptionHandler/GlobalExceptionHandlerTest.java)
- [src/test/java/com/henrique/catalog/domain/mapper/MovieMapperTest.java](src/test/java/com/henrique/catalog/domain/mapper/MovieMapperTest.java)

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
