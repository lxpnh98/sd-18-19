package serverallocator;

import java.io.*;
import java.util.*;
import java.net.Socket;

class Bid {
    String client;
    float value;

    Bid(String c, float v) {
        this.client = c;
        this.value = v;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;
        return this.client.equals(((Bid)o).client);
    }
}

class BidComparator implements Comparator<Bid> {
    public int compare(Bid o1, Bid o2) {
        return Float.compare(o1.value, o2.value);
    }
}

public class Auction {

    private ServerProduct serverToRent;
    private float minPrice;
    private TreeSet<Bid> bids;
    private String clientUsername;

    public Auction (ServerProduct serverToRent, float minPrice) throws IOException {

        this.serverToRent = serverToRent;
        this.minPrice = minPrice;
        this.clientUsername = null;
        this.bids = new TreeSet(new BidComparator());

    }

    // o que acontece quando o leilão recebe uma oferta
    private void makeBid(String clientUsername, String money) throws IOException {

        float price = Float.parseFloat(money);

        if (price >= this.minPrice) {

            // add new bid or update client's bid
            this.bids.add(new Bid(clientUsername, price));

            String newClient = this.bids.last().client;
            if (this.clientUsername != newClient) {
                 // TODO: timestamps
                // calculate amount to charge old client

                // change current client
                this.clientUsername = newClient;
                
            }

        }

    }

}
