package webshop.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import webshop.common.Command;
import webshop.common.Item;
import webshop.common.LogUtil;

public class Client {

    private static final ClientConfiguration configuration = new ClientConfiguration();

    public static void main(final String... args) throws UnknownHostException, IOException, ClassNotFoundException {
        int port = Integer.parseInt(configuration.getServerPort());

        try (Socket socket = new Socket("localhost", port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeUTF(Command.FIND_BY_KEY.name());
            out.writeInt(101);
            out.flush();

            Item response = (Item) in.readObject();

            LogUtil.info("Response received from server: " + response);
        }
    }

}
