package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserStatus {
    private Integer userId;
    private String username;
    private String role;
    private String startTime;
    private String endTime;

    public UserStatus(Integer userId, String username, String role, String startTime, String endTime) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}