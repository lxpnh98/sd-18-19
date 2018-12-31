package serverallocator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    static int ID_LENGTH = 10;
    static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                          "abcdefghijklmnopqrstuvwxyz" +
                          "0123456789";
    private ServerSocket svSocket;
    private int port;
    private HashMap<String, User> users;
    private RWLock usersLock;
    private HashMap<String, ServerProduct> servers;
    private RWLock serversLock;

    public Server(int port) {
        this.port = port;
        this.users = new HashMap<>();
        this.usersLock = new RWLock();
        this.servers = new HashMap<>();
        this.serversLock = new RWLock();
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
    public boolean login(String username, String password) {
        User u = this.getUser(username);
        if (u == null) return false;
        if (u.getPassword().equals(password)) {
            u.setLoggedIn(true);
            return true;
        }
        return false;
    }

    // MÉTODO REGISTO
    public boolean register(String username, String password) {

        try {
            this.usersLock.writeLock();
            if (!this.users.containsKey(username)) {

                User newUser = new User(username, password);
                this.users.put(username, newUser);

                return true;
            }

            return false;
        } finally {
            this.usersLock.writeUnlock();
        }

    }

    // neste método, em caso de indisponibilidade de servidores do tipo pedido,
    // poderão ser canceladas reservas concedidas em leilão para obter o servidor pretendido.

    // Método para fazer uma bid para um servidor de leilão
    // retorna o id da reserva caso tenha sucesso, ou null caso contrário
    public String bidAuctionServer(String id, String username, String money) throws IOException {
        ServerProduct auctionServer;
        String idReservation = Server.newReservationId();
        try {
            this.serversLock.readLock();
            auctionServer = this.servers.get(id);
        } finally {
            this.serversLock.readUnlock();
        }

        if(auctionServer != null) {
            Bill conta = auctionServer.makeBid(username, Float.parseFloat(money), idReservation);
            auctionServer.printReservations();
            User user;
            if(conta!=null) {
                user = this.getUser(conta.getClient());
                float balance = user.getBalance() + conta.getValue();
                user.setBalance(balance);
            }
        } else {
            // servidor não encontrado
            System.out.println("Servidor não encontrado");
            return null;
        }
        return idReservation;
    }

    // Método para alugar servidores
    // retorna o id da reserva caso tenha sucesso, ou null caso contrário
    public String rentServer(String id, String username) {
        ServerProduct rentServer;
        String idReservation = Server.newReservationId();
        try {
            this.serversLock.readLock();
            rentServer = this.servers.get(id);
        } finally {
            this.serversLock.readUnlock();
        }

        if(rentServer != null) {
            Bill conta = rentServer.makeOnDemandReservation(username, idReservation);
            rentServer.printReservations();
            User user;
            
            if(conta!=null) {
                user = this.getUser(conta.getClient());
                float balance = user.getBalance() + conta.getValue();
                user.setBalance(balance);
            }
        } else {
            System.out.println("Servidor não encontrado");
            return null;
        }
        return idReservation;
    }


	// Método para libertar o servidor
    public int freeServer(String serverID, String username, String idReservation) {
        User user = this.getUser(username);
        ServerProduct freeServer;
        int r = 0;
        try {
            this.serversLock.readLock();
            freeServer = this.servers.get(serverID);
        } finally {
            this.serversLock.readUnlock();
        }

        if(freeServer != null && user != null) {
            Bill conta = freeServer.freeReservation(username,idReservation);
            freeServer.printReservations();
            User oldUser;
            if(conta != null) {
                oldUser = this.getUser(conta.getClient());
                float balance = oldUser.getBalance() + conta.getValue();
                user.setBalance(balance);
            } else {
                System.out.println("Servidor não foi libertado");
                r = 2;
            }
        } else {
            System.out.println("Servidor ou cliente não existem.");
            r = 3;
        }
        return r;
    }

    // retorna lista de servidores fixos
    public ArrayList<ServerProduct> getListOfFixedServers() {
        ArrayList<ServerProduct> list = new ArrayList<>();

        Collection<ServerProduct> values;
        try {
            this.serversLock.readLock();
            values = this.servers.values();
        } finally {
            this.serversLock.readUnlock();
        }

        for (ServerProduct sp : values) {
                list.add(sp);
        }
        return list;
    }

    // retorna lista de servidores a leilao
    public ArrayList<ServerProduct> getListOfAuctionServers() {
        ArrayList<ServerProduct> list = new ArrayList<>();

        Collection<ServerProduct> values;
        try {
            this.serversLock.readLock();
            values = this.servers.values();
        } finally {
            this.serversLock.readUnlock();
        }

        for (ServerProduct sp : values) {
                list.add(sp);
        }
        return list;
    }

    // retorna utilizador
    public User getUser(String username) {
        try {
            this.usersLock.readLock();
            return this.users.get(username);
        } finally {
            this.usersLock.readUnlock();
        }
    }

    // cria servidor
    private void createServer(String id, int numServers, float price, float minBidPrice) {
        ServerProduct sp = new ServerProduct(id, numServers, price, minBidPrice);
        try {
            this.serversLock.writeLock();
            this.servers.put(id, sp);
        } finally {
            this.serversLock.writeUnlock();
        }
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

    public float getUserBalance(String username) {
        User u = this.getUser(username);
        if (u == null) return Float.NaN;
        float total = 0.0f;
        total += u.getBalance();
        try {
            this.serversLock.readLock();
            total += this.servers.values().stream().mapToDouble(s -> s.getTotalUserBalance(username)).sum();
        } finally {
            this.serversLock.readUnlock();
        }
        return total;
    }

    private static String newReservationId() {
        StringBuilder r = new StringBuilder();
        (new Random()).ints(ID_LENGTH, 0, chars.length())
            .forEach(i -> r.append(chars.charAt(i)));
        return r.toString();
    }

    public static void main(String[] args) {
        Server s = new Server(12345);

        // start server
        s.startServer();
    }
}
