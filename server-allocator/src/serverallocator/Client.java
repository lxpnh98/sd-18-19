package serverallocator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private Socket svSocket;
    private String hostname;
    private int port;
    private Menu menu;

    private BufferedReader serverIn;
    private BufferedWriter serverOut;

    public Client(String hostname, int port) {

        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void run() {

        String responseFromSv;

        try {
            while ((responseFromSv = serverIn.readLine()) != null) {

                this.parseResponse(responseFromSv);

            }
        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    // parse da resposta do servidor
    // data[0] tem o tipo da mensagem e as restantes datas têm outras informações
    private void parseResponse (String responseFromSv) {

        String[] data = responseFromSv.split(" ", 2);

        switch (data[0]) {

            case "LOGINFEITO":

                System.out.println("Login Feito");

                // muda o estado do menu
                this.menu.changeState(1);

                // mostra o menu com o novo estado
                this.menu.show();

                break;

            case "REGISTOFEITO":

                System.out.println("Registo Feito");

                // muda o estado do menu
                this.menu.changeState(0);

                // mostra o menu com o novo estado
                this.menu.show();

                break;

            case "OVERVIEW":

                System.out.println("Overview Feito");

                this.menu.changeBalance(Float.parseFloat(data[1]));

                // muda o estado do menu
                this.menu.changeState(2);

                // mostra o menu com o novo estado
                this.menu.show();

                break;

            case "MANDASVFIXOS":

                System.out.println("Mostrar svs fixos");

                ArrayList<String> array = new ArrayList<>();

                String[] parts = data[1].split(" ");
                for (String part : parts) {

                    array.add(part);
                }

                // array com os servidores fixos para mostrar no menu
                this.menu.changeArray(array);

                // muda o estado do menu
                this.menu.changeState(3);

                // mostra o menu com o novo estado
                this.menu.show();

                break;

            case "MANDASVLEILAO":

                System.out.println("Mostrar svs leiloes");

                ArrayList<String> array1 = new ArrayList<>();

                String[] parts1 = data[1].split(" ");
                for (String part : parts1) {

                    array1.add(part);
                }

                // array com os servidores fixos para mostrar no menu
                this.menu.changeArray(array1);

                // muda o estado do menu
                this.menu.changeState(4);

                // mostra o menu com o novo estado
                this.menu.show();

                break;
        }

    }

    public void startClient() {

        try {

            System.out.println("#### CLIENT ####");
            System.out.println("Client -> Connecting to server...");

            this.svSocket = new Socket(this.hostname, this.port);

            System.out.println("Client -> Connection done!");

            // read and write channels from server socket
            serverIn = new BufferedReader(new InputStreamReader(svSocket.getInputStream()));
            serverOut = new BufferedWriter(new OutputStreamWriter(svSocket.getOutputStream()));

            new Thread(this).start();

            // read channel from stdin
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            menu = new Menu();
            menu.show();

            int choice;

            while (((choice = menu.userChoice()) != -1)) {

                try {

                    this.controller(choice);

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }


        } catch (IOException ioex) {

            ioex.printStackTrace();
        }

    }

    // controla o menu de acordo com o input/escolha do utilizador
    private void controller (int choice) throws IOException {

        switch (menu.getState()) {
            case 0:
                if (choice == 1) {

                    this.login();
                }
                if (choice == 2) {

                    this.register();
                }
                if (choice == 0) {

                    System.exit(0);
                }

                break;

            case 1:
                if (choice == 1) {
                    this.sendMessage("OVERVIEW");
                }
                if (choice == 2) {
                    this.sendMessage("RESERVARSV");
                }
                if (choice == 0) {
                    this.sendMessage("RESERVARINSTANCIA");
                }

                break;
        }
    }

    // envia mensagem para o servidor
    private void sendMessage(String message) throws IOException {

        serverOut.write(message);
        serverOut.newLine();
        serverOut.flush();
    }

    // envia string para o servidor no formato: LOGIN username password
    private void login() throws IOException {

        String username = menu.readStringFromUser("Username: ");
        String password = menu.readStringFromUser("Password: ");

        String query = String.join(" ", "LOGIN", username, password);

        this.sendMessage(query);

    }

    // envia string para o servidor no formato: REGISTER username password
    private void register() throws IOException {

        String username = menu.readStringFromUser("Username: ");
        String password = menu.readStringFromUser("Password: ");

        String query = String.join(" ", "REGISTER", username, password);

        this.sendMessage(query);

    }

    public static void main(String[] args) {

        Client c = new Client("127.0.0.1", 12345);

        // start client
        c.startClient();

    }


}