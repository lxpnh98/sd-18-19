package serverallocator;

public class Bill {
    String client;
    float value;

    public Bill(String client, float value) {
        this.client = client;
        this.value = value;
    }

    public float getValue() {
    	return this.value;
    }
}
