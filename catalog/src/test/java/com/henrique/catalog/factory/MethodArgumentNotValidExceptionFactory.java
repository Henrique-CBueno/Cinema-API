package com.henrique.catalog.factory;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodArgumentNotValidExceptionFactory {

    public static MethodArgumentNotValidException createValidationException(String fieldName, String message) {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = createFieldError(fieldName, message);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        return exception;
    }

    public static MethodArgumentNotValidException createValidationException(String message) {
        return createValidationException("field", message);
    }

    public static MethodArgumentNotValidException createValidationExceptionWithMultipleErrors(
            List<String> fieldNames, 
            List<String> messages) {
        
        if (fieldNames.size() != messages.size()) {
            throw new IllegalArgumentException("fieldNames and messages must have the same size");
        }

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = new ArrayList<>();

        for (int i = 0; i < fieldNames.size(); i++) {
            fieldErrors.add(createFieldError(fieldNames.get(i), messages.get(i)));
        }

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        return exception;
    }

    private static FieldError createFieldError(String fieldName, String message) {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(message);
        when(fieldError.getField()).thenReturn(fieldName);
        return fieldError;
    }
}
