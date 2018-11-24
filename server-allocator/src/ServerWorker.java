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

    private void parseResponse (String responseFromClient) throws IOException {

        String[] data = responseFromClient.split(" ", 2);

        switch (data[0]) {

            case "LOGIN":

                System.out.println("Recebeu login");

                // Dados do login estão em  data[1];
                this.login(data[1]);

                break;

            case "REGISTER":

                System.out.println("Recebeu registo");

                // Dados do registo estão em  data[1];
                this.register(data[1]);

                break;

        }


    }

    private void sendMessage(String message) throws IOException {

        clientOut.write(message);
        clientOut.newLine();
        clientOut.flush();
    }

    private void login(String dataLogin) throws IOException {

        String[] data = dataLogin.split(" ");

        String username = data[0];
        String password = data[1];

        if (data.length != 2) {

            System.out.println("Dados inseridos incorretamente");
            return;
        }

        this.server.login(username, password);

        this.sendMessage("LOGINFEITO");

    }

    private void register(String dataRegister) throws IOException {

        String[] data = dataRegister.split(" ");

        String username = data[0];
        String password = data[1];

        if (data.length != 2) {

            System.out.println("Dados inseridos incorretamente");
            return;
        }

        this.server.register(username, password);

        this.sendMessage("REGISTOFEITO");

    }


}
