package common;

import java.io.IOException;

public interface Connection{
    void open() throws IOException;
    void close() throws IOException;
    void send(String message) throws IOException;
    String receive() throws IOException;
}
