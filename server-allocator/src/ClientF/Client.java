package ClientF;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket svSocket;
    private String hostname;
    private int port;
    private Menu menu;

    private BufferedReader inSv;
    private BufferedWriter outSv;

    public Client(String hostname, int port) {

        this.hostname = hostname;
        this.port = port;
    }

    public void startClient() {

        try {

            System.out.println("#### CLIENT ####");
            System.out.println("ClientF.Client -> Connecting to server...");

            this.svSocket = new Socket(this.hostname, this.port);

            System.out.println("ClientF.Client -> Connection done!");

            // read and write channels from server socket
            inSv = new BufferedReader(new InputStreamReader(svSocket.getInputStream()));
            outSv = new BufferedWriter(new OutputStreamWriter(svSocket.getOutputStream()));

            // read channel from stdin
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            menu = new Menu();
            menu.show();

            int choice;

            while (((choice = menu.userChoice()) != -1)) {

                try {

                    controller(choice);

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }


        } catch (IOException ioex) {

            ioex.printStackTrace();
        }

    }

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
                if (choice==1) {
                    this.sendMessage("OVERVIEW");
                }
                if (choice==2) {
                    this.sendMessage("RESERVARSV");
                }
                if (choice==0) {
                    this.sendMessage("RESERVARINSTANCIA");
                }
                break;
        }
    }

    private void sendMessage(String message) throws IOException {

        outSv.write(message);
        outSv.newLine();
        outSv.flush();
    }

    private void login() throws IOException {

        String username = menu.readStringFromUser("Username: ");
        String password = menu.readStringFromUser("Password: ");

        String query = String.join(" ", "LOGIN", username, password);

        outSv.write(query);
        outSv.newLine();
        outSv.flush();

    }

    private void register() throws IOException {

        String username = menu.readStringFromUser("Username: ");
        String password = menu.readStringFromUser("Password: ");

        String query = String.join(" ", "REGIST", username, password);

        outSv.write(query);
        outSv.newLine();
        outSv.flush();
    }

    public static void main(String[] args) {

        Client c = new Client("127.0.0.1", 12345);

        // start client
        c.startClient();

    }


}