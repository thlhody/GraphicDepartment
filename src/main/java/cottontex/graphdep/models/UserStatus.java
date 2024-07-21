package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatus {
    private int userId;
    private String username;
    private String startTime;
    private String endTime;

    public UserStatus(Integer userId, String username, String startTime, String endTime) {
        this.userId = userId;
        this.username = username;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}