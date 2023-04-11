import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Kasjer implements KasjerInterface{
    List<Pieniadz> cashResult = new LinkedList<>();
    List<Pieniadz> result = new ArrayList<>();
    RozmieniaczInterface rozmieniacz;

    @Override
    public List<Pieniadz> rozlicz(int cena, List<Pieniadz> pieniadze) {
        List<Pieniadz> receivedCash = new ArrayList<>(pieniadze);

        int notExchangable = pieniadze.stream().filter(cash ->
                        !cash.czyMozeBycRozmieniony())
                .mapToInt(Pieniadz::wartosc).sum();
        int exchangable = pieniadze.stream().filter(Pieniadz::czyMozeBycRozmieniony)
                .mapToInt(Pieniadz::wartosc).sum();


        if(cena == (exchangable + notExchangable)){
            cashResult.addAll(receivedCash);
            return result;
        }

        if(notExchangable > cena){

            loops(receivedCash, result);

            receivedCash.addAll(cashResult);

            whileLoops(result,
                    (exchangable + notExchangable) - cena,
                    cashResult);
            return result;
        } else {
            whileLoops(result,
                    (exchangable + notExchangable) - cena,
                    receivedCash);
            cashResult.addAll(receivedCash);
        }
        return result;
    }

    @Override
    public List<Pieniadz> stanKasy() {
        return cashResult;
    }

    @Override
    public void dostępDoRozmieniacza(RozmieniaczInterface rozmieniacz) {
        this.rozmieniacz = rozmieniacz;
    }

    @Override
    public void dostępDoPoczątkowegoStanuKasy(Supplier<Pieniadz> dostawca) {
        Stream.iterate(dostawca.get(), Objects::nonNull, cash -> dostawca.get()).forEachOrdered(cash -> cashResult.add(cash));
    }

    protected void loops(List<Pieniadz> receivedCash, List<Pieniadz> result){
        var ref = new Object(){
            int minimumValue = 0;
        };

        int i = 0;
        int receivedCashSize = receivedCash.size();
        while(i < receivedCashSize){
            Pieniadz cash = receivedCash.get(i);
            if(cash.czyMozeBycRozmieniony() == false){
                ref.minimumValue = cash.wartosc();
            }
            i++;
        }

        loop2(receivedCash, ref.minimumValue);

        forEachLoop(receivedCash, ref.minimumValue, result);
    }

    protected void loop2(List<Pieniadz> receivedCash, int minimumValue){
        insideLoop2(receivedCash, minimumValue);
    }

    protected void insideLoop2(List<Pieniadz> receivedCash, int minimumValue){
        int i = 0;
        int receivedCashSize = receivedCash.size();
        while (i < receivedCashSize) {
            Pieniadz cash = receivedCash.get(i);
            if(cash.czyMozeBycRozmieniony() == false && cash.wartosc() < minimumValue){
                minimumValue = cash.wartosc();
            }
            i++;
        }
    }

    protected void forEachLoop(List<Pieniadz> receivedCash, Object ref, List<Pieniadz> result){
        int objToInt = (Integer) ref;
        receivedCash.stream().filter(cash -> !cash.czyMozeBycRozmieniony()).forEachOrdered(cash -> {
            if(cash.wartosc() == objToInt){
                result.add(cash);
            } else {
                cashResult.add(cash);
            }
        });
    }

    protected void whileLoops(List<Pieniadz> result, int rest, List<Pieniadz> cashResult) {
        do {
            List<Pieniadz> exchanged = new ArrayList<>();
            boolean loopBreaker = true;

            Iterator<Pieniadz> it = cashResult.iterator();
            while(true){
                if(it.hasNext()){
                    Pieniadz cash = it.next();
                    if(cash.czyMozeBycRozmieniony() == false || cash.wartosc() == 1){
                        continue;
                    }
                    exchanged = rozmieniacz.rozmien(cash);
                    it.remove();
                    loopBreaker = false;
                    break;
                } else {
                    break;
                }
            }

            cashResult.addAll(exchanged);
            if(loopBreaker == true) {
                break;
            }
        }while(true);

        int i=0;
        while (true){
            if(i < rest){
                Iterator<Pieniadz> it = cashResult.iterator();
                while(true){
                    if(!it.hasNext()){
                        break;
                    }
                    Pieniadz cash = it.next();
                    if(cash.czyMozeBycRozmieniony() != false){
                        result.add(cash);
                        it.remove();
                        break;
                    }
                }
                i++;
            } else {
                break;
            }
        }
    }
}