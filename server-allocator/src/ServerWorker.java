import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private Socket client;

    // instance of server to do login method
    private Server server;

    private BufferedWriter clientOut;
    private BufferedReader clientIn;

    public ServerWorker(Socket client, Server server) throws IOException {

        this.client = client;
        this.server = server;
        this.clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.clientOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

    }

    public void run() {

        try {

            String responseFromClient;
            while ((responseFromClient = clientIn.readLine()) != null ) {


            }


        } catch (IOException ioex) {

            ioex.printStackTrace();

        }

    }

    // fazer método para o login

    // fazer método para o registo

    // fazer método para parse daquilo q vem do client

    // fazer método para o write no client

}
