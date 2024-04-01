package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

import api.KeyValueRpc;
import api.KeyValueResponse;

public class ClientDeleteTask implements Runnable {
    private final String key;
    private final KeyValueRpc stub;
    public KeyValueResponse result;

    public ClientDeleteTask(String key, KeyValueRpc stub) {
        this.key = key;
        this.stub = stub;
        this.result = new KeyValueResponse();
    }

    @Override
    public void run() {
        String value;
        try {
            value = stub.get(key).getValue();
            this.result = stub.delete(this.key);
            if (result.isSuccess()) {
                String success = String.format("DELETE Operation for <key=%s, value=%s> completed successfully!", this.key, value);
                System.out.println(success);
            } else {
                System.out.println(String.format("Error! %s", result.getErrorMsg()));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ClientApp.INVALID_KEY_OR_VALUE);
            ClientLogger.error("Error: " + e.getMessage());
            this.result.setSuccess(false);
        } catch (Exception e) {
            ClientLogger.error("Error: " + e.getMessage());
            this.result.setSuccess(false);
        }
    }
}