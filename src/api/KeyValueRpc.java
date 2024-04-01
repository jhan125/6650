package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.lang.InterruptedException;

/**
 * Defines the Remote Procedure Call (RPC) interface for interacting with a key-value store.
 * This interface allows clients to perform get, put, and delete operations on the key-value store
 * in a thread-safe manner over a network.
 */
public interface KeyValueRpc extends Remote {

    /**
     * Retrieves the value associated with the specified key from the key-value store.
     *
     * @param key The key whose associated value is to be returned.
     * @return A {@link KeyValueResponse} object containing the operation result, including
     *         the value if the operation was successful, or an error message if not.
     * @throws RemoteException If an error occurs during the remote method call.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    KeyValueResponse get(String key) throws RemoteException, InterruptedException;

    /**
     * Inserts or updates a key-value pair in the key-value store.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @return A {@link KeyValueResponse} object containing the operation result, indicating
     *         success or failure, and an error message if applicable.
     * @throws RemoteException If an error occurs during the remote method call.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    KeyValueResponse put(String key, String value) throws RemoteException, InterruptedException;

    /**
     * Removes the key-value pair associated with the specified key from the key-value store, if it exists.
     *
     * @param key The key whose key-value pair is to be removed.
     * @return A {@link KeyValueResponse} object containing the operation result, indicating
     *         success or failure, and an error message if the key was not found.
     * @throws RemoteException If an error occurs during the remote method call.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    KeyValueResponse delete(String key) throws RemoteException, InterruptedException;
}
