import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private ServerSocket svSocket;
    private int port;

    private HashMap<String, User> users;
    private HashMap<String, ServerProduct> servers;
    private HashMap<String, Thread> auctions;

    public Server(int port) {

        this.port = port;
        this.users = new HashMap<>();
        this.servers = new HashMap<>();
        this.auctions = new HashMap<>();

    }

    public void startServer() {

        try {

            this.svSocket = new ServerSocket(this.port);
            System.out.println("#### SERVER ####");

            this.createTestServers();

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

    // TODO método para alugar servidor fixo
    // neste método, em caso de indisponibilidade de servidores do tipo pedido,
    // poderão ser canceladas reservas concedidas em leilão para obter o servidor pretendido.

    private void createAuctionServer(String svID) throws IOException {

        ServerProduct server = this.servers.get(svID);
        server.changeType("auction");

        Auction newAuction = new Auction(server, 10);

        Thread t1 = new Thread(newAuction);
        t1.start();

        this.auctions.put(server.getID(), t1);

    }

    public void bidAuctionServer(Auction auction) {



    }


    // TODO método para reservar instância em leilão

    public void freeServer(String serverID) {

        this.servers.get(serverID).changeStatus(0);

    }

    private void rentServer() {



    }


    public User getUser(String username) {

        return this.users.get(username);
    }

    private void createServer(String id, float price, String type) {

        ServerProduct sp = new ServerProduct(id, price, type);

        this.servers.put(id, sp);

    }

    private void createTestServers() throws IOException {

        // creating test servers
        this.createServer("f1.micro", 20, "fixed");
        this.createServer("f2.micro", 20, "fixed");
        this.createServer("f3.micro", 20, "fixed");
        this.createServer("f4.micro", 20, "fixed");
        this.createServer("f5.micro", 20, "fixed");
        this.createServer("f1.normal", 40, "fixed");
        this.createServer("f2.normal", 40, "fixed");
        this.createServer("f3.normal", 40, "fixed");
        this.createServer("f4.normal", 40, "fixed");
        this.createServer("f5.normal", 40, "fixed");
        this.createServer("f1.large", 60, "fixed");
        this.createServer("f2.large", 60, "fixed");
        this.createServer("f3.large", 60, "fixed");
        this.createServer("f4.large", 60, "fixed");
        this.createServer("f5.large", 60, "fixed");

        this.createServer("a1.micro", 30, "fixed");
        this.createServer("a2.micro", 30, "fixed");
        this.createServer("a3.micro", 30, "fixed");
        this.createServer("a4.micro", 30, "fixed");
        this.createServer("a5.micro", 30, "fixed");
        this.createServer("a1.normal", 50, "fixed");
        this.createServer("a2.normal", 50, "fixed");
        this.createServer("a3.normal", 50, "fixed");
        this.createServer("a4.normal", 50, "fixed");
        this.createServer("a5.normal", 50, "fixed");
        this.createServer("a1.large", 70, "fixed");
        this.createServer("a2.large", 70, "fixed");
        this.createServer("a3.large", 70, "fixed");
        this.createServer("a4.large", 70, "fixed");
        this.createServer("a5.large", 70, "fixed");

        this.createAuctionServer("a1.micro");
        this.createAuctionServer("a2.micro");
        this.createAuctionServer("a3.micro");
        this.createAuctionServer("a4.micro");
        this.createAuctionServer("a5.micro");

    }

    public static void main(String[] args) {

        Server s = new Server(12345);

        // start server
        s.startServer();

    }


}