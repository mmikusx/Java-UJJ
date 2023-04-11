public class Decode extends DecoderInterface{

    protected String in = "";
    protected String toReset = "";
    protected String number = "";
    protected String result = "";
    protected String wzorKodowania = "";
    protected String zakodowanaLiczba = "";

    public void input(int bit) {
        in = Integer.toString(bit);
        number += in;
    }

    public String output() {
        if(number == null)
            return result;

        int pierwszaJedynka;
        pierwszaJedynka = number.indexOf("1");
        if(!number.contains("1"))
            return result;

        int dlugosc = number.length();
        number = number.substring(pierwszaJedynka, dlugosc);
        dlugosc = number.length();

        int pierwszeZero;
        pierwszeZero = number.indexOf("0");
        if(!number.contains("0"))
            return result;

        wzorKodowania = number.substring(0, pierwszeZero); //liczba jedynek w kodowaniu ( X lub XX )
        number = number.substring(pierwszeZero+1, dlugosc);

        int liczbaJedynekWKodowaniu = wzorKodowania.length();
        dlugosc = number.length();

        result += "0";

        while(dlugosc > 0){
            if(!number.contains("1"))
                break;
            int x;
            x = number.indexOf("1");
            number = number.substring(x, dlugosc);
            dlugosc = number.length();

            if(!number.contains("0"))
                break;
            int y;
            y = number.indexOf("0");

            zakodowanaLiczba = number.substring(0, y+1);
            number = number.substring(y+1, dlugosc);
            dlugosc = number.length();
            int dlugoscKodowanejLiczbyBezZera;
            dlugoscKodowanejLiczbyBezZera = zakodowanaLiczba.length() - 1;

            switch(dlugoscKodowanejLiczbyBezZera/liczbaJedynekWKodowaniu){
                case 1:
                    result += "0";
                    break;

                case 2:
                    result += "1";
                    break;

                case 3:
                    result += "2";
                    break;

                case 4:
                    result += "3";
                    break;
            }
        }
        return result;
    }

    public void reset() {
        number = toReset;
        result = toReset;
    }
}