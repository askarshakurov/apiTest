package dataclass;

import java.util.List;

public record CreateUserErrorResponse(
        boolean success,
        List<String> message
) {}