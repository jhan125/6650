package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.lang.InterruptedException;

import api.KeyValueRpc;
import api.KeyValueResponse;

/**
 * Implementation of the KeyValueRpc interface for remote access to a key-value store.
 * This class ensures thread-safe operations on the key-value store and handles remote method invocation.
 */
public class KeyValueRpcImpl extends UnicastRemoteObject implements KeyValueRpc {

    /**
     * Constructs a KeyValueRpcImpl instance and exports it to allow remote access.
     *
     * @throws RemoteException if an error occurs during object export.
     */
    protected KeyValueRpcImpl() throws RemoteException {
        super();
    }

    @Override
    public KeyValueResponse get(String key) throws IllegalArgumentException, RemoteException, InterruptedException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty after being trimmed.");
        }
        // Proceed with the operation if the input is valid
        key = key.trim();
        String value = KeyValue.getInstance().get(key);
        KeyValueResponse response = new KeyValueResponse();
        response.setOperation("GET");
        if (value == null) {
            response.setErrorMsg(String.format("Key=%s is not found in the database.", key));
            response.setSuccess(false);
        } else {
            response.setSuccess(true);
            response.setValue(value);
        }
        return response;
    }

    @Override
    public KeyValueResponse put(String key, String value) throws IllegalArgumentException, RemoteException, InterruptedException {
        /** For TEST: Simulating some long-running operation */
//        System.out.println("Sleeping for 10 seconds...");
//
//        try {
//            // Simulate a long-running operation
//            Thread.sleep(10000); // 10,000 milliseconds = 10 seconds
//        } catch (InterruptedException e) {
//            System.err.println("The sleeping thread was interrupted.");
//            Thread.currentThread().interrupt();
//        }

        if (key == null || key.trim().isEmpty() || value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Key and value must not be null or empty after being trimmed.");
        }
        // Proceed with the operation if the input is valid
        key = key.trim();
        value = value.trim();
        KeyValue.getInstance().put(key, value);
        KeyValueResponse response = new KeyValueResponse();
        response.setOperation("PUT");
        response.setSuccess(true);
        return response;
    }

    @Override
    public KeyValueResponse delete(String key) throws IllegalArgumentException, RemoteException, InterruptedException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty after being trimmed.");
        }
        // Proceed with the operation if the input is valid
        key = key.trim();
        boolean success = KeyValue.getInstance().delete(key);
        KeyValueResponse response = new KeyValueResponse();
        response.setOperation("DELETE");
        response.setSuccess(success);
        if (!success) {
            response.setErrorMsg(String.format("Key=%s is not found or has already been deleted from the database.", key));
        }
        return response;
    }
}
