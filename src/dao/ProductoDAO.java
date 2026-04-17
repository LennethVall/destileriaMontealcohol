package dao;

import config.DatabaseConnection;
import model.Producto;
import model.Tipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la gestión de productos en la base de datos.
 * Proporciona operaciones CRUD completas, así como métodos adicionales que
 * utilizan procedimientos almacenados para eliminar productos o modificar stock.
 *
 * Cada método establece su propia conexión mediante {@link DatabaseConnection}
 * y utiliza consultas preparadas para garantizar seguridad y eficiencia.
 *
 * @author Ines
 * @version 1.0
 */
public class ProductoDAO implements IProductoDAO {


    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
 
    /**
     * Inserta un nuevo producto en la base de datos.
     * Valida previamente el formato del código (1 letra + 4 dígitos).
     *
     * @param p Producto a insertar.
     * @return true si la inserción fue exitosa.
     * @throws SQLException Si ocurre un error durante la operación.
     * @throws IllegalArgumentException Si el código del producto no es válido.
     */
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
    /**
     * Busca un producto por su código.
     *
     * @param codigo Código del producto.
     * @return Producto encontrado o null si no existe.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
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
    /**
     * Obtiene un listado completo de productos ordenados por tipo y código.
     *
     * @return Lista de productos.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
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
    /**
     * Obtiene todos los productos pertenecientes a un tipo específico.
     *
     * @param tipo Tipo de producto (según etiqueta en la BD).
     * @return Lista de productos filtrados por tipo.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
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
    /**
     * Actualiza los datos de un producto existente.
     *
     * @param p Producto con los datos actualizados.
     * @return true si la actualización fue exitosa.
     * @throws SQLException Si ocurre un error durante la operación.
     */
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
    /**
     * Elimina un producto por su código.
     *
     * @param codigo Código del producto a eliminar.
     * @return true si la eliminación fue exitosa.
     * @throws SQLException Si ocurre un error durante la operación.
     */
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
    /**
     * Elimina un producto utilizando el procedimiento almacenado
     * ELIMINAR_PRODUCTO.
     *
     * @param codProducto Código del producto.
     * @return true si el procedimiento se ejecutó correctamente.
     * @throws SQLException Si ocurre un error durante la ejecución.
     */
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
    /**
     * Añade stock a un producto utilizando el procedimiento almacenado
     * AÑADIR_PRODUCTO. El procedimiento devuelve un mensaje que se retorna
     * al usuario.
     *
     * @param cod      Código del producto.
     * @param cantidad Cantidad a añadir.
     * @return Mensaje devuelto por el procedimiento.
     */
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
    /**
     * Resta una cantidad de stock al producto indicado.
     *
     * @param cod      Código del producto.
     * @param cantidad Cantidad a restar.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    public void restarStock(String cod, int cantidad) throws SQLException {

        String sql = "UPDATE producto SET Stock = Stock - ? WHERE Cod_Pro = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setString(2, cod);
            ps.executeUpdate();
        }
    }

    /**
     * Suma una cantidad de stock al producto indicado.
     *
     * @param cod      Código del producto.
     * @param cantidad Cantidad a sumar.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    public void sumarStock(String cod, int cantidad) throws SQLException {

        String sql = "UPDATE producto SET Stock = Stock + ? WHERE Cod_Pro = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setString(2, cod);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------
    // Mapeo
    // -------------------------------------------------------
    /**
     * Convierte una fila del ResultSet en un objeto {@link Producto}.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return Producto construido a partir de los datos del ResultSet.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
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