package esu.visionary.domain.user.model;

public class User {

    private Long userId;
    private String id;
    private String password;
    private String nickName;

    public User() {}

    public User(Long userId, String id, String password, String nickName) {
        this.userId = userId;
        this.id = id;
        this.password = password;
        this.nickName = nickName;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
}
