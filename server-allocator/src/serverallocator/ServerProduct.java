package serverallocator;

public class ServerProduct {

    private String id;
    private float price;
    private String type;

    private int status; // 1 ocupado, 0 livre

    public ServerProduct(String id, float price, String type) {

        this.id = id;
        this.price = price;
        this.type = type;
        this.status = 0;

    }

    public void changeStatus(int newStatus) {

        this.status = newStatus;

    }

    public void changeType(String newType) {

        this.type = newType;

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

    public float getStatus() {

        return this.status;

    }

    public String getType() {

        return this.type;

    }



}
