import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private Socket client;

    // instance of server to do login method
    private Server server;

    public ServerWorker(Socket client, Server server) {

        this.client = client;
        this.server = server;

    }

    public void run() {

        try {

            // read and write channels from client socket
            BufferedReader clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter clientOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            // MÉTODO PRA TRATAR ESCOLHA DE MENU DO CLIENTE
            // this.menuSelection(clientIn, clientOut);

        } catch (IOException ioex) {

            ioex.printStackTrace();

        }

    }

    // fazer método para o login

    // fazer método para o registo

}
