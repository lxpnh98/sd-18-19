public class User {

    private String username;
    private String password;
    private boolean loggedIn;

    public User(String user, String pass) {

        this.username = user;
        this.password = pass;

    }

    public String getUsername() {

        return this.username;
    }

    public String getPassword() {

        return this.password;
    }

    public void setLoggedIn(boolean status) {

        this.loggedIn = status;
    }

    public boolean isLoggedIn() {

        return this.loggedIn;
    }



}
