package serverallocator;

enum ReservationType {
    ON_DEMAND, SPOT
}

public class Reservation {
    private String client;
    private String id;
    private ReservationType type;
    private float price;
    private long timestamp;

    public Reservation(String client, String id, ReservationType type, float price) {
        this.client = client;
        this.id = id;
        this.type = type;
        this.price = price;
        this.client = client;
        this.timestamp = System.currentTimeMillis();
    }
}
