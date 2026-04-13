
package dao;

import config.DatabaseConnection;
import model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Proveedor.
 */
public class ProveedorDAO implements IProveedorDAO {
    // ... tu implementación actual



    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    public boolean insertar(Proveedor p) throws SQLException {
        String sql = "INSERT INTO proveedor (Nif_Prove, Nombre, Localidad, Telefono, Email) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNif_Prove());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getLocalidad());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getEmail());  // null si no se proporcionó
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // READ - Buscar por NIF
    // -------------------------------------------------------
    public Proveedor buscarPorNif(String nif) throws SQLException {
        String sql = "SELECT * FROM proveedor WHERE Nif_Prove = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nif);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // -------------------------------------------------------
    // READ - Listado completo
    // -------------------------------------------------------
    public List<Proveedor> listarTodos() throws SQLException {
        String sql = "SELECT * FROM proveedor ORDER BY Nombre";
        List<Proveedor> lista = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    public boolean actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE proveedor SET Nombre=?, Localidad=?,Telefono=?, Email=? WHERE Nif_Prove=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getLocalidad());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getNif_Prove());
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    public boolean eliminar(String nif) throws SQLException {
        String sql = "DELETE FROM proveedor WHERE Nif_Prove = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nif);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // Mapeo
    // -------------------------------------------------------
    private Proveedor mapear(ResultSet rs) throws SQLException {
        return new Proveedor(
            rs.getString("Nif_Prove"),
            rs.getString("nombre"),
            rs.getString("Localidad"),
            rs.getString("Telefono"),
            rs.getString("Email")
        );
    }
}
