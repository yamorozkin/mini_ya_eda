package delivery.external;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.bind.annotation.RequestHeader;
@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/api/v1/users"
)
public interface UserHttpClient {

    @GetExchange("/me")
    UserProfileResponse getMyProfile(@RequestHeader("Authorization") String authorizationHeader);
}
