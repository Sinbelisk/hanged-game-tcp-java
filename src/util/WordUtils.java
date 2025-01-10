package util;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordUtils {
    private static final Map<String, String> registeredDocuments = new HashMap<>();

    /**
     * Declaración de todos los documentos que contienen las palabras.
     */
    static {
        registeredDocuments.put("default", Paths.get("Files/default.txt").toString());
        registeredDocuments.put("videogames.txt", Paths.get("Files/videogames.txt").toString());
    }

    /**
     * Devuelve una lista conteniendo las palabras del documento especificado
     * @param name nombre de la colección de palabras.
     * @return una lista con todas las palabras del documento, o null si la colección de palabras no se ha encontrado.
     * @throws IOException si ocurre un error al leer el fichero
     */
    private static List<String> getWordsFromDocument(String name) throws IOException {
        File words = new File(registeredDocuments.get(name));

        if(!words.exists()) return null;

        List<String> wordList = new ArrayList<>();
        String buffer;
        try(BufferedReader reader = new BufferedReader(new FileReader(words))){
            while((buffer = reader.readLine()) != null){
                wordList.add(buffer);
            }
        }

        return wordList;
    }
}