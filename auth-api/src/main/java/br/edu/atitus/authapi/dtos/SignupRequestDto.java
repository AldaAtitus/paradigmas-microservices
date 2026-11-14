package br.edu.atitus.authapi.dtos;

import br.edu.atitus.authapi.entities.UserType;

public record SignupRequestDto(String name, String email, String password, UserType type) {

}
