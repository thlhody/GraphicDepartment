package cottontex.graphdep.models;

public class UserStatus {
    private final int userId;
    private final String username;
    private final String startTime;
    private final String endTime;

    public UserStatus(int userId, String username, String startTime, String endTime) {
        this.userId = userId;
        this.username = username;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}