package dao;

import config.DatabaseConnection;
import model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Cliente.
 * Implementa todas las operaciones CRUD contra MySQL.
 */
public class ClienteDAO implements IClienteDAO{

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
	public boolean insertar(Cliente c) throws SQLException {
	    String sql = "INSERT INTO cliente "
	               + "(Nif_Cli, Nombre, Apellido, Calle, Numero, Piso, Localidad, Provincia, Telefono, Email) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
	        ps.setString(1,  c.getNif_Cli());
	        ps.setString(2,  c.getNombre());
	        ps.setString(3,  c.getApellido());
	        ps.setString(4,  c.getCalle());
	        ps.setInt(5,     c.getNumero());      // ← CORREGIDO
	        ps.setString(6,  c.getPiso());
	        ps.setString(7,  c.getLocalidad());
	        ps.setString(8,  c.getProvincia());
	        ps.setString(9,  c.getTelefono());
	        ps.setString(10, c.getEmail());       // null si no se proporcionó

	        return ps.executeUpdate() > 0;
	    }
	}


    // -------------------------------------------------------
    // READ - Buscar por NIF (PK)
    // -------------------------------------------------------
    public Cliente buscarPorNif(String Nif_Cli) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE Nif_Cli = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, Nif_Cli);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // -------------------------------------------------------
    // READ - Listado completo
    // -------------------------------------------------------
    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM cliente ORDER BY Apellido, Nombre";
        List<Cliente> lista = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET Nombre=?, Apellido=?, Calle=?, Numero=?, Piso=?, "
                   + "Localidad=?, Provincia=?, Telefono=?, Email=? WHERE Nif_Cli=?";
        
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1,  c.getNombre());
            ps.setString(2,  c.getApellido());
            ps.setString(3,  c.getCalle());
            ps.setInt(4,     c.getNumero());     // ← CORREGIDO
            ps.setString(5,  c.getPiso());
            ps.setString(6,  c.getLocalidad());
            ps.setString(7,  c.getProvincia());
            ps.setString(8,  c.getTelefono());
            ps.setString(9,  c.getEmail());
            ps.setString(10, c.getNif_Cli());
            
            return ps.executeUpdate() > 0;
        }
    }


    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    public boolean eliminar(String Nif_Cli) throws SQLException {
        String sql = "DELETE FROM cliente WHERE Nif_Cli = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, Nif_Cli);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // Mapeo ResultSet -> Cliente
    // -------------------------------------------------------
    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getString("Nif_Cli"),
            rs.getString("Nombre"),
            rs.getString("Apellido"),
            rs.getString("Calle"),
            rs.getInt("Numero"),        // ← CORREGIDO
            rs.getString("Piso"),
            rs.getString("Localidad"),
            rs.getString("Provincia"),
            rs.getString("Telefono"),
            rs.getString("Email")
        );
    }

}