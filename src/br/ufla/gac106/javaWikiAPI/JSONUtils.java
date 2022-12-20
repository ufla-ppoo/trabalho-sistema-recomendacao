package br.ufla.gac106.javaWikiAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Classe com métodos utilitários para manipular objetos e strings JSON
 */
public class JSONUtils {
    /**
     * Retorna uma representação amigável (indentada e mais legível) da string JSON passada
     * 
     * @param jsonString String JSON
     * 
     * @return Representação amigável da string passada
     */
    public static String stringAmigavel(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(jsonString);
        return gson.toJson(je);
    }
}
