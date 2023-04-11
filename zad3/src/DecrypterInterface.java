import java.util.Map;

public interface DecrypterInterface {
    public void setInputText(String encryptedDocument);
    public Map<Character, Character> getCode();
    public Map<Character, Character> getDecode();
}