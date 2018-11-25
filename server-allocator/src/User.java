public class User {

    private String username;
    private String password;
    private float balance;
    private boolean loggedIn;

    public User(String user, String pass) {

        this.username = user;
        this.password = pass;
        this.balance = 0;
        this.loggedIn = false;

    }

    public String getUsername() {

        return this.username;
    }

    public String getPassword() {

        return this.password;
    }

    public float getBalance() {

        return this.balance;
    }

    public void setLoggedIn(boolean status) {

        this.loggedIn = status;
    }

    public boolean isLoggedIn() {

        return this.loggedIn;
    }



}
