package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class UserOffline {
    private int userId;
    private String name;
    private int employeeId;
    private String username;
    private String password;
    private String role;

    // Default constructor
    public UserOffline() {}

    // Constructor with fields
    public UserOffline(int userId, String name, int employeeId, String username, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOffline that = (UserOffline) o;
        return userId == that.userId &&
                employeeId == that.employeeId &&
                Objects.equals(name, that.name) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, employeeId, username, password, role);
    }
}