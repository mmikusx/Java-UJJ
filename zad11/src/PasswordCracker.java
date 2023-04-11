import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

public class PasswordCracker implements PasswordCrackerInterface{
    @Override
    public String getPassword(String host, int port) {
        String passwordResult = null;
        String schemaFromServer;
        InputStream in = null;
        InputStreamReader inReader;
        OutputStream out = null;
        BufferedReader buffReader = null;
        PrintWriter writer = null;
        String passwordCorrect1 = "+OK";
        String passwordCorrect2 = "Access granted - hasło zostało odgadnięte";

        try {
            Socket so = new Socket(host, port);
            in = so.getInputStream();
            out = so.getOutputStream();
            writer = new PrintWriter(out, true);
            inReader = new InputStreamReader(in);
            buffReader = new BufferedReader(inReader);

            System.out.println(buffReader.readLine());
            writer.println("Program");
            buffReader.readLine();
            schemaFromServer = buffReader.readLine().replaceAll("\\s", "").replace("schema:", "");
            System.out.println(schemaFromServer);

            List<Character> decodedSchema = Collections.unmodifiableList(PasswordComponents.decodePasswordSchema(schemaFromServer));

            AtomicIntegerArray id = new AtomicIntegerArray(new int[decodedSchema.size()]);
            AtomicIntegerArray max = new AtomicIntegerArray(new int[decodedSchema.size()]);

            AtomicInteger passwordSchemaId = new AtomicInteger();
            setMax(decodedSchema, max, passwordSchemaId);

            AtomicReference<LongAdder> previouslyCorrectElements = new AtomicReference<>(new LongAdder());
            AtomicReference<LongAdder> idOfElementToChange = new AtomicReference<>(new LongAdder());
            String serverResponse;
            buffReader.readLine();

            while (passwordResult == null) {
                passwordSchemaId.set(0);
                String tmpPasswordResult = "";

                Iterator<Character> iterator = decodedSchema.iterator();
                while (iterator.hasNext()) {
                    Character element = iterator.next();
                    tmpPasswordResult += PasswordComponents.passwordComponents.get(element).get(id.get(passwordSchemaId.get()));
                    passwordSchemaId.getAndIncrement();
                }

                String tmpPasswordResultCheck;
                tmpPasswordResultCheck = tmpPasswordResult;
                writer.println(tmpPasswordResultCheck);
                serverResponse = buffReader.readLine();
                if (serverResponse.equals(passwordCorrect1) || serverResponse.equals(passwordCorrect2)) {
                    passwordResult = tmpPasswordResultCheck;
                } else {
                    ifNotEqual(serverResponse, previouslyCorrectElements.get(), id, idOfElementToChange.get(), max);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeEverything(in, out, buffReader, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return passwordResult;
    }

    protected void closeEverything(InputStream in,
                                   OutputStream out,
                                   BufferedReader buffReader,
                                   PrintWriter prtWr) throws IOException {
        if(in != null){
            in.close();
        }
        if(out != null){
            out.close();
        }
        if(buffReader != null){
            buffReader.close();
        }
        if(prtWr != null){
            prtWr.close();
        }
    }

    protected void setMax(List<Character> decodedSchema,
                          AtomicIntegerArray max,
                          AtomicInteger passwordSchemaId){
        int i = 0;
        while (i < decodedSchema.size()) {
            Character element = decodedSchema.get(i);
            max.set(passwordSchemaId.get(), PasswordComponents.passwordComponents.get(element).size());
            passwordSchemaId.set(++i);
        }
    }

    protected void ifNotEqual(String serverResponse,
                              LongAdder previouslyCorrectElements,
                              AtomicIntegerArray id,
                              LongAdder idOfElementToChange,
                              AtomicIntegerArray max){
        AtomicInteger howManyCorrect = new AtomicInteger();
        searchForNumberInResponse(serverResponse, howManyCorrect);
        if (howManyCorrect.get() >= previouslyCorrectElements.sum()) {
            Object idObj = id.get(Math.toIntExact(idOfElementToChange.sum()));
            int idInt = (int) idObj;
            Object maxObj = max.get(Math.toIntExact(idOfElementToChange.sum()));
            int maxInt = (int) maxObj;
            if (idInt + 1 == maxInt) {
                idOfElementToChange.add(1);
            } else {
                id.getAndAdd(Math.toIntExact(idOfElementToChange.sum()), (1));
                previouslyCorrectElements.add(howManyCorrect.get() - previouslyCorrectElements.sum());
            }
        } else {
            id.getAndAdd(Math.toIntExact(idOfElementToChange.sum()), -(1));
            idOfElementToChange.add(1);
        }
    }

    protected void searchForNumberInResponse(String serverResponse,
                                             AtomicInteger howManyCorrect){
        char[] charsInServerResponse = serverResponse.toCharArray();
        for(char c : charsInServerResponse){
            if(PasswordComponents.numbers.contains(c)){
                howManyCorrect.set(Integer.parseInt(String.valueOf(c)));
            }
        }
    }
}