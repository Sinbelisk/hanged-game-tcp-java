package util;

import common.SocketConnection;

import java.net.Socket;

public class StringUtils {

    public static String[] getSeparatedStrings(String string){
        return string.split("/s");
    }

    public static String getUserId(Socket socket, String userName){
        return userName + "@" + socket.getInetAddress().getHostAddress();
    }

    public static String[] parseArguments(String[] strings){
        int size = strings.length - 1;
        String[] parsedStrings = new String[size];
        System.arraycopy(strings, 1, parsedStrings, 0, size);

        return parsedStrings;
    }

}
