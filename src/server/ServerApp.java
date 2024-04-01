package server;

import java.io.IOException;
import java.util.Arrays;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.rmi.RemoteException;

import api.KeyValueRpc;
import api.KeyValueResponse;

/**
 * ServerApp is the main class for a server application that that initializes and binds the KeyValueRpcImpl
 * to the RMI registry for remote access.
 */
public class ServerApp {

    // Record the application start time for logging or other purposes
    public static long appStartTime = System.currentTimeMillis();

    /**
     * Constructs a ServerApp instance and binds it to the RMI registry.
     *
     * @param port The port number on which the server should listen and accept connections.
     * @throws RemoteException If a RemoteException occurs during the RMI operation.
     */
    protected ServerApp() throws RemoteException {
        try {
            int port = Integer.parseInt(System.getenv("RMI_PORT"));
            // Create the RMI registry on the dynamically obtained port
            LocateRegistry.createRegistry(port);
            ServerLogger.info("RMI registry created on port: " + port);

            // Instantiate the KeyValueRpcImpl
            KeyValueRpc keyValueStore = new KeyValueRpcImpl();

            // Construct the RMI registry binding name with the provided port
            String serviceName = "rpc-server-" + port;

            // Bind the KeyValueRpcImpl instance to the RMI registry
            Naming.rebind("//localhost:" + port + "/" + serviceName, keyValueStore);

            // Log server readiness
            ServerLogger.info("Server ready and listening on port: " + port);
        } catch (RemoteException | MalformedURLException e) {
            ServerLogger.error("Error occurred while setting up the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The main method is the entry point of the server application.
     *
     * @param args Command-line arguments, expects a single argument for the server port.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            ServerLogger.error(String.format("Invalid arguments [%s]. Expect usage: java server.ServerApp <port>", Arrays.toString(args)));
            return;
        }

        try {
            new ServerApp();
        } catch (NumberFormatException e) {
            ServerLogger.error("Error: Port number must be an integer. " + e.getMessage());
        } catch (RemoteException e) {
            ServerLogger.error("Error: RemoteException occurred while starting the server: " + e.getMessage());
        }
    }
}
