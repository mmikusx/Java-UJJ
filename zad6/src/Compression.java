import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

class Compression implements CompressionInterface{
    List<String> entry = new LinkedList<>();
    Map<String, Integer> wordCountingMap = new HashMap<>();
    Map<String, String> header = new HashMap<>();
    Map<String, String> getWordHeader = new HashMap<>();
    List<Map.Entry<String, Integer>> toSort = new ArrayList<>();
    Map<String, Integer> wordDecreasingCounter = new LinkedHashMap<>();
    protected String word = "";
    protected String loop = "";
    protected int entryLength = 0;
    protected int optimal = 0;
    protected int compressed = 0;
    protected int wordAppear = 0;
    protected int optimalCodedWords = 0;
    protected int optimalKeyLength = 0;
    protected int exit = 0;


    @Override
    public void addWord(String word){
        enterData(word);
        wordCountingMap.merge(word, 1, Integer::sum);
    }

    @Override
    public void compress(){
        optimal = entryLength;

        sortDescending(wordCountingMap, wordDecreasingCounter);

        loop = (String) wordDecreasingCounter.keySet().toArray()[0];
        for(int i=1; i < loop.length(); i++){
            int j=1;
            while(j <= Math.pow(2, i-1)){
                compressed = entryLength;

                {
                    int k=0;
                    while(k < wordDecreasingCounter.size()){
                        word = (String) wordDecreasingCounter.keySet().toArray()[k];
                        wordAppear = wordDecreasingCounter.get(wordDecreasingCounter.keySet().toArray()[k]);

                        if (k >= j){
                            compressed += wordAppear;
                        } else {
                            compressed += (i + word.length() + (wordAppear * i));
                            compressed -= (word.length() * wordAppear);
                        }
                        k++;
                    }
                }

                if(compressed >= optimal){
                    j++;
                    continue;
                }
                optimalKeyLength = i;
                optimal = compressed;
                optimalCodedWords = j;
                j++;
            }
        }
        checkCompression(optimal, entryLength, wordDecreasingCounter, optimalCodedWords, optimalKeyLength, header);
    }

    @Override
    public Map<String, String> getHeader(){
        return header;
    }

    @Override
    public String getWord(){
        return (getWordHeader.get(entry.get(exit)) != null) ? getWordHeader.get(entry.get(exit++)) : ("1" + entry.get(exit++));
    }

    protected void enterData(String word){
        entry.add(word);
        entryLength += word.length();
    }

    protected void sortDescending(Map<String, Integer> wordCountingMap,
                                  Map<String, Integer> wordDecreasingCounter){
        for(Iterator<Map.Entry<String, Integer>> iterator = wordCountingMap.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<String, Integer> stringIntegerEntry = iterator.next();
            toSort.add(stringIntegerEntry);
        }
        toSort.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        for(Map.Entry<String, Integer> stringIntegerEntry : toSort){
            wordDecreasingCounter.put(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
        }
    }

    protected void checkCompression(int optimal,
                                    int entryLength,
                                    Map<String, Integer> wordDecreasingCounter,
                                    int optimalCodedWordLength,
                                    int optimalKeyLength,
                                    Map<String, String> header){
        if(optimal >= entryLength){
            return;
        }
        IntStream.range(0, optimalCodedWordLength).forEachOrdered(i ->{
            header.put(String.format("%" + optimalKeyLength + "s", Integer.toBinaryString(i)).replaceAll(" ", "0"),
                    (String) wordDecreasingCounter.keySet().toArray()[i]);
        });

        makeMapToGetWordHeader(header);
    }

    protected void makeMapToGetWordHeader(Map<String, String> header) throws IllegalStateException{
        Map<String, String> map = new HashMap<>();
        for(Iterator<Map.Entry<String, String>> iterator = header.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<String, String> stringStringEntry = iterator.next();
            if(map.put(stringStringEntry.getValue(), stringStringEntry.getKey()) != null){
                throw new IllegalStateException("Duplicate key");
            }
        }
        getWordHeader = map;
    }
}