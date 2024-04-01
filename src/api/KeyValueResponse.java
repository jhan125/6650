package api;

import java.io.Serializable;

/**
 * Represents a response from key-value store operations.
 * This class encapsulates the result of an operation, indicating whether it was successful,
 * the operation performed, the value associated with the operation (if applicable), and
 * an error message in case the operation failed.
 */
public class KeyValueResponse implements Serializable {

    private String operation;
    private String value;
    private boolean success;
    private String errorMsg;

    /**
     * Default constructor for creating an empty response.
     */
    public KeyValueResponse() {
    }

    /**
     * Constructs a new KeyValueResponse with specified details.
     *
     * @param operation The operation performed (e.g., "GET", "PUT", "DELETE").
     * @param value The value associated with the operation, if applicable.
     * @param success Indicates whether the operation was successful.
     * @param errorMsg An error message if the operation failed, otherwise null.
     */
    public KeyValueResponse(String operation, String value, boolean success, String errorMsg) {
        this.operation = operation;
        this.value = value;
        this.success = success;
        this.errorMsg = errorMsg;
    }

    /**
     * Gets the operation performed.
     *
     * @return The operation name.
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the operation performed.
     *
     * @param operation The operation name.
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * Gets the value associated with the operation.
     *
     * @return The value, or null if not applicable.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value associated with the operation.
     *
     * @param value The value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Indicates whether the operation was successful.
     *
     * @return true if the operation was successful, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the operation.
     *
     * @param success true if the operation was successful, false otherwise.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the error message if the operation failed.
     *
     * @return The error message, or null if the operation was successful.
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * Sets the error message for a failed operation.
     *
     * @param errorMsg The error message.
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Returns a string representation of the KeyValueResponse.
     *
     * @return A string containing the details of the response.
     */
    @Override
    public String toString() {
        return String.format("KeyValueResponse {operation=%s, value=%s, success=%s, errorMsg=%s}",
                operation, value, success, errorMsg);
    }
}
