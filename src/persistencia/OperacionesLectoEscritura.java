package persistencia;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public class OperacionesLectoEscritura {

    public static void grabar(String nombreArchivo, JSONObject jsonObject) throws IOException {
        // Crear directorio si no existe
        File archivo = new java.io.File(nombreArchivo);
        File directorio = archivo.getParentFile();
        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }
        
        FileWriter fileWriter = new FileWriter(nombreArchivo, false); // false = sobrescribir archivo
        fileWriter.write(jsonObject.toString(4));
        // pongo indentacion para que el Json quede con formato indentado
        fileWriter.close();
    }
}
