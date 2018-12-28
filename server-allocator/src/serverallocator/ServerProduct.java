package serverallocator;

import java.util.*;
import java.io.*;

class Bid {
    String client;
    float value;
    String idReservation;

    Bid(String c, float v, String idReservation) {
        this.client = c;
        this.value = v;
        this.idReservation = idReservation;
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

    // free reservation (both on demand and spot reservations)
    public Bill freeReservation(String username,String idReservation) {
        // find reservation
        int i = 0;
        Reservation free = null;

        for(Reservation r : this.reservations) {
            if(r.getId() == idReservation) {
                free = this.reservations.get(i);
                break;
            } else {
                i++;
            }
        }

        // charge user
        long newTimestamp = System.currentTimeMillis();
        long timeElapsed = newTimestamp - free.getTimestamp();
        float toCharge = (timeElapsed / millisecondsInHour) * free.getPrice();
        Bill bill = new Bill(username, toCharge);

        if(free.getType() == ReservationType.ON_DEMAND) {
            //free server
            free = null;
            for(Bid b : bids) {
                if(b.getIdReservation() != null) {
                    String clientUsername = b.getClient();
                    float preco = b.getValue();
                    this.reservations.set(i, new Reservation(clientUsername, "something", ReservationType.SPOT, price));
                    break;
                }
            }
        } else {
            // Type == spot
            // free server
            free = null;

            // delete user bid 
            // Esta parte pode estar dentro do outro ciclo;
            for(Bid b : bids) {
                if(b.getClient().equals(username)) {
                    bids.remove(b);
                    break;
                }
            }

            // reserve server for highest bid
            for(Bid b : bids) {
                if(b.getIdReservation() != null) {
                    String clientUsername = b.getClient();
                    float preco = b.getValue();
                    this.reservations.set(i, new Reservation(clientUsername, "something", ReservationType.SPOT, price));
                    break;
                }
            }
        }
        return bill;
    }

    // on demand reservation
    public Bill makeOnDemandReservation(String clientUsername) {
        // find reservation with lowest bid or the first empty reservation spot
        int emptyIndex = -1;
        Reservation lowestBid = null;
        int minIndex = -1;
        for (int i=0; i<this.numServers; i++) {
            Reservation curr = this.reservations.get(i);

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
            if (lowestBid == null || lowestBid.getPrice() > curr.getPrice()) {
                lowestBid = curr;
                minIndex = i;
            }
        }

        // if there is empty reservation
        if (emptyIndex != -1) {
            // create reservation
            this.reservations.set(emptyIndex, new Reservation(clientUsername, "something", ReservationType.ON_DEMAND, this.price));

            // no bill to charge
            return null;

        // if there are no empty spots, replace lowest bid
        } else if (lowestBid != null) {

            // calculate amount to charge old client
            long newTimestamp = System.currentTimeMillis();
            long timeElapsed = newTimestamp - lowestBid.getTimestamp();
            float toCharge = (timeElapsed / millisecondsInHour) * lowestBid.getPrice();

            // replace reservation
            this.reservations.set(minIndex, new Reservation(clientUsername, "something", ReservationType.SPOT, price));

            // bill old client
            return new Bill(lowestBid.getClient(), toCharge);

        // if all reservations are on demand, reject reservation
        } else {
            // TODO: dar sinal que rejeitou reserva
            return null;
        }
    }

    // o que acontece quando o leilão recebe uma oferta
    // returns bill to charge old client, or null
    public Bill makeBid(String clientUsername, float price) throws IOException {

        // if bid < minimum price then do nothing
        if (price < this.minBidPrice) return null;

        // add new bid or update client's bid
        this.bids.add(new Bid(clientUsername, price, null));

        // find reservation with lowest bid or the first empty reservation spot
        int emptyIndex = -1;
        Reservation lowestBid = null;
        int minIndex = -1;
        for (int i=0; i<this.numServers; i++) {
            Reservation curr = this.reservations.get(i);

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
            if (lowestBid == null || lowestBid.getPrice() > curr.getPrice()) {
                lowestBid = curr;
                minIndex = i;
            }
        }

        // if there is empty reservation
        if (emptyIndex != -1) {
            // create reservation
            this.reservations.set(emptyIndex, new Reservation(clientUsername, "something", ReservationType.SPOT, price));

            // no bill to charge
            return null;
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

        // if bid is not high enough, no reservation is replaced, and no bill charged
        return null;
    }
}
