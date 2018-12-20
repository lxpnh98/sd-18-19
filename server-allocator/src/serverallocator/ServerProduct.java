package serverallocator;

import java.util.*;
import java.io.*;

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

public class ServerProduct {

    private final float millisecondsInHour = 1000.0f * 3600.0f;
    private String id;
    private int numServers;
    private List<Reservation> reservations;
    private TreeSet<Bid> bids;
    private float minBidPrice;
    private float price;

    public ServerProduct(String id, int numServers, float price, float minBidPrice) {
        this.id = id;
        this.numServers = numServers;
        this.reservations = new ArrayList<>(numServers);        
        this.bids = new TreeSet<>(new BidComparator());
        this.price = price;
        this.minBidPrice = minBidPrice;
    }

    public void changePrice(float newPrice) {
        this.price = newPrice;
    }

    public String getID() {
        return this.id;
    }

    public float getPrice() {
        return this.price;
    }

    // o que acontece quando o leilão recebe uma oferta
    // returns bill to charge old client, or null
    // TODO: alterar makeBid() para alocar vários servidores
    private Bill makeBid(String clientUsername, String money) throws IOException {
        return null;
        /*
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
    */
    }
}
