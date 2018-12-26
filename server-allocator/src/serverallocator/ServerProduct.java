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
    public Bill makeBid(String clientUsername, String money) throws IOException {
        float price = Float.parseFloat(money);

        if (price >= this.minBidPrice) {

            // add new bid or update client's bid
            this.bids.add(new Bid(clientUsername, price));

            // TODO: find empty reservation spot

            // find reservation with lowest bid
            Reservation lowestBid = null;
            int minIndex = -1;
            for (int i=0; i<this.numServers; i++) {
                Reservation curr = this.reservations.get(i);

                // exclude on demand reservations
                if (curr.getType() == ReservationType.ON_DEMAND) {
                    continue;
                }

                if (lowestBid == null || lowestBid.getPrice() > curr.getPrice()) {
                    lowestBid = curr;
                    minIndex = i;
                }
            }

            // if bid made is higher than lowest bid
            if (lowestBid != null && lowestBid.getPrice() < price) {

                // calculate amount to charge old client
                long newTimestamp = System.currentTimeMillis();
                long timeElapsed = newTimestamp - lowestBid.getTimestamp();
                float toCharge = (timeElapsed / millisecondsInHour) * lowestBid.getPrice();

                // replace reservation
                this.reservations.set(minIndex, new Reservation(clientUsername, "something", ReservationType.SPOT, price));

                // bill old client
                return new Bill(lowestBid.getClient(), toCharge);
            }
        }
        return null;
    }
}
