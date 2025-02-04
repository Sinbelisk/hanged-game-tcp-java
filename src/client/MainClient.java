package client;

import java.net.InetAddress;

public class MainClient {
    private static final int SERVER_PORT = 2050;
    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: debes introducir una IP válida como parámetro.");
            System.out.println("Uso: MainClient <ip>");
            return;
        }

        String ip = args[0];

        if (!isValidIPAddress(ip)) {
            System.err.println("Error: Dirección IP no válida -> " + ip);
            return;
        }

        Client client = new Client(ip, SERVER_PORT);
        client.connect();
    }

    private static boolean isValidIPAddress(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
