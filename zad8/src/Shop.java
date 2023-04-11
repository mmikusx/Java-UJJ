import java.util.HashMap;
import java.util.Map;

class Shop implements ShopInterface {

    Map<String, Object> synchro = new HashMap<>();
    Map<String, Integer> _stock = new HashMap<>();

    public void delivery(Map<String, Integer> goods) {
        makingDelivery(goods);
    }

    public boolean purchase(String productName, int quantity) {
        synchro.putIfAbsent(productName, this);

        return makingPurchase(productName, quantity);
    }

    public Map<String, Integer> stock() {
        return _stock;
    }

    protected void makingDelivery(Map<String, Integer> goods){
        for(String good : goods.keySet()){
            synchronized (this){
                if(_stock.get(good) == null){
                    _stock.put(good, goods.get(good));
                    synchro.computeIfAbsent(good, k -> new Object());
                } else {
                    _stock.put(good, _stock.get(good) + goods.get(good));
                }
            }

            synchronized (synchro.get(good)){
                synchro.get(good).notifyAll();
            }
        }
    }

    protected boolean makingPurchase(String productName, int quantity){
        synchronized (synchro.get(productName)){
            if(_stock.get(productName) != null && quantity <= _stock.get(productName)){
                _stock.put(productName, _stock.get(productName) - quantity);
                return true;
            } else {
                try {
                    synchro.get(productName).wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(_stock.get(productName) != null && _stock.get(productName) >= quantity){
                    _stock.put(productName, _stock.get(productName) - quantity);
                    return true;
                }
            }
        }
        return false;
    }
}