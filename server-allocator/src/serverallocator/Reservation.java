package serverallocator;

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
        this.timestamp = System.currentTimeMillis();
    }

    public String getClient() {
        return this.client;
    }

    public String getId() {
        return this.id;
    }

    public ReservationType getType() {
        return this.type;
    }

    public float getPrice() {
        return this.price;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}