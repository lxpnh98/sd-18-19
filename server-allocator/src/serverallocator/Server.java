package serverallocator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    private ServerSocket svSocket;
    private int port;

    private HashMap<String, User> users;
    private HashMap<String, ServerProduct> servers;

    public Server(int port) {

        this.port = port;
        this.users = new HashMap<>();
        this.servers = new HashMap<>();

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

    // MÉTODO LOGIN
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

    // MÉTODO REGISTO
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

    // TODO reservar instancia em leilao
    public void bidAuctionServer(String id) {



    }


    // TODO método para reservar instância em leilão

    // TODO: método para libertar servidor
    public void freeServer(String serverID) {

    }

    // TODO alugar servidor fixo
    private void rentServer() {

    }

    // retorna lista de servidores fixos
    public ArrayList<ServerProduct> getListOfFixedServers() {

        ArrayList<ServerProduct> list = new ArrayList<>();

        for (ServerProduct sp : servers.values()) {

            //if (sp.getStatus() == 0 && sp.getType().equals("fixed")) {

                list.add(sp);
            //}
        }

        return list;

    }

    // retorna lista de servidores a leilao
    public ArrayList<ServerProduct> getListOfAuctionServers() {

        ArrayList<ServerProduct> list = new ArrayList<>();

        for (ServerProduct sp : servers.values()) {

            //if (sp.getStatus() == 0 && sp.getType().equals("auction")) {

                list.add(sp);
            //}
        }

        return list;

    }

    // retorna utilizador
    public User getUser(String username) {

        return this.users.get(username);
    }

    // cria servidor
    private void createServer(String id, int numServers, float price, float minBidPrice) {

        ServerProduct sp = new ServerProduct(id, numServers, price, minBidPrice);

        this.servers.put(id, sp);

    }

    private void createTestServers() throws IOException {

        // creating test servers
        this.createServer("f1.micro", 5, 20, 2);
        this.createServer("f2.micro", 5, 20, 2);
        this.createServer("f3.micro", 5, 20, 2);
        this.createServer("f4.micro", 5, 20, 2);
        this.createServer("f5.micro", 5, 20, 2);
        this.createServer("f1.normal", 5, 40, 2);
        this.createServer("f2.normal", 5, 40, 2);
        this.createServer("f3.normal", 5, 40, 2);
        this.createServer("f4.normal", 5, 40, 2);
        this.createServer("f5.normal", 5, 40, 2);
        this.createServer("f1.large", 5, 60, 2);
        this.createServer("f2.large", 5, 60, 2);
        this.createServer("f3.large", 5, 60, 2);
        this.createServer("f4.large", 5, 60, 2);
        this.createServer("f5.large", 5, 60, 2);
        this.createServer("a1.micro", 5, 30, 2);
        this.createServer("a2.micro", 5, 30, 2);
        this.createServer("a3.micro", 5, 30, 2);
        this.createServer("a4.micro", 5, 30, 2);
        this.createServer("a5.micro", 5, 30, 2);
        this.createServer("a1.normal", 5, 50, 2);
        this.createServer("a2.normal", 5, 50, 2);
        this.createServer("a3.normal", 5, 50, 2);
        this.createServer("a4.normal", 5, 50, 2);
        this.createServer("a5.normal", 5, 50, 2);
        this.createServer("a1.large", 5, 70, 2);
        this.createServer("a2.large", 5, 70, 2);
        this.createServer("a3.large", 5, 70, 2);
        this.createServer("a4.large", 5, 70, 2);
        this.createServer("a5.large", 5, 70, 2);

    }

    public static void main(String[] args) {

        Server s = new Server(12345);

        // start server
        s.startServer();

    }


}
