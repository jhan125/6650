package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ConnectIOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.lang.Thread;

import api.KeyValueRpc;
import api.KeyValueResponse;

/**
 * ClientApp is the main class for the client application of a key-value store.
 * It handles command-line arguments to establish a connection to the server and
 * interacs with a remote key-value store via RMI.
 * It allows performing basic CRUD operations like get, put, and delete on the key-value store.
 */
public class ClientApp {

    private static String host;
    private static int port;
    private static String serverName;

    public static final String INSTRUCTIONS = "\nHow to Enter Your Request:\n" +
            "Enter 1: GET the value by a certain key\n" +
            "Enter 2: ADD or UPDATE a key-value pair\n" +
            "Enter 3: DELETE an existing key-value pair\n" +
            "Enter 4: EXIT this program\n" +
            "--------------------------------\n" +
            "Please enter a number for your request: ";
    public static final String INVALID_OPERATION = "Invalid input. Please enter 1 or 2 or 3 or 4 to select an operation.";
    public static final String INVALID_KEY_OR_VALUE = "Key or value must NOT be NULL or EMPTY. Please enter a valid input.";

    /**
     * Main entry point of the application. Parses command-line arguments to
     * establish a connection to the server and handles connection errors.
     *
     * @param args Command-line arguments specifying the server's hostname, port, and protocol.
     */
    public static void main(String[] args) {
        try {
            // Set timeout for RMI calls to 3 seconds
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", "3000");

            if (args.length < 2) {
                System.out.println("Invalid arguments, usage: java ClientApp <host> <port> [--preload]" +
                        "e.g. `java ClientApp localhost 1099 --preload` or `java ClientApp localhost 1099`");
                return;
            }
            host = args[0];
            port = Integer.parseInt(args[1]);
            serverName = "rpc-server-" + port;
            boolean choosePreload = args.length > 2 && args[2].equals("--preload");

            ClientLogger.info(String.format("Client attempting to build RMI connection with host[%s] port[%s]...", host, port));

            Registry registry = LocateRegistry.getRegistry(host, port);
            KeyValueRpc serverStub = (KeyValueRpc) registry.lookup(serverName);

            ClientLogger.info(String.format("Client successfully built RMI connection with host[%s] port[%s]...", host, port));

            if (choosePreload) {
                preloadData(serverStub);
            }

            /** Test concurrent client requests */
//            int numberOfThreads = 5;
//
//            for (int i = 0; i < numberOfThreads; i++) {
//                String key = "6650";
//                String value = "distributed systems";
//                ClientPutTask task = new ClientPutTask(key, value, serverStub);
//                ClientLogger.info(String.format("PUT/UPDATE Operation for <key=%s, value=%s> completed successfully!", key, value));
//                Thread thread = new Thread(task);
//                thread.start();
//            }

            /** Test timeout */
//            String key = "6650";
//            String value = "distributed systems";
//            ClientPutTask task = new ClientPutTask(key, value, serverStub);
//            Thread thread = new Thread(task);
//            thread.start();

            handleConnection(serverStub);
        } catch (RemoteException | NotBoundException | IllegalStateException | NumberFormatException | InterruptedException ex) {
            ClientLogger.error("Client setup error: " + ex.getMessage());
        }
    }

