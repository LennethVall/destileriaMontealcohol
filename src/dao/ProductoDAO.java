package dao;

import config.DatabaseConnection;
import model.Producto;
import model.Tipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements IProductoDAO {

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    @Override
    public boolean insertar(Producto p) throws SQLException {
        if (!Producto.codigoValido(p.getCod_Pro())) {
            throw new IllegalArgumentException(
                "Código de producto inválido. Formato: 1 letra + 4 dígitos (ej: D0001)");
        }

        String sql = "INSERT INTO producto (Cod_Pro, Nom_Pro, Precio_Pro, Stock, Tipo, Nif_Prove) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getCod_Pro());
            ps.setString(2, p.getNom_Pro());
            ps.setFloat(3, p.getPrecio_Pro());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getTipo().getLabel());
            ps.setString(6, p.getNif_Prove());
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // READ - Buscar por código
    // -------------------------------------------------------
    @Override
    public Producto buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM producto WHERE Cod_Pro = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // -------------------------------------------------------
    // READ - Listado completo
    // -------------------------------------------------------
    @Override
    public List<Producto> listarTodos() throws SQLException {
        String sql = "SELECT * FROM producto ORDER BY Tipo, Cod_Pro";
        List<Producto> lista = new ArrayList<>();

        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ - Listar por tipo
    // -------------------------------------------------------
    @Override
    public List<Producto> listarPorTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM producto WHERE Tipo = ? ORDER BY Cod_Pro";
        List<Producto> lista = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    @Override
    public boolean actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET Precio_Pro=?, Stock=?, Tipo=?, Nif_Prove=? WHERE Cod_Pro=?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setFloat(1, p.getPrecio_Pro());
            ps.setInt(2, p.getStock());
            ps.setString(3, p.getTipo().getLabel());
            ps.setString(4, p.getNif_Prove());
            ps.setString(5, p.getCod_Pro());
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    @Override
    public boolean eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM producto WHERE Cod_Pro = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // DELETE con procedimiento
    // -------------------------------------------------------
    @Override
    public boolean eliminarProductoProcedimiento(String codProducto) throws SQLException {
        String sql = "{ CALL ELIMINAR_PRODUCTO(?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, codProducto);
            cs.execute();
            return true;
        }
    }

    // -------------------------------------------------------
    // AÑADIR STOCK con procedimiento (AÑADIR_PRODUCTO)
    // -------------------------------------------------------
    @Override
    public String añadirProductoProcedimiento(String cod, int cantidad) {
        String mensaje = "";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL AÑADIR_PRODUCTO(?, ?)}")) {

            cs.setString(1, cod);
            cs.setInt(2, cantidad);

            boolean tieneResultado = cs.execute();

            if (tieneResultado) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        mensaje = rs.getString(1);
                    }
                }
            }

        } catch (SQLException e) {
            mensaje = "ERROR: No se pudo ejecutar el procedimiento.";
        }

        return mensaje;
    }

    // -------------------------------------------------------
    // Mapeo
    // -------------------------------------------------------
    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getString("Cod_Pro"),
            rs.getString("Nom_Pro"),
            rs.getFloat("Precio_Pro"),
            rs.getInt("Stock"),
            Tipo.fromLabel(rs.getString("Tipo")),
            rs.getString("Nif_Prove")
        );
    }
}
