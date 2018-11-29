public class ServerProduct {

    private String id;
    private float price;
    private String type;

    private int status;

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

    public String getID() {

        return this.id;

    }



}
