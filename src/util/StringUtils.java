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

}
