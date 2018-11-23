import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private ServerSocket svSocket;
    private int port;
    private HashMap<String, User> users;

    public Server(int port) {

        this.port = port;
        this.users = new HashMap<>();
    }

    public void startServer() {

        try {

            this.svSocket = new ServerSocket(this.port);
            System.out.println("#### SERVER ####");

            while (true) {

                System.out.println("Server -> Server is waiting for new connection!");

                Socket client = svSocket.accept();

                System.out.println("Server -> Connection received! Creating worker thread...");

                ServerWorker sw = new ServerWorker(client, this);
                new Thread(sw).start();

                System.out.println("Server -> Worker thread created!");

            }

        } catch (IOException ioex) {

            ioex.printStackTrace();
        }

    }

    // evitar logins tbm ao mesmo tempo? neste caso verificar a flag loggedIn
    public boolean login(String username, String password) {

        if (!this.users.containsKey(username)) {

            return false;
        }

        if (this.users.get(username).getPassword().equals(password)) {

            this.users.get(username).setLoggedIn(true);

            return true;
        }

        return false;

    }

    // evitar registos ao mesmo tempo :P
    public synchronized boolean register(String username, String password) {

        if (!this.users.containsKey(username)) {

            User newUser = new User(username, password);
            this.users.put(username, newUser);

            return true;
        }

        return false;

    }

    public static void main(String[] args) {

        Server s = new Server(12345);

        // start server
        s.startServer();

    }


}