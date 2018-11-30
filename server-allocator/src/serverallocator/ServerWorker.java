package serverallocator;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerWorker implements Runnable {

    private Socket client;

    // instance of server to do login method
    private Server server;

    private BufferedWriter clientOut;
    private BufferedReader clientIn;

    private User loggedinUser;

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

            case "OVERVIEW":

                System.out.println("Recebeu o ver balance");

                this.getOverview();

                break;

            case "RESERVARSV":

                System.out.println("Recebeu reservar servidor fixo");

                this.getFixedServers();

                break;

            case "RESERVARINSTANCIA":

                System.out.println("Recebeu bidar leilão");

                this.getAuctionServers();

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

        this.loggedinUser = this.server.getUser(username);

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

    private void getOverview() throws IOException {

        String balance = Float.toString(this.loggedinUser.getBalance());

        String query = String.join(" ", "OVERVIEW", balance);

        this.sendMessage(query);

    }

    private void getFixedServers() throws IOException {

        ArrayList<ServerProduct> array = this.server.getListOfFixedServers();

        String res = "MANDASVFIXOS";

        List<String> idsList = array.stream()
                .map(ServerProduct::getID)
                .collect(Collectors.toList());

        List<Float> pricesList = array.stream()
                .map(ServerProduct::getPrice)
                .collect(Collectors.toList());

        ArrayList<String> finalList = new ArrayList<>(array.size());

        for (int i = 0; i < array.size(); i++) {

            finalList.add(idsList.get(i) + " " + pricesList.get(i));

        }

        String res1 = String.join(" ", finalList);

        this.sendMessage(res + " " + res1);

    }

    private void getAuctionServers() throws IOException {

        ArrayList<ServerProduct> array = this.server.getListOfFixedServers();

        String res = "MANDASVLEILAO";

        List<String> idsList = array.stream()
                .map(ServerProduct::getID)
                .collect(Collectors.toList());

        List<Float> pricesList = array.stream()
                .map(ServerProduct::getPrice)
                .collect(Collectors.toList());

        ArrayList<String> finalList = new ArrayList<>(array.size());

        for (int i = 0; i < array.size(); i++) {

            finalList.add(idsList.get(i) + " " + pricesList.get(i));

        }

        String res1 = String.join(" ", finalList);

        this.sendMessage(res + " " + res1);

    }

    private void shiftArray(ArrayList<ServerProduct> array) {

        // make a loop to run through the array list
        for(int i = array.size() - 1; i > 0; i--) {

            // set the last element to the value of the 2nd to last element
            array.set(i, array.get(i - 1));
        }

    }


}
