package hu.elte.iszraai.webshop.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import hu.elte.iszraai.webshop.common.Item;
import hu.elte.iszraai.webshop.common.LogUtil;
import hu.elte.iszraai.webshop.common.RMIServer;
import hu.elte.iszraai.webshop.common.WebshopServerException;

public class RMIClient extends Thread {

    private static final Command[]           COMMANDS      = Command.values();
    private static final ClientConfiguration configuration = new ClientConfiguration();

    private static RMIServer                 server;

    public static void main(final String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        server = (RMIServer) Naming.lookup(configuration.getRmiUrl());

        new RMIClient().start();
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
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
                    boolean result = executeCommand(commandNumber - 1, scanner);
                    if (!result) {
                        break;
                    }
                }
            }
        }
    }

    private boolean executeCommand(final int commandIndex, final Scanner consoleReader) {
        Command command = COMMANDS[commandIndex];

        try {
            switch (command) {
            case FIND_BY_KEY: {
                int id = readIntFromConsole("Item ID: ", consoleReader);

                Item response = server.findItemById(id);

                LogUtil.info("Response received from server: " + response);
            }
                break;
            case FIND_BY_NAME: {
                System.out.print("Item name: ");
                String name = consoleReader.nextLine();

                List<Item> response = server.findItemByName(name);

                LogUtil.info("Response received from server: " + response);
            }
                break;
            case DELETE: {
                int id = readIntFromConsole("Item ID: ", consoleReader);
                server.deleteItem(id);
            }
                break;
            case UPDATE: {
                Item item = readItemFromConsole(consoleReader);
                server.updateItem(item);
            }
                break;
            case INSERT: {
                Item item = readItemFromConsole(consoleReader);
                server.insertItem(item);
            }
                break;
            case STOP_SERVER:
                server.stopServer();
            case EXIT:
                return false;
            default:
                LogUtil.error("Command not implemented yet.");
            }
        } catch (WebshopServerException ex) {
            LogUtil.error("Error occured on server.", ex);
        } catch (RemoteException ex) {
            LogUtil.error("Communicaton error occured between the client and the server.", ex);
        }

        return true;
    }

    private int readIntFromConsole(final String label, final Scanner consoleReader) {
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

    private Item readItemFromConsole(final Scanner consoleReader) {
        int id = readIntFromConsole("Item ID: ", consoleReader);

        System.out.print("Item name: ");
        String name = consoleReader.nextLine();

        int price = readIntFromConsole("Item price: ", consoleReader);

        Item item = new Item(id, name, price);
        return item;
    }

}
