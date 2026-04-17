package dao;

import config.DatabaseConnection;
import model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Cliente.
 * Implementa todas las operaciones CRUD contra la base de datos MySQL.
 * 
 * Se encarga de transformar datos entre la BD y objetos Cliente.
 * 
 * @author Anartz
 */
public class ClienteDAO implements IClienteDAO {

	/**
	 * Inserta un nuevo cliente en la base de datos.
	 * 
	 * @param c objeto {@link Cliente} con los datos del cliente a insertar
	 * @return {@code true} si la inserción fue correcta, {@code false} en caso contrario
	 * @throws SQLException si ocurre un error durante la operación SQL
	 */
	
    public boolean insertar(Cliente c) throws SQLException {

        String sql = "INSERT INTO cliente "
                   + "(Nif_Cli, Nombre, Apellido, Calle, Numero, Piso, Localidad, Provincia, Telefono, Email) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // PreparedStatement → evita SQL Injection y permite parámetros dinámicos
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {

            // Asignación de valores desde el objeto Cliente
            ps.setString(1,  c.getNif_Cli());
            ps.setString(2,  c.getNombre());
            ps.setString(3,  c.getApellido());
            ps.setString(4,  c.getCalle());
            ps.setInt(5,     c.getNumero());   // número es entero
            ps.setString(6,  c.getPiso());
            ps.setString(7,  c.getLocalidad());
            ps.setString(8,  c.getProvincia());
            ps.setString(9,  c.getTelefono());
            ps.setString(10, c.getEmail());    // puede ser null

            // Devuelve true si se ha insertado correctamente
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca un cliente en la base de datos mediante su NIF.
     * 
     * @param Nif_Cli NIF del cliente (clave primaria)
     * @return objeto {@link Cliente} si existe, {@code null} si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     */
    
    public Cliente buscarPorNif(String Nif_Cli) throws SQLException {

        String sql = "SELECT * FROM cliente WHERE Nif_Cli = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, Nif_Cli);

            try (ResultSet rs = ps.executeQuery()) {

                // Si encuentra resultado → convertir a objeto Cliente
                if (rs.next()) return mapear(rs);
            }
        }
        return null; // no encontrado
    }

    /**
     * Obtiene una lista con todos los clientes almacenados en la base de datos.
     * 
     * <p>Los resultados se devuelven ordenados por apellido y nombre.</p>
     * 
     * @return lista de objetos {@link Cliente}
     * @throws SQLException si ocurre un error en la consulta
     */
    
    public List<Cliente> listarTodos() throws SQLException {

        String sql = "SELECT * FROM cliente ORDER BY Apellido, Nombre";
        List<Cliente> lista = new ArrayList<>();

        // Statement porque no hay parámetros
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Recorrer resultados y añadir a la lista
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    /**
     * Actualiza los datos de un cliente existente.
     * 
     * <p>El cliente se identifica por su NIF.</p>
     * 
     * @param c objeto {@link Cliente} con los nuevos datos
     * @return {@code true} si se actualizó correctamente, {@code false} si no existe
     * @throws SQLException si ocurre un error durante la operación SQL
     */
    
    public boolean actualizar(Cliente c) throws SQLException {

        String sql = "UPDATE cliente SET Nombre=?, Apellido=?, Calle=?, Numero=?, Piso=?, "
                   + "Localidad=?, Provincia=?, Telefono=?, Email=? WHERE Nif_Cli=?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {

            // Nuevos valores
            ps.setString(1,  c.getNombre());
            ps.setString(2,  c.getApellido());
            ps.setString(3,  c.getCalle());
            ps.setInt(4,     c.getNumero());
            ps.setString(5,  c.getPiso());
            ps.setString(6,  c.getLocalidad());
            ps.setString(7,  c.getProvincia());
            ps.setString(8,  c.getTelefono());
            ps.setString(9,  c.getEmail());

            // Condición (qué cliente actualizar)
            ps.setString(10, c.getNif_Cli());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un cliente de la base de datos según su NIF.
     * 
     * @param Nif_Cli NIF del cliente a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existe
     * @throws SQLException si ocurre un error durante la operación SQL
     */
    
    public boolean eliminar(String Nif_Cli) throws SQLException {

        String sql = "DELETE FROM cliente WHERE Nif_Cli = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, Nif_Cli);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Cliente}.
     * 
     * <p>Este método centraliza la lógica de conversión entre los datos
     * de la base de datos y el modelo de la aplicación.</p>
     * 
     * @param rs resultado de la consulta SQL posicionado en una fila válida
     * @return objeto {@link Cliente} con los datos de la fila actual
     * @throws SQLException si ocurre un error al acceder a los datos
     */// -------------------------------------------------------
    // MÉTODO PRIVADO → Mapear ResultSet a objeto Cliente
    // -------------------------------------------------------
    
    private Cliente mapear(ResultSet rs) throws SQLException {

        // Convierte una fila de la BD en un objeto Cliente
        return new Cliente(
            rs.getString("Nif_Cli"),
            rs.getString("Nombre"),
            rs.getString("Apellido"),
            rs.getString("Calle"),
            rs.getInt("Numero"),
            rs.getString("Piso"),
            rs.getString("Localidad"),
            rs.getString("Provincia"),
            rs.getString("Telefono"),
            rs.getString("Email")
        );
    }
}