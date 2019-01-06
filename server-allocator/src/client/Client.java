package client;

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

                System.out.println("\nLogin Feito\n");
                
                // muda o estado do menu
                this.menu.changeState(1);

                // mostra o menu com o novo estado
                this.menu.show();

                break;

            case "LOGINFALHADO" :

                System.out.println("\nLogin Falhado\n");
                
                // muda o estado do menu
                this.menu.changeState(0);

                // mostra o menu com o novo estado
                this.menu.show();
                break;

            case "REGISTOFEITO":

                System.out.println("\nRegisto Feito\n");

                // muda o estado do menu
                this.menu.changeState(0);

                // mostra o menu com o novo estado
                this.menu.show();
                break;

            case "VERBALANCE":

                System.out.println("\nVer balance Feito\n");

                this.menu.changeBalance(Float.parseFloat(data[1]));

                // muda o estado do menu
                this.menu.changeState(2);

                // mostra o menu com o novo estado
                this.menu.show();
                break;

            case "MANDASVFIXOS":

                System.out.println("\nMostrar svs fixos\n");

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

                System.out.println("\nMostrar svs leiloes\n");

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

            case "LIBERTARSV":

                System.out.println("\nServer libertado\n");
                this.menu.changeState(1);
                this.menu.show();

                break;

            case "RESERVAFIXO":

                System.out.println("\nServer Reservado\n");
                System.out.println("Id da reserva: "+data[1]);
                this.menu.changeState(1);
                this.menu.show();

                break;

            case "LICITACAO":
            
                System.out.println("\nLicitação feita\n");
                System.out.println("Id da reserva: "+data[1]);
                this.menu.changeState(1);
                this.menu.show();

                break;

            case "SERVERNAOENCONTRADO":

                System.out.println("\nServer não encontrado\n");
                this.menu.changeState(1);
                this.menu.show();

                break;

            case "SERVERNAOLIBERTADO":

                   System.out.println("\nServer não libertado\n");
                this.menu.changeState(1);
                this.menu.show();

                break;

            case "NAOEXISTE":

                   System.out.println("\nServidor ou cliente não existem\n");
                this.menu.changeState(1);
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
                    this.sendMessage("VERBALANCE");
                }
                if (choice == 2) {
                    this.sendMessage("RESERVARSV");
                }
                if (choice == 3) {
                    this.sendMessage("RESERVARINSTANCIA");
                }
                if (choice == 4) {
                    String id = menu.readStringFromUser("Server a libertar: ");
                    String idR = menu.readStringFromUser("Id de reserva: ");
                    String query = String.join(" ", "LIBERTARSV", id, idR);
                    this.sendMessage(query);
                }
                if (choice == 0) {
                    menu.changeState(0);
                    this.menu.show();
                }

                break;
            case 2:
                if (choice == 0) {
                    menu.changeState(1);
                    this.menu.show();
                }

                break;
            case 3:
                if (choice == 1) {
                    String id = menu.readStringFromUser("Server a reservar: ");
                    String query = String.join(" ", "RESERVAFIXO", id);
                    this.sendMessage(query);
                }
                if (choice == 0) {
                    menu.changeState(1);
                    this.menu.show();
                }

                break;
            case 4:
                if (choice == 1) {
                    String id = menu.readStringFromUser("Server a reservar: ");
                    String money = menu.readStringFromUser("Valor da licitação: ");
                    String query = String.join(" ", "LICITACAO", id, money);
                    this.sendMessage(query);
                }
                if (choice == 0) {
                    menu.changeState(1);
                    this.menu.show();
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
