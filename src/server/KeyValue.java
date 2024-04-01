package server;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a thread-safe singleton pattern to store and manage key-value pairs. This class
 * utilizes {@link ReentrantLock} for key-value store storage to ensure thread-safe operations.
 * It provides methods to add, delete, and retrieve key-value pairs.
 * Another option is to use a {@link ConcurrentHashMap}.
 */
public class KeyValue {

    // Static variable reference of single_instance of type KeyValue
    private static KeyValue singletonInstance = null;

//    private final ConcurrentHashMap<String, String> keyValStore;

    private final HashMap<String, String> keyValStore;
    private final ReentrantLock mutex;

    /**
     * Private constructor to prevent instantiation from outside this class.
     * Initializes the key-value store and the mutex lock.
     */
    private KeyValue() {
//        keyValStore = new ConcurrentHashMap<>();
        this.keyValStore = new HashMap<>();
        this.mutex = new ReentrantLock();
        this.singletonInstance = null;
    }

    /**
     * Provides a global point of access to the singleton instance of the {@link KeyValue} class.
     * If the instance does not exist, it is created. This method ensures that only one instance
     * of the class is created and returned.
     *
     * @return The singleton instance of the {@link KeyValue} class.
     */
    public static KeyValue getInstance() {
        if (singletonInstance == null) {
            synchronized (KeyValue.class) {
                if (singletonInstance == null) { // Double-check locking pattern
                    singletonInstance = new KeyValue();
                }
            }
        }
        return singletonInstance;
    }

    /**
     * Inserts or updates a key-value pair in the store. If the key already exists, its value is updated.
     * This method is thread-safe.
     *
     * @param key   The key to insert or update.
     * @param value The value associated with the key.
     */
    public void put(String key, String value) {
        try {
            mutex.lock();
            keyValStore.put(key, value);
            ServerLogger.info(String.format("Added/Updated pair <key=%s, value=%s> in the database.", key, value));
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Deletes a key-value pair from the store if the key exists.
     * This method is thread-safe.
     *
     * @param key The key of the pair to be deleted.
     * @return True if the deletion was successful, false if the key was not found.
     */
    public boolean delete(String key) {
        try {
            mutex.lock();
            if (keyValStore.containsKey(key)) {
                keyValStore.remove(key);
                ServerLogger.info(String.format("Deleted key=%s from the database.", key));
                return true;
            } else {
                ServerLogger.error(String.format("Key=%s is not found in the database.", key));
                return false;
            }
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Retrieves the value associated with a given key from the store.
     * This method is thread-safe.
     *
     * @param key The key whose value is to be retrieved.
     * @return The value associated with the key, or null if the key is not found.
     */
    public String get(String key) {
        try {
            mutex.lock();
            if (keyValStore.containsKey(key)) {
                String value = keyValStore.get(key);
                ServerLogger.info(String.format("Retrieved value=%s for key=%s in the database", value, key));
                return value;
            } else {
                ServerLogger.error(String.format("Key=%s is not found in the database.", key));
                return null;
            }
        } finally {
            mutex.unlock();
        }
    }
}

