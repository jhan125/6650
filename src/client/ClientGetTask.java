package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

import api.KeyValueRpc;
import api.KeyValueResponse;

public class ClientGetTask implements Runnable {
    private final String key;
    private final KeyValueRpc stub;
    public KeyValueResponse result;

    public ClientGetTask(String key, KeyValueRpc stub) {
        this.key = key;
        this.stub = stub;
        this.result = new KeyValueResponse();
    }

    @Override
    public void run() {
        String value;
        try {
            this.result = stub.get(this.key);
            value = result.getValue();
            if (result.isSuccess()){
                String success = String.format("GET Operation for <key=%s> completed successfully! <value=%s>", this.key, value);
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
            System.err.println("Client GET operation exception: " + e.toString());
            this.result.setSuccess(false);
        }
    }
}