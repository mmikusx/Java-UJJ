import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Decrypter implements DecrypterInterface{
    private Map<Character, Character> codeMap = new HashMap<>();
    private Map<Character, Character> decodeMap = new HashMap<>();

    public void setInputText(String encryptedDocument) {
        reset();

        if (encryptedDocument == null) {
            return;
        }
        String wydzial = "Wydział Fizyki, Astronomii i Informatyki Stosowanej";
        String a = wydzial.replaceAll("\\s+", "");
        String b = a.replace(",", "");

        char[] nazwa_wydzialu = stringToCharArray(b);
        int[] dlugosc_znakow = {7, 6, 10, 1, 11, 10};
        StringBuilder helpResult = new StringBuilder();
        boolean more = false;
        int ilosc_slow_w_nazwie = 0;

        String encrypted_without_coma = encryptedDocument.replace(",", "");
        String encrypted_without_coma_and_tabs = encrypted_without_coma.replaceAll("\t", "");
        String[] in = encrypted_without_coma_and_tabs.split("\\s+");

        for (String s : in) {
            if (s.length() != dlugosc_znakow[ilosc_slow_w_nazwie]) {
                if (s.length() == dlugosc_znakow[0]) {
                    ilosc_slow_w_nazwie = 1;
                    helpResult = new StringBuilder(s);
                } else {
                    ilosc_slow_w_nazwie = 0;
                    helpResult = new StringBuilder();
                }
            } else {
                helpResult.append(s);
                ilosc_slow_w_nazwie++;
            }

            if (ilosc_slow_w_nazwie == 6) {
                for (int j = 0; j < Objects.requireNonNull(nazwa_wydzialu).length; j++) {
                    if (decodeMap.containsKey(helpResult.charAt(j))) {
                        if (decodeMap.get(helpResult.charAt(j)) != nazwa_wydzialu[j]) {
                            clearMaps();
                            more = true;
                            break;
                        }
                    } else {
                        decodeMap.put(helpResult.charAt(j), nazwa_wydzialu[j]);
                        codeMap.put(nazwa_wydzialu[j], helpResult.charAt(j));
                    }
                }
                if (more == true) {
                    break;
                } else {
                    if (codeMap.size() == decodeMap.size()) {
                        break;
                    } else {
                        clearMaps();
                        ilosc_slow_w_nazwie = 0;
                        helpResult = new StringBuilder();
                    }
                }
            }
        }
    }

    public Map<Character, Character> getCode() {
        codeMap.remove(',');
        return codeMap;
    }

    public Map<Character, Character> getDecode() {
        decodeMap.remove(',');
        return decodeMap;
    }

    private void reset(){
        codeMap = new HashMap<Character, Character>();
        decodeMap = new HashMap<Character, Character>();
    }

    private void clearMaps(){
        decodeMap.clear();
        codeMap.clear();
    }

    private char[] stringToCharArray(String string) {
        char[] array;
        if (string.length() == 0) {
            return null;
        } else {
            int len = string.length();
            array = new char[len];
            for (int i = 0; i < len; i++) {
                array[i] = string.charAt(i);
            }
        }
        return array;
    }
}
