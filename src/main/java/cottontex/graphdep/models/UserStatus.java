package cottontex.graphdep.models;

public class UserStatus {
    private final int userId;
    private final String username;
    private final boolean isOnline;
    private final String startTime;
    private final String endTime;

    public UserStatus(Integer userId, String username, boolean isOnline, String startTime, String endTime) {
        this.userId = userId;
        this.username = username;
        this.isOnline = isOnline;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public boolean isOnline() { return isOnline; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}