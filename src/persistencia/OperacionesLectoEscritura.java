package persistencia;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class OperacionesLectoEscritura {

    public static void grabar(String nombreArchivo, JSONObject jsonObject) {
        try {
            FileWriter fileWriter = new FileWriter(nombreArchivo, false); // false = sobrescribir archivo
            fileWriter.write(jsonObject.toString(4));
            // pongo indentacion para que el Json quede con formato indentado
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONTokener leer(String nombreArchivo) {
        JSONTokener tokener = null;
        try {
            tokener = new JSONTokener(new FileReader(nombreArchivo));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tokener;
    }
}
