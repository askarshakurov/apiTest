package dataclass;

public record CreateUserResponse(
        boolean success,
        Details details,
        String message
) {
    public record Details(
            String username,
            String email,
            String password,
            String created_at,
            String updated_at,
            Integer id
    ) {}
}