import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private Socket client;

    // instance of server to do login method
    private Server server;

    private BufferedWriter clientOut;
    private BufferedReader clientIn;

    public ServerWorker(Socket client, Server server) throws IOException, NullPointerException {

        this.client = client;
        this.server = server;
        this.clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.clientOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

    }

    @Override
    public void run() {

        try {

            String responseFromClient;
            while ((responseFromClient = clientIn.readLine()) != null ) {

                this.parseResponse(responseFromClient);

            }


        } catch (IOException ioex) {

            ioex.printStackTrace();

        }

    }

    private void parseResponse (String responseFromClient) {

        String[] data = responseFromClient.split(" ", 2);

        switch (data[0]) {

            case "LOGIN":

                System.out.println("Recebeu login");

            case "SIGNUP":

        }


    }

    // fazer método para o login

    // fazer método para o registo


}
