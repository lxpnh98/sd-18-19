import java.io.*;
import java.net.Socket;

public class Auction implements Runnable {

    private ServerProduct serverToRent;
    private float minPrice;
    private String clientUsername;

    private Socket svSocket;

    private BufferedReader serverIn;
    private BufferedWriter serverOut;

    public Auction (ServerProduct serverToRent, float minPrice) throws IOException {

        this.svSocket = new Socket("127.0.0.1", 12345);
        this.serverToRent = serverToRent;
        this.minPrice = minPrice;
        this.clientUsername = null;

        serverIn = new BufferedReader(new InputStreamReader(svSocket.getInputStream()));
        serverOut = new BufferedWriter(new OutputStreamWriter(svSocket.getOutputStream()));

    }

    @Override
    public void run() {

        System.out.println("LEILÃO ATIVO e à espera de bids");

        String responseFromSv;

        try {

            while ((responseFromSv = serverIn.readLine()) != null) {

                this.parseResponse(responseFromSv);

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void sendMessage(String message) throws IOException {

        serverOut.write(message);
        serverOut.newLine();
        serverOut.flush();
    }

    private void parseResponse (String responseFromSv) {

        String[] data = responseFromSv.split(" ", 2);

        switch (data[0]) {

            case "BIDOFFER":

                System.out.println("Oferta feita");

                this.bidReceived();

                break;

        }

    }

    private void bidReceived(String clientUsername, float money) throws IOException {

        if (money >= this.minPrice) {

            this.serverToRent.changePrice(money);

        }

        this.sendMessage("Oferta aceite");

    }


}