    /**
     * Handles the connection to the server and user input for performing operations.
     *
     * @param stub The remote stub of the key-value store service.
     * @throws RemoteException If a remote method call fails.
     */
    private static void handleConnection(KeyValueRpc stub) throws RemoteException {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println(INSTRUCTIONS);
                try {
                    String input = sc.nextLine();
                    int userOption = Integer.parseInt(input.trim());
                    if (userOption == 4) {
                        ClientLogger.info("User chose to exit the application.");
                        break;
                    } else if (userOption < 1 || userOption > 4) {
                        System.out.println(INVALID_OPERATION);
                        continue; // Skip the rest of the loop and prompt again
                    }
                    handleUserInput(userOption, stub, sc);
                } catch (NumberFormatException ex) {
                    System.out.println(INVALID_OPERATION);
                    ClientLogger.error("Invalid number format: " + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid argument: " + ex.getMessage());
                    ClientLogger.error("Invalid argument provided: " + ex.getMessage());
                } catch (RemoteException ex) {
                    System.out.println("A remote exception occurred: " + ex.getMessage());
                    ClientLogger.error("Remote exception: " + ex.getMessage());
                    throw ex;
                } catch (Exception ex) {
                    // Catch-all for any other exceptions that weren't anticipated
                    System.out.println("An unexpected error occurred: " + ex.getMessage());
                    ClientLogger.error("Unexpected exception: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            // Handle exceptions from Scanner or any other initialization errors
            System.out.println("An error occurred initializing the user input handling: " + e.getMessage());
            ClientLogger.error("Initialization exception: " + e.getMessage());
        }
    }

    /**
     * Handles user input for CRUD operations on the key-value store.
     *
     * @param selectedOption The user-selected operation.
     * @param stub The remote stub of the key-value store service.
     * @param sc The scanner for reading user input.
     * @throws RemoteException If a remote method call fails.
     */
    private static void handleUserInput(int selectedOption, KeyValueRpc stub, Scanner sc) throws RemoteException {
        String key, value;
        KeyValueResponse result;
        Thread thread;
        switch (selectedOption) {
            case 1: // Get operation
                System.out.println("Enter the key: ");
                key = sc.nextLine();
                if (key == null || key.trim().isEmpty()) {
                    System.out.println(INVALID_KEY_OR_VALUE);
                    break;
                }
                ClientGetTask get = new ClientGetTask(key.trim(), stub);
                thread = new Thread(get);
                thread.start();
                try {
                    thread.join(); // Wait for the get operation thread to finish
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    System.out.println("GET Operation interrupted.");
                }
                break;
            case 2: // Put operation
                System.out.println("Enter the key: ");
                key = sc.nextLine();
                if (key == null || key.trim().isEmpty()) {
                    System.out.println(INVALID_KEY_OR_VALUE);
                    break;
                }
                System.out.println("Enter the value: ");
                value = sc.nextLine();
                if (key == null || key.trim().isEmpty()) {
                    System.out.println(INVALID_KEY_OR_VALUE);
                    break;
                }
                ClientPutTask put = new ClientPutTask(key.trim(), value.trim(), stub);
                thread = new Thread(put);
                thread.start();
                try {
                    thread.join(); // Wait for the put operation thread to finish
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    System.out.println("PUT Operation interrupted.");
                }
                break;
            case 3: // Delete operation
                System.out.println("Enter the key: ");
                key = sc.nextLine();
                if (key == null || key.trim().isEmpty()) {
                    System.out.println(INVALID_KEY_OR_VALUE);
                    break;
                }
                ClientDeleteTask delete = new ClientDeleteTask(key.trim(), stub);
                thread = new Thread(delete);
                thread.start();
                try {
                    thread.join(); // Wait for the delete operation thread to finish
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    System.out.println("DELETE Operation interrupted.");
                }
                break;
            default:
                System.out.println(INVALID_OPERATION);
                break;
        }
    }

    /**
     * Performs initial data loading to the key-value store via RPC.
     * The initial data loading includes generating a list of predefined requests to be sent to the server.
     * These requests are used to pre-populate the server with data and perform a series of PUT, GET, and
     * DELETE operations.
     *
     * @param stub The remote stub of the key-value store service.
     * @throws RemoteException If a remote method call fails.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    public static void preloadData(KeyValueRpc stub) throws RemoteException, InterruptedException {
        // Preload data
        doPut("6650", "Scalable Distributed Systems", stub);
        doPut("5800", "Algorithms", stub);
        doPut("5700", "Computer Networking", stub);
        doPut("6140", "Machine Learning", stub);
        doPut("5200", "Database Management", stub);

        doGet("6650", stub);
        doGet("5800", stub);
        doGet("5700", stub);
        doGet("6140", stub);
        doGet("5200", stub);

        doDelete("6650", stub);
        doDelete("5800", stub);
        doDelete("5700", stub);
        doDelete("6140", stub);
        doDelete("5200", stub);

        doPut("6650", "Scalable Distributed Systems", stub);
        doPut("5800", "Algorithms", stub);
        doPut("5700", "Computer Networking", stub);
        doPut("6140", "Machine Learning", stub);
        doPut("5200", "Database Management", stub);
    }

    /**
     * Performs a "get" operation on the remote key-value store.
     * This method retrieves the value associated with a specified key.
     *
     * @param key The key whose associated value is to be retrieved from the key-value store.
     * @param stub The remote interface stub for communicating with the key-value store.
     * @throws RemoteException If an RMI error occurs during the remote method invocation.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    private static void doGet(String key, KeyValueRpc stub) throws RemoteException, InterruptedException {
        ClientLogger.info(String.format("Getting value by key=%s ", key));
        String value = stub.get(key).getValue();
        KeyValueResponse res = stub.get(key);
        if (res.isSuccess()) {
            ClientLogger.info(String.format("Value=%s for key=%s retrieved successfully.", value, key));
        } else {
            ClientLogger.error(res.getErrorMsg());
        }
    }

    /**
     * Performs a "put" operation on the remote key-value store.
     * This method inserts a new key-value pair or updates the value for an existing key.
     *
     * @param key The key to insert or update in the key-value store.
     * @param value The value associated with the key.
     * @param stub The remote interface stub for communicating with the key-value store.
     * @throws RemoteException If an RMI error occurs during the remote method invocation.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    private static void doPut(String key, String value, KeyValueRpc stub) throws RemoteException, InterruptedException {
        ClientLogger.info(String.format("Inserting pair <key=%s, value=%s>", key, value));
        KeyValueResponse res = stub.put(key, value);
        if(res.isSuccess()) {
            ClientLogger.info(String.format("Pair <key=%s, value=%s> added successfully.", key, value));
        } else {

        }
    }

    /**
     * Performs a "delete" operation on the remote key-value store.
     * This method removes the key-value pair associated with the specified key.
     *
     * @param key The key of the key-value pair to remove from the store.
     * @param stub The remote interface stub for communicating with the key-value store.
     * @throws RemoteException If an RMI error occurs during the remote method invocation.
     * @throws InterruptedException if there's an interruption during the remote method call.
     */
    private static void doDelete(String key, KeyValueRpc stub) throws RemoteException, InterruptedException {
        ClientLogger.info(String.format("Deleting pair with key=%s ", key));
        String value = stub.get(key).getValue();
        KeyValueResponse res = stub.delete(key);
        if(res.isSuccess()) {
            ClientLogger.info(String.format("Pair <key=%s, value=%s> deleted successfully.", key, value));
        } else {
            ClientLogger.error(res.getErrorMsg());
        }
    }
}
