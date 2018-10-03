package capstone.gvsu.collegebudget;

public class User {

    public String username;
    public String email;
    public int age;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, int age) {
        this.age = age;
        this.username = username;
        this.email = email;
    }
}
