package dao;

import config.DatabaseConnection;
import model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar la entidad Proveedor en la base de datos.
 * Permite realizar operaciones CRUD y consultas específicas.
 * 
 * @author Anartz
 */
public class ProveedorDAO implements IProveedorDAO {
	
	/**
	 * Inserta un nuevo proveedor en la base de datos.
	 * 
	 * @param p objeto {@link Proveedor} con los datos a insertar
	 * @return {@code true} si la inserción fue correcta, {@code false} en caso contrario
	 * @throws SQLException si ocurre un error durante la operación SQL
	 */
    public boolean insertar(Proveedor p) throws SQLException {
        String sql = "INSERT INTO proveedor (Nif_Prove, Nombre, Localidad, Telefono, Email) VALUES (?, ?, ?, ?, ?)";

        // PreparedStatement evita SQL Injection y permite insertar parámetros dinámicos
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {

            // Asignamos los valores del objeto Proveedor a la consulta
            ps.setString(1, p.getNif_Prove());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getLocalidad());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getEmail());

            // executeUpdate devuelve nº de filas afectadas (>0 si inserta correctamente)
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca un proveedor en la base de datos a partir de su NIF.
     * 
     * @param nif NIF del proveedor (clave primaria)
     * @return objeto {@link Proveedor} si existe, {@code null} si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     */
    public Proveedor buscarPorNif(String nif) throws SQLException {
        String sql = "SELECT * FROM proveedor WHERE Nif_Prove = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nif);

            try (ResultSet rs = ps.executeQuery()) {

                // Si encuentra resultado, lo convierte a objeto Proveedor
                if (rs.next()) return mapear(rs);
            }
        }
        return null; // Si no existe
    }

    /**
     * Obtiene una lista con todos los proveedores de la base de datos.
     * 
     * <p>Los resultados se devuelven ordenados por nombre.</p>
     * 
     * @return lista de objetos {@link Proveedor}
     * @throws SQLException si ocurre un error en la consulta
     */
    
    public List<Proveedor> listarTodos() throws SQLException {
        String sql = "SELECT * FROM proveedor ORDER BY Nombre";
        List<Proveedor> lista = new ArrayList<>();

        // Statement simple porque no hay parámetros
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Recorremos todos los resultados y los añadimos a la lista
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Actualiza los datos de un proveedor existente.
     * 
     * <p>El proveedor se identifica por su NIF.</p>
     * 
     * @param p objeto {@link Proveedor} con los nuevos datos
     * @return {@code true} si se actualizó correctamente, {@code false} si no existe
     * @throws SQLException si ocurre un error durante la operación SQL
     */
    public boolean actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE proveedor SET Nombre=?, Localidad=?, Telefono=?, Email=? WHERE Nif_Prove=?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {

            // Nuevos valores
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getLocalidad());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEmail());

            // Condición (qué proveedor actualizar)
            ps.setString(5, p.getNif_Prove());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un proveedor de la base de datos según su NIF.
     * 
     * @param nif NIF del proveedor a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existe
     * @throws SQLException si ocurre un error durante la operación SQL
     */
    
    public boolean eliminar(String nif) throws SQLException {
        String sql = "DELETE FROM proveedor WHERE Nif_Prove = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nif);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Obtiene el nombre del producto más vendido de un proveedor.
     * 
     * <p>Este método ejecuta una función almacenada en la base de datos
     * (<code>PRODUCTO_MAS_VENDIDO_PROVEEDOR</code>).</p>
     * 
     * @param nif NIF del proveedor
     * @return nombre del producto más vendido o cadena vacía si no hay resultados
     * @throws SQLException si ocurre un error en la consulta
     */
    
    public String ProductoMasVendido(String nif) throws SQLException {
        String sql = "SELECT PRODUCTO_MAS_VENDIDO_PROVEEDOR(?)";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nif);

            ResultSet rs = ps.executeQuery();

            // Devuelve el resultado de la función de la BD
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return "";
            }
        }
    }

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Proveedor}.
     * 
     * <p>Este método centraliza la lógica de mapeo entre la base de datos
     * y el modelo de la aplicación.</p>
     * 
     * @param rs resultado de la consulta SQL
     * @return objeto {@link Proveedor} con los datos de la fila actual
     * @throws SQLException si ocurre un error al acceder a los datos
     */
    
    private Proveedor mapear(ResultSet rs) throws SQLException {
        return new Proveedor(
            rs.getString("Nif_Prove"),
            rs.getString("Nombre"),
            rs.getString("Localidad"),
            rs.getString("Telefono"),
            rs.getString("Email")
        );
    }
}