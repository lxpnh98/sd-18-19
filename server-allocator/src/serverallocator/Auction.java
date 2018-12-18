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

    // bid identified by name of client
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;
        return this.client.equals(((Bid)o).client);
    }
}

// sort by bid value
class BidComparator implements Comparator<Bid> {
    public int compare(Bid o1, Bid o2) {
        return Float.compare(o1.value, o2.value);
    }
}

public class Auction {

    private final float millisecondsInHour = 1000.0f * 3600.0f;
    private ServerProduct serverToRent;
    private float minPrice;
    private float currPrice;
    private TreeSet<Bid> bids;
    private String clientUsername;
    private long timestamp;

    public Auction (ServerProduct serverToRent, float minPrice) throws IOException {

        this.serverToRent = serverToRent;
        this.minPrice = minPrice;
        this.clientUsername = null;
        this.bids = new TreeSet<>(new BidComparator());

    }

    // o que acontece quando o leilão recebe uma oferta
    // returns bill to charge old client, or null
    private Bill makeBid(String clientUsername, String money) throws IOException {

        float price = Float.parseFloat(money);
        String oldClient = this.clientUsername;
        float toCharge = 0.0f;

        if (price >= this.minPrice) {

            // add new bid or update client's bid
            this.bids.add(new Bid(clientUsername, price));

            Bid bestBid = this.bids.last();
            String newClient = bestBid.client;
            if (this.clientUsername != newClient) {

                // 
                long newTimestamp = System.currentTimeMillis();

                // calculate amount to charge old client
                if (this.clientUsername != null) {
                    long timeElapsed = newTimestamp - this.timestamp;
                    toCharge = (timeElapsed / millisecondsInHour) * this.currPrice;
                }

                // change current client
                this.clientUsername = newClient;
                this.currPrice = bestBid.value;
                this.timestamp = newTimestamp;
                
            }

        }

        if (toCharge > 0.0) {
            return new Bill(oldClient, toCharge);
        }
        return null;

    }

}
