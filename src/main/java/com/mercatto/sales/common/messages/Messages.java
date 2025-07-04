package com.mercatto.sales.common.messages;

public class Messages {
    // error msj
    public static final String LOG_ERROR_DATA_INTEGRITY = "Data integrity error: [{}]";
    public static final String LOG_ERROR_TO_SAVE_ENTITY = "An unexpected error occurred while saving the registry: [{}]";
    public static final String LOG_ERROR_TO_UPDATE_ENTITY = "An unexpected error occurred while updating the registry: [{}]";
    public static final String LOG_ERROR_TO_DELETE_ENTITY = "An unexpected error occurred while deleting the registry: [{}]";
    public static final String LOG_ERROR_API = "Error in class: {}, method: {}. message: {}";
    public static final String TOKEN_ERROR = "Token de sesión no valido o expirado.";
    public static final String SESSION_ERROR = "Sesión no válida o expirada.";

    public static final String ERROR_ENTITY_SAVE = "Se produjo un error inesperado al guardar el registro.";
    public static final String ERROR_ENTITY_UPDATE = "Se produjo un error inesperado al actualizar el registro.";
    public static final String ERROR_ENTITY_DELETE = "Se produjo un error inesperado al eliminar el registro.";
    public static final String ERROR_FILE_DOWNLOAD = "Se produjo un error inesperado al descargar el archivo.";
    public static final String ENTITY_DELETE = "El recurso con id: [%s] fue eliminado";
    public static final String NOT_FOUND = "El recurso solicitado con id o nombre: [%s] no fue encontrado.";
    public static final String NOT_FOUND_BASIC = "El recurso solicitado no fue encontrado.";
    public static final String NOT_FOUND_FILE = "El archivo solicitado con id: [%s] no fue encontrado.";
    public static final String DATA_INTEGRITY = "Violación de la integridad de los datos.";

    public static final String ADDRESS = "Address";
    public static final String USER = "User";
    public static final String SUBSIDIARY = "Subsidiary";
    public static final String EMPLOYEE = "Employee";

    // anotaciones
    public static final String RFC_ANNOTATION = "El valor no es un RFC valida.";
    public static final String UUID_ANNOTATION = "El valor no es un UUID valido.";
    public static final String ENUM_ANNOTATION = "El valor no es válido para la enumeración proporcionada.";

    public static final String CITY_CREATE = "Se han creado las ciudades.";
    public static final String COUNTRY_CREATE = "Los países han sido creados.";
    public static final String USER_ALREADY_REGISTERED = "El nombre de usuario ya fue registrado.";
    public static final String PROFILES_CREATE = "Se han creado los perfiles.";

    private Messages() {
        throw new IllegalStateException("Utility class");
    }
}
