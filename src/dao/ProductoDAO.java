
package dao;

import config.DatabaseConnection;
import model.Producto;
import model.Tipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Producto.
 * El código tiene formato: 1 letra + 4 dígitos.
 */
public class ProductoDAO implements IProductoDAO{

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    public boolean insertar(Producto p) throws SQLException {
        if (!Producto.codigoValido(p.getCod_Pro())) {
            throw new IllegalArgumentException(
                "Código de producto inválido. Formato: 1 letra + 4 dígitos (ej: D0001)");
        }
        String sql = "INSERT INTO producto (Cod_Pro, Nom_Pro, Precio_Pro, Stock, Tipo, Nif_Prove) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
        	ps.setString(1, p.getCod_Pro());
        	ps.setString(2, p.getNom_Pro());   // ← FALTABA
        	ps.setDouble(3, p.getPrecio_Pro());
        	ps.setInt(4, p.getStock());
        	ps.setString(5, p.getTipo().getLabel());
        	ps.setString(6, p.getNif_Prove());

            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // READ - Buscar por código (PK)
    // -------------------------------------------------------
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
    public List<Producto> listarPorTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM producto WHERE Tipo = ? ORDER BY cod_Pro";
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
    public boolean actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET Precio_Pro=?, Stock=?, Tipo=?, Nif_Prove=? WHERE Cod_Pro=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, p.getPrecio_Pro());
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
    public boolean eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM producto WHERE Cod_Pro = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }
    @Override
    public boolean eliminarProductoProcedimiento(String codProducto) throws SQLException {

        String sql = "{ CALL ELIMINAR_PRODUCTO(?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, codProducto);
            cs.execute();
            return true;
        }
        
        @Override
        public boolean crearProductoConStockInicial(
                String cod,
                String nombre,
                double precio,
                String tipo,
                String nifProveedor
        ) throws SQLException {

            String sql = "{ CALL CREAR_PRODUCTO_CON_STOCK_INICIAL(?, ?, ?, ?, ?) }";

            try (Connection conn = DatabaseConnection.getConnection();
                 CallableStatement cs = conn.prepareCall(sql)) {

                cs.setString(1, cod);
                cs.setString(2, nombre);
                cs.setDouble(3, precio);
                cs.setString(4, tipo);
                cs.setString(5, nifProveedor);

                cs.execute();
                return true;
            }
        }
    
    }

    // -------------------------------------------------------
    // Mapeo
    // -------------------------------------------------------
    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getString("Cod_Pro"),
            rs.getString("Nom_Pro"),      
            rs.getDouble("Precio_Pro"),   
            rs.getInt("Stock"),
            Tipo.fromLabel(rs.getString("Tipo")),  
            rs.getString("Nif_Prove")
        );
    }
    
    

}
