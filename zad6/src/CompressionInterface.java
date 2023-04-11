import java.util.Map;

public interface CompressionInterface {
    public void addWord(String word);
    public void compress();
    public Map<String, String> getHeader();
    public String getWord();
}