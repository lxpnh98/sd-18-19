package serverallocator;

public class Bill {
    String client;
    float value;

    public Bill(String client, float value) {
        this.client = client;
        this.value = value;
    }

    public String getClient() {
        return this.client;
    }

    public float getValue() {
    	return this.value;
    }
}
