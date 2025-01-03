package dataclass;

import java.util.List;

public record GetUserResponse(
        List<UserDetails> users
) {
    public record UserDetails(
            Integer id,
            String username,
            String email,
            String password,
            String created_at,
            String updated_at
    ) {}
}
