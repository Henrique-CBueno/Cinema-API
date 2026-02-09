package com.bsys.custumers.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateKeycloakUserRequest {

    private String username;
    private String email;
    private boolean enabled;
    private boolean emailVerified;
    private List<String> requiredActions;
}
