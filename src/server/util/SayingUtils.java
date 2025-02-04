package server.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilidades para gestionar y acceder a colecciones de palabras almacenadas en archivos de texto.
 * <p>
 * Esta clase permite cargar colecciones de palabras desde archivos de texto ubicados en una carpeta
 * específica y ofrece métodos para obtener las palabras de un archivo y listar las colecciones disponibles.
 * </p>
 */
public class SayingUtils {
    private static final String FILES_PATH = "Files/";  // Ruta donde se almacenan los archivos de colecciones de palabras
    private static final Map<String, File> registeredDocuments = new HashMap<>();  // Mapa que asocia los nombres de colección a los archivos correspondientes

    /**
     * Inicializa las colecciones de palabras escaneando la carpeta de archivos.
     * Se ejecuta al cargar la clase.
     */
    static {
        scanFileFolder();  // Llama al método que escanea la carpeta de archivos
    }

    /**
     * Devuelve una lista con las palabras de la colección especificada.
     *
     * <p>
     * El método lee el archivo correspondiente al nombre de la colección y devuelve una lista con las palabras
     * en orden aleatorio. Si el archivo no existe o no puede ser leído, se devuelve {@code null}.
     * </p>
     *
     * @param name El nombre de la colección de palabras a cargar.
     * @return Una cola de palabras aleatorias si la colección existe, o {@code null} si no se encuentra el archivo o hay un error de lectura.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static Queue<String> getWordsFromDocumentName(String name) throws IOException {
        File collection = registeredDocuments.get(name);

        if (collection == null || !collection.exists()) return null;  // Si el archivo no se encuentra, retorna null
        try (BufferedReader reader = new BufferedReader(new FileReader(collection))) {
            List<String> wordList = reader.lines()
                    .collect(Collectors.toList());  // Lee todas las líneas y las convierte en una lista

            Collections.shuffle(wordList);  // Aleatoriza las palabras
            return new LinkedList<>(wordList);  // Retorna una cola con las palabras
        }
    }

    /**
     * Escanea la carpeta de archivos {@code FILES_PATH} en busca de archivos de texto.
     * Los archivos encontrados son añadidos al mapa {@code registeredDocuments} para su posterior acceso.
     */
    private static void scanFileFolder() {
        try {
            Files.list(Paths.get(FILES_PATH))  // Lista todos los archivos en el directorio FILES_PATH
                    .filter(Files::isRegularFile)  // Filtra solo archivos regulares (no directorios)
                    .forEach(file -> addFileToMap(new File(file.toString())));  // Añade cada archivo al mapa
        } catch (IOException e) {
            // TODO: Agregar registro de log en caso de error
        }
    }

    /**
     * Añade un archivo al mapa {@code registeredDocuments} con el nombre de archivo procesado como clave.
     *
     * @param file El archivo que se debe añadir al mapa.
     */
    private static void addFileToMap(File file) {
        String key = getParsedFileName(file.getName());  // Obtiene el nombre procesado del archivo
        System.out.println(key);  // Imprime el nombre de la colección
        registeredDocuments.put(key, file);  // Añade el archivo al mapa con su nombre procesado como clave
    }

    /**
     * Procesa el nombre de un archivo para eliminar la extensión ".txt" y obtener solo el nombre de la colección.
     *
     * @param fileName El nombre del archivo que se desea procesar.
     * @return El nombre de la colección, sin la extensión ".txt".
     */
    private static String getParsedFileName(String fileName) {
        return fileName.split(".txt")[0];  // Divide el nombre del archivo y toma la parte antes de ".txt"
    }

    /**
     * Devuelve una lista de todas las colecciones de palabras disponibles, es decir, los nombres de los archivos cargados.
     *
     * @return Una cadena con los nombres de todas las colecciones, cada uno en una nueva línea.
     * TODO: remove
     */
    public String getAvailibleCollections() {
        StringBuilder sb = new StringBuilder();
        for (String name : registeredDocuments.keySet()) {  // Recorre las claves del mapa (nombres de las colecciones)
            sb.append(name).append("\n");  // Añade cada nombre al StringBuilder
        }
        return sb.toString();  // Devuelve la cadena con todos los nombres
    }
}
