package br.one.forum.dtos;

import br.one.forum.entities.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
