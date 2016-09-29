package webshop.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import webshop.common.Command;
import webshop.common.Item;
import webshop.common.LogUtil;

public class Client {

    private static final Command[]           COMMANDS      = Command.values();

    private static final ClientConfiguration configuration = new ClientConfiguration();

    public static void main(final String... args) throws Exception {
        int port = Integer.parseInt(configuration.getServerPort());

        try (Scanner scanner = new Scanner(System.in)) {
            try (Socket socket = new Socket("localhost", port);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                for (;;) {
                    // print the available commands to the console
                    System.out.println("Available commands:");
                    for (int i = 1; i <= COMMANDS.length; ++i) {
                        System.out.println(i + ". " + COMMANDS[i - 1]);
                    }

                    // read the selected command number
                    int commandNumber = readIntFromConsole("Selected command number: ", scanner);
                    if (commandNumber < 1 || commandNumber > COMMANDS.length) {
                        LogUtil.error("Please select a valid command number.");
                        continue;
                    } else {
                        // execute the command
                        boolean result = executeCommand(commandNumber - 1, scanner, out, in);
                        if (!result) {
                            break;
                        }
                    }
                }

            }
        }
    }

    private static boolean executeCommand(final int commandIndex, final Scanner consoleReader, final ObjectOutputStream out,
            final ObjectInputStream in) throws Exception {
        Command command = COMMANDS[commandIndex];
        out.writeUTF(command.name());

        switch (command) {
        case FIND_BY_KEY: {
            int id = readIntFromConsole("Item ID: ", consoleReader);

            out.writeInt(id);
            out.flush();

            Item response = (Item) in.readObject();

            LogUtil.info("Response received from server: " + response);
        }
            break;
        case FIND_BY_NAME: {
            System.out.print("Item name: ");
            String name = consoleReader.nextLine();
            out.writeUTF(name);
            out.flush();

            @SuppressWarnings("unchecked")
            List<Item> response = (List<Item>) in.readObject();

            LogUtil.info("Response received from server: " + response);
        }
            break;
        case DELETE: {
            int id = readIntFromConsole("Item ID: ", consoleReader);

            out.writeInt(id);
            out.flush();

            String response = in.readUTF();

            LogUtil.info("Response received from server: " + response);
        }
            break;
        case UPDATE:
        case INSERT: {
            Item item = readItemFromConsole(consoleReader);

            out.writeObject(item);
            out.flush();

            String response = in.readUTF();

            LogUtil.info("Response received from server: " + response);
        }
            break;
        case EXIT:
        case STOP_SERVER:
            out.flush();
            return false;
        default:
            LogUtil.error("Command not implemented yet.");
        }

        return true;
    }

    private static int readIntFromConsole(final String label, final Scanner consoleReader) {
        for (;;) {
            System.out.print(label);
            String inputStr = consoleReader.nextLine();
            try {
                return Integer.parseInt(inputStr);
            } catch (NumberFormatException ex) {
                LogUtil.error("Invalid integer.");
            }
        }
    }

    private static Item readItemFromConsole(final Scanner consoleReader) {
        int id = readIntFromConsole("Item ID: ", consoleReader);

        System.out.print("Item name: ");
        String name = consoleReader.nextLine();

        int price = readIntFromConsole("Item price: ", consoleReader);

        Item item = new Item(id, name, price);
        return item;
    }

}
