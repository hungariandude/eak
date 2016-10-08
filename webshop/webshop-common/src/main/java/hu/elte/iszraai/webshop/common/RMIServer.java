package hu.elte.iszraai.webshop.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote {

    void stopServer() throws RemoteException;

    Item findItemById(int itemId) throws RemoteException, WebshopServerException;

    List<Item> findItemByName(String name) throws RemoteException, WebshopServerException;

    void insertItem(Item item) throws RemoteException, WebshopServerException;

    void updateItem(Item item) throws RemoteException, WebshopServerException;

    void deleteItem(int itemId) throws RemoteException, WebshopServerException;

}
