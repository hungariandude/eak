package hu.elte.iszraai.webshop.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import hu.elte.iszraai.webshop.common.Item;
import hu.elte.iszraai.webshop.common.LogUtil;
import hu.elte.iszraai.webshop.common.RMIServer;
import hu.elte.iszraai.webshop.common.WebshopServerException;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {

    private static final long                serialVersionUID = 1L;

    private static final ServerConfiguration configuration;
    private static final DAO<Item, Integer>  dao;

    static {
        configuration = new ServerConfiguration();
        dao = new DAO<>(configuration, Item.class);
    }

    public RMIServerImpl() throws RemoteException {
        super();
    }

    public static void main(final String[] args) throws RemoteException, MalformedURLException {
        RMIServer server = new RMIServerImpl();

        int port = Integer.parseInt(configuration.getServerPort());
        LocateRegistry.createRegistry(port);
        Naming.rebind(configuration.getRmiUrl(), server);
    }

    @Override
    public void stopServer() throws RemoteException {
        try {
            Naming.unbind(configuration.getRmiUrl());
            UnicastRemoteObject.unexportObject(this, true);
        } catch (MalformedURLException | NotBoundException ex) {
            LogUtil.error("Server stop failed", ex);
        }
    }

    @Override
    public Item findItemById(final int itemId) throws WebshopServerException {
        LogUtil.debug("Item ID received from client: " + itemId);

        Item item = dao.findById(itemId);

        return item;
    }

    @Override
    public List<Item> findItemByName(final String name) throws WebshopServerException {
        LogUtil.debug("Item name received from client: " + name);

        List<Item> items = dao.findAllByProperty("name", name);

        return items;
    }

    @Override
    public void insertItem(final Item item) throws WebshopServerException {
        LogUtil.debug("Item object received from client: " + item);

        dao.insert(item);
    }

    @Override
    public void updateItem(final Item item) throws WebshopServerException {
        LogUtil.debug("Item object received from client: " + item);

        dao.update(item);
    }

    @Override
    public void deleteItem(final int itemId) throws WebshopServerException {
        LogUtil.debug("Item ID received from client: " + itemId);

        dao.deleteById(itemId);
    }

}
