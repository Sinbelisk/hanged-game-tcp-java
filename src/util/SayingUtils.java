package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SayingUtils {
    private static final String FILES_PATH = "Files/";
    private static final Map<String, File> registeredDocuments = new HashMap<>();

    /**
     * Declaración de todos los documentos que contienen las palabras.
     */
    static {
        scanFileFolder();
    }

    /**
     * Devuelve una lista conteniendo las palabras del documento especificado
     * @param name nombre de la colección de palabras.
     * @return una lista con todas las palabras del documento, o null si la colección de palabras no se ha encontrado.
     * @throws IOException si ocurre un error al leer el fichero
     */
    public static Queue<String> getWordsFromDocumentName(String name) throws IOException {
        File collection = registeredDocuments.get(name);

        if (collection == null || !collection.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(collection))) {
            List<String> wordList = reader.lines()
                    .collect(Collectors.toList());

            Collections.shuffle(wordList);
            return new LinkedList<>(wordList);
        }
    }

    private static void scanFileFolder(){
        try{
            Files.list(Paths.get(FILES_PATH))
                    .filter(Files::isRegularFile)
                    .forEach(file -> addFileToMap(new File(file.toString())));
        } catch (IOException e){
            // TODO log
        }
    }

    private static void addFileToMap(File file){
        String key = getParsedFileName(file.getName());
        System.out.println(key);
        registeredDocuments.put(key, file);
    }
    private static String getParsedFileName(String fileName){
        return fileName.split(".txt")[0];
    }

    public String getAvailibleCollections(){
        StringBuilder sb = new StringBuilder();
        for (String name : registeredDocuments.keySet()) {
            sb.append(name).append("\n");
        }
        return sb.toString();
    }
}