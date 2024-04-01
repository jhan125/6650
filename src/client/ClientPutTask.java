package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

import api.KeyValueRpc;
import api.KeyValueResponse;

public class ClientPutTask implements Runnable {
    private final String key;
    private final String value;
    private final KeyValueRpc stub;
    public KeyValueResponse result;

    public ClientPutTask(String key, String value, KeyValueRpc stub) {
        this.key = key;
        this.value = value;
        this.stub = stub;
        this.result = new KeyValueResponse();
    }

    @Override
    public void run() {
        try {
            this.result = stub.put(this.key, this.value);
            if (result.isSuccess() ){
                String success = String.format("PUT/UPDATE Operation for <key=%s, value=%s> completed successfully!", this.key, this.value);
                ClientLogger.info(success);
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
