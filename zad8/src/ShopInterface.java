import java.util.Map;

public interface ShopInterface {
    public void delivery(Map<String, Integer> goods);
    public boolean purchase(String productName, int quantity);
    public Map<String, Integer> stock();
}