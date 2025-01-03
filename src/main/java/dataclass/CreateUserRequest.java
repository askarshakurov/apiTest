package dataclass;

public record CreateUserRequest(
        String username,
        String email,
        String password
) {}

