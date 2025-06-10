package com.mercatto.sales.common.messages;

public class Messages {
    public static final String NOT_FOUND_AFTER_SAVING = "The entity with name: [%s] was not found after saving";
    public static final String DATA_INTEGRITY_ERROR = "Data integrity error: [%]";
    public static final String ERROR_TO_SAVE_ENTITY = "An unexpected error occurred while saving the registry: [%s]";
    public static final String NOT_FOUND = "The entity with id: [%s] was not found";
    public static final String DELETE_ENTITY = "The entity with id [%s] was deleted";

    public static final String ADDRESS = "Address";
    public static final String USER = "User";
    public static final String SUBSIDIARY = "Subsidiary";
    public static final String EMPLOYEE = "Employee";

    private Messages() {
        throw new IllegalStateException("Utility class");
    }
}
