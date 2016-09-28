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

        try (Scanner consoleIn = new Scanner(System.in)) {
            try (Socket socket = new Socket("localhost", port);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                for (;;) {
                    System.out.println("Available commands:");
                    for (int i = 1; i <= COMMANDS.length; ++i) {
                        System.out.println(i + ". " + COMMANDS[i - 1]);
                    }
                    System.out.print("Selected command number: ");

                    String selectedCommand = consoleIn.nextLine();
                    boolean validInput = true;
                    try {
                        int commandNumber = Integer.parseInt(selectedCommand);
                        if (commandNumber < 1 || commandNumber > COMMANDS.length) {
                            validInput = false;
                        } else {
                            boolean result = executeCommand(commandNumber - 1, consoleIn, out, in);
                            if (!result) {
                                break;
                            }
                        }
                    } catch (NumberFormatException ex) {
                        validInput = false;
                    }

                    if (!validInput) {
                        LogUtil.error("Please select a command number from the top.");
                        continue;
                    }
                }

            }
        }
    }

    private static boolean executeCommand(final int commandIndex, final Scanner consoleInput, final ObjectOutputStream out,
            final ObjectInputStream in) throws Exception {
        Command command = COMMANDS[commandIndex];
        out.writeUTF(command.name());

        switch (command) {
        case FIND_BY_KEY: {
            System.out.print("Item ID: ");
            int id = 0;
            try {
                id = Integer.parseInt(consoleInput.nextLine());
            } catch (NumberFormatException ex) {
            }
            out.writeInt(id);
            out.flush();

            Item response = (Item) in.readObject();

            LogUtil.info("Response received from server: " + response);
        }
            break;
        case FIND_BY_NAME: {
            System.out.print("Item name: ");
            String name = consoleInput.nextLine();
            out.writeUTF(name);
            out.flush();

            @SuppressWarnings("unchecked")
            List<Item> response = (List<Item>) in.readObject();

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

}
