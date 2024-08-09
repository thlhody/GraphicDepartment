package cottontex.graphdep.models;

public class UserStatus {
    private Integer userId;
    private String username;
    private String startTime;
    private String endTime;

    public UserStatus(Integer userId, String username, String startTime, String endTime) {
        this.userId = userId;
        this.username = username;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}