package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos.
 * Implementa el patrón Singleton para reutilizar una única conexión.
 * 
 * Evita crear múltiples conexiones innecesarias, mejorando el rendimiento.
 * 
 * @author Anartz
 */
public class DatabaseConnection {

    /** -------------------------------------------------------
    *  DATOS DE CONEXIÓN
    *   -------------------------------------------------------*/
    private static final String URL = "jdbc:mysql://localhost:3306/montealcohol"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASSWORD = "abcd";

    /** Única instancia de conexión (Singleton)*/
    private static Connection connection;

    /** Constructor privado → evita crear objetos desde fuera*/
    private DatabaseConnection() {}

    /**
     * Obtiene la conexión a la base de datos.
     * 
     * <p>Si la conexión no existe o está cerrada, se crea una nueva.
     * En caso contrario, devuelve la conexión ya existente.</p>
     * 
     * <p>Este método garantiza el comportamiento Singleton.</p>
     * 
     * @return objeto {@link Connection} activo
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    
    public static Connection getConnection() throws SQLException {

        // Si no existe conexión o está cerrada → crear nueva
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Crear conexión con la BD
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

            } catch (ClassNotFoundException e) {
                // Error si no encuentra el driver
                throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
            }
        }

        // Devuelve siempre la misma conexión
        return connection;
    }

    /**
     * Cierra la conexión con la base de datos.
     * 
     * <p>Solo se ejecuta si la conexión existe y está abierta.</p>
     * 
     * <p>En caso de error, se muestra un mensaje por consola
     * sin interrumpir la ejecución de la aplicación.</p>
     */
    
    public static void close() {
        try {
            // Solo cerrar si existe y está abierta
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Mostrar error por consola (no rompe la app)
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}