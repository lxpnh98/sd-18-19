package serverallocator;

import java.util.*;
import java.io.*;

class Bid {
    String client;
    float value;
    String idReservation;
    boolean hasReservation;

    Bid(String c, float v, String idReservation, boolean hasReservation) {
        this.client = c;
        this.value = v;
        this.idReservation = idReservation;
        this.hasReservation = hasReservation;
    }

    public String getClient() {
        return this.client;
    }

    public float getValue() {
        return this.value;
    }

    public String getIdReservation() {
        return this.idReservation;
    }

    public boolean hasReservation() {
        return this.hasReservation;
    }

    // bid identified by name of client
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;
        return this.client.equals(((Bid)o).client) && this.idReservation.equals(((Bid)o).idReservation);
    }
}

// sort by bid value
class BidComparator implements Comparator<Bid> {
    public int compare(Bid o1, Bid o2) {
        return Float.compare(o1.value, o2.value);
    }
}

public class ServerProduct {

    public static final float millisecondsInHour = 1000.0f * 3600.0f;
    private String id;
    private int numServers;
    private Reservation[] reservations;
    private TreeSet<Bid> bids;
    private float minBidPrice;
    private float price;

    public ServerProduct(String id, int numServers, float price, float minBidPrice) {
        this.id = id;
        this.numServers = numServers;
        this.reservations = new Reservation[numServers];        
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

    // free reservation (both on demand and spot reservations)
    public synchronized Bill freeReservation(String username, String idReservation) {

        // find reservation
        int i;
        Reservation free = null;

        for(i=0; i<this.numServers; i++) {
            if (this.reservations[i] != null && this.reservations[i].getClient().equals(username)
                                             && this.reservations[i].getId().equals(idReservation)) {
                free = this.reservations[i];
                break;
            }
        }

        if (free == null) {
            // Não existe reserva
            return null;
        }

        // if on spot reservation delete user bid 
        if(free.getType() == ReservationType.SPOT) {
            for(Bid b : bids) {
                if(b.getClient().equals(username) && b.idReservation.equals(idReservation)) {
                    bids.remove(b);
                    break;
                }
            }
        }

        // free server
        this.reservations[i] = null;

        // reserve server for highest bid
        Iterator<Bid> it = bids.descendingIterator();
        while (it.hasNext()) {
            Bid b = it.next();
            if(b.hasReservation() == false) {
                String clientUsername = b.getClient();
                float bidValue = b.getValue();
                String id = b.getIdReservation();
                b.hasReservation = true;
                this.reservations[i] = new Reservation(clientUsername, id, ReservationType.SPOT, bidValue);
                break;
            }
        }

        // charge user
        long newTimestamp = System.currentTimeMillis();
        long timeElapsed = newTimestamp - free.getTimestamp();
        float toCharge = (timeElapsed / millisecondsInHour) * free.getPrice();
        return new Bill(username, toCharge);
    }

    // on demand reservation
    public synchronized Bill makeOnDemandReservation(String clientUsername, String reservationId) {

        // find reservation with lowest bid or the first empty reservation spot
        int emptyIndex = -1;
        Reservation lowestBidReservation = null;
        int minIndex = -1;
        for (int i=0; i<this.numServers; i++) {
            Reservation curr = this.reservations[i];

            // check if empty
            if (curr == null) {
                emptyIndex = i;
                break;
            }

            // exclude on demand reservations
            if (curr.getType() == ReservationType.ON_DEMAND) {
                continue;
            }

            // update minimum bid
            if (lowestBidReservation == null || lowestBidReservation.getPrice() > curr.getPrice()) {
                lowestBidReservation = curr;
                minIndex = i;
            }
        }

        // if there is empty reservation
        if (emptyIndex != -1) {
            // create reservation
            this.reservations[emptyIndex] = new Reservation(clientUsername, reservationId, ReservationType.ON_DEMAND, this.price);

            // no bill to charge
            return null;

        // if there are no empty spots, replace lowest bid
        } else if (lowestBidReservation != null) {

            // calculate amount to charge old client
            long newTimestamp = System.currentTimeMillis();
            long timeElapsed = newTimestamp - lowestBidReservation.getTimestamp();
            float toCharge = (timeElapsed / millisecondsInHour) * lowestBidReservation.getPrice();

            // replace reservation
            Bid toCompare = new Bid(lowestBidReservation.getClient(), 0.0f, lowestBidReservation.getId(), false);
            for (Bid b : this.bids) {
                if (b.equals(toCompare)) {
                    b.hasReservation = false;
                }
            }
            this.reservations[minIndex] = new Reservation(clientUsername, reservationId, ReservationType.ON_DEMAND, price);

            // bill old client
            return new Bill(lowestBidReservation.getClient(), toCharge);

        // if all reservations are on demand, reject reservation
        } else {
            return null;
        }
    }

    // o que acontece quando o leilão recebe uma oferta
    // returns bill to charge old client, or null
    public synchronized Bill makeBid(String clientUsername, float price, String reservationId) throws IOException {

        // if bid < minimum price then do nothing
        if (price < this.minBidPrice) return null;

        // add new bid or update client's bid
        Bid newBid = new Bid(clientUsername, price, reservationId, false);
        this.bids.add(newBid);

        // find reservation with lowest bid or the first empty reservation spot
        int emptyIndex = -1;
        Reservation lowestBidReservation = null;
        int minIndex = -1;
        for (int i=0; i<this.numServers; i++) {
            Reservation curr = this.reservations[i];

            // check if empty
            if (curr == null) {
                emptyIndex = i;
                break;
            }

            // exclude on demand reservations
            if (curr.getType() == ReservationType.ON_DEMAND) {
                continue;
            }

            // update minimum bid
            if (lowestBidReservation == null || lowestBidReservation.getPrice() > curr.getPrice()) {
                lowestBidReservation = curr;
                minIndex = i;
            }
        }

        // if there is empty reservation
        if (emptyIndex != -1) {
            // create reservation
            this.reservations[emptyIndex] = new Reservation(clientUsername, reservationId, ReservationType.SPOT, price);

            // no bill to charge
            return null;
        }

        // if bid made is higher than lowest bid
        if (lowestBidReservation != null && lowestBidReservation.getPrice() < price) {

            // calculate amount to charge old client
            long newTimestamp = System.currentTimeMillis();
            long timeElapsed = newTimestamp - lowestBidReservation.getTimestamp();
            float toCharge = (timeElapsed / millisecondsInHour) * lowestBidReservation.getPrice();

            // replace reservation
            this.reservations[minIndex] = new Reservation(clientUsername, reservationId, ReservationType.SPOT, price);

            // bill old client
            return new Bill(lowestBidReservation.getClient(), toCharge);
            
        }

        // if bid is not high enough, no reservation is replaced, and no bill charged
        return null;
    }

    public void printReservations() {
        for (int i=0; i<this.numServers; i++)
            System.out.println(""+i+": "+this.reservations[i]);
    }

    public double getTotalUserBalance(String username) {
        double total = 0.0f;
        for (int i=0; i<this.numServers; i++) {
            Reservation r = this.reservations[i];
            if (r != null && r.getClient().equals(username)) {
                total += new Double(r.getToCharge());
            }
        }
        return total;
    }
}
