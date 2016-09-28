package webshop.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import webshop.common.Command;
import webshop.common.Item;
import webshop.common.LogUtil;

public class Server extends Thread {

    private static final ServerConfiguration configuration;
    private static final DAO                 dao;

    private static boolean                   running = true;

    static {
        configuration = new ServerConfiguration();
        dao = new DAO(configuration);
    }

    public static void main(final String[] args) throws IOException {
        int port = Integer.parseInt(configuration.getServerPort());

        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setSoTimeout(1000);

            while (running) {
                try {
                    new Server(ss.accept()).start();
                } catch (SocketTimeoutException ex) {
                }
            }
        }
    }

    private final Socket socket;

    private Server(final Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            serveClient(in, out);
        } catch (IOException ex) {
            LogUtil.error(ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            LogUtil.error(ex);
        }
    }

    private static void serveClient(final ObjectInputStream in, final ObjectOutputStream out) throws IOException {
        OUTER: for (;;) {
            String commandStr = in.readUTF();
            LogUtil.debug(commandStr + " command received from client");

            Command command = Command.valueOf(commandStr);

            switch (command) {
            case FIND_BY_KEY: {
                int id = in.readInt();
                LogUtil.debug("Item ID received from client: " + id);

                Item item = dao.findItemById(id);

                out.writeObject(item);
                out.flush();
            }
                break;
            case FIND_BY_NAME: {
                String name = in.readUTF();
                LogUtil.debug("Item name received from client: " + name);

                List<Item> items = dao.findItemsByName(name);

                out.writeObject(items);
                out.flush();
            }
                break;
            case INSERT:
                break;
            case UPDATE:
                break;
            case DELETE:
                break;
            case STOP_SERVER:
                running = false;
            case EXIT:
                break OUTER;
            default:
                LogUtil.warn("Unknown command ignored: " + commandStr);
            }
        }

    }

}
