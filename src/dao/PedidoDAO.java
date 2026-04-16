/* Alvaro */

package dao;

import config.DatabaseConnection;
import model.Pedido;
import model.LineaPedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Clase que implementa las operaciones de acceso a datos para los pedidos
public class PedidoDAO implements IPedidoDAO {

    // -------------------------------------------------------
    // CREATE - Inserta pedido y sus lineas usando una transaccion
    // -------------------------------------------------------
    @Override
    public int insertar(Pedido pedido) throws SQLException {

        // Obtiene la conexion activa a la base de datos
        Connection conn = DatabaseConnection.getConnection();

        // Desactiva el autocommit para gestionar la transaccion manualmente
        conn.setAutoCommit(false);

        try {
            // Define la sentencia SQL para insertar la cabecera del pedido
            String sqlPedido = "INSERT INTO pedido (Fecha_Ped, Fecha_Ent, Precio_Total_Ped, Nif_Cli) "
                    + "VALUES (?, ?, ?, ?)";

            // Almacena el numero de pedido generado automaticamente por la BD
            int numeroPedido;

            // Prepara la sentencia solicitando que se devuelvan las claves generadas
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

                // Asigna los valores del pedido a los parametros de la sentencia
                ps.setDate(1, Date.valueOf(pedido.getFecha_ped()));
                ps.setDate(2, Date.valueOf(pedido.getFecha_ent()));
                ps.setFloat(3, pedido.getPrecio_Total_Ped());
                ps.setString(4, pedido.getNif_Cli());
                ps.executeUpdate();

                // Recupera el numero de pedido generado por la base de datos
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    numeroPedido = rs.getInt(1);
                }
            }

            // Define la sentencia SQL para insertar cada linea del pedido
            String sqlLinea = "INSERT INTO contiene (Num_Pedido, Cod_Pro, Cantidad_Pro, Precio_Total) "
                    + "VALUES (?, ?, ?, ?)";

            // Prepara la insercion en batch de todas las lineas del pedido
            try (PreparedStatement ps = conn.prepareStatement(sqlLinea)) {
                for (LineaPedido linea : pedido.getLineas()) {

                    // Asigna los valores de cada linea al batch
                    ps.setInt(1, numeroPedido);
                    ps.setString(2, linea.getCod_Pro());
                    ps.setInt(3, linea.getCantidad_Pro());
                    ps.setFloat(4, linea.getPrecio_Total());
                    ps.addBatch();
                }
                // Ejecuta todas las inserciones de lineas de una sola vez
                ps.executeBatch();
            }

            // Confirma la transaccion si todo fue correcto
            conn.commit();
            return numeroPedido;

        } catch (SQLException e) {
            // Deshace todos los cambios si ocurre algun error durante la insercion
            conn.rollback();
            throw e;
        } finally {
            // Restaura el autocommit a su estado original
            conn.setAutoCommit(true);
        }
    }

    // -------------------------------------------------------
    // READ - Busca un pedido concreto por su numero
    // -------------------------------------------------------
    @Override
    public Pedido buscarPorNumero(int numeroPedido) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE Num_Pedido = ?";
        Pedido pedido = null;

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);

            try (ResultSet rs = ps.executeQuery()) {
                // Comprueba si existe un resultado y lo convierte en objeto Pedido
                if (rs.next()) {
                    pedido = mapearPedido(rs);

                    // Carga las lineas de productos asociadas a este pedido
                    pedido.setLineas(buscarLineas(numeroPedido));
                }
            }
        }
        return pedido;
    }

    // -------------------------------------------------------
    // READ - Devuelve todos los pedidos ordenados por fecha descendente
    // -------------------------------------------------------
    @Override
    public List<Pedido> listarTodos() throws SQLException {
        String sql = "SELECT * FROM pedido ORDER BY Fecha_Ped DESC";
        List<Pedido> lista = new ArrayList<>();

        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Recorre cada fila del resultado y la convierte en un objeto Pedido
            while (rs.next()) {
                Pedido p = mapearPedido(rs);

                // Carga las lineas asociadas a cada pedido
                p.setLineas(buscarLineas(p.getNum_Pedido()));
                lista.add(p);
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ - Devuelve todos los pedidos de un cliente concreto
    // -------------------------------------------------------
    @Override
    public List<Pedido> listarPorCliente(String nifCliente) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE Nif_Cli = ? ORDER BY Fecha_Ped DESC";
        List<Pedido> lista = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nifCliente);

            try (ResultSet rs = ps.executeQuery()) {
                // Recorre los pedidos del cliente y los convierte en objetos Pedido
                while (rs.next()) {
                    Pedido p = mapearPedido(rs);

                    // Carga las lineas de cada pedido del cliente
                    p.setLineas(buscarLineas(p.getNum_Pedido()));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE - Actualiza la cabecera y las lineas de un pedido existente
    // -------------------------------------------------------
    @Override
    public boolean actualizar(Pedido pedido) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Desactiva el autocommit para gestionar la transaccion manualmente
        conn.setAutoCommit(false);

        try {
            // Define la sentencia SQL para actualizar los datos de la cabecera
            String sqlPedido = "UPDATE pedido SET Fecha_Ped=?, Fecha_ent=?, "
                    + "Precio_Total_Ped=?, Nif_Cli=? WHERE Num_Pedido=?";

            // Ejecuta la actualizacion de la cabecera del pedido
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido)) {
                ps.setDate(1, Date.valueOf(pedido.getFecha_ped()));
                ps.setDate(2, Date.valueOf(pedido.getFecha_ent()));
                ps.setFloat(3, pedido.getPrecio_Total_Ped());
                ps.setString(4, pedido.getNif_Cli());
                ps.setInt(5, pedido.getNum_Pedido());
                ps.executeUpdate();
            }

            // Elimina las lineas antiguas del pedido antes de insertar las nuevas
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM contiene WHERE Num_Pedido = ?")) {
                ps.setInt(1, pedido.getNum_Pedido());
                ps.executeUpdate();
            }

            // Define la sentencia SQL para insertar las lineas actualizadas
            String sqlLinea = "INSERT INTO contiene (Num_Pedido, Cod_Pro, Cantidad_Pro, Precio_Total) "
                    + "VALUES (?, ?, ?, ?)";

            // Inserta en batch todas las nuevas lineas del pedido actualizado
            try (PreparedStatement ps = conn.prepareStatement(sqlLinea)) {
                for (LineaPedido linea : pedido.getLineas()) {
                    ps.setInt(1, pedido.getNum_Pedido());
                    ps.setString(2, linea.getCod_Pro());
                    ps.setInt(3, linea.getCantidad_Pro());
                    ps.setFloat(4, linea.getPrecio_Total());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Confirma la transaccion si todo fue correcto
            conn.commit();
            return true;

        } catch (SQLException e) {
            // Deshace todos los cambios si ocurre algun error durante la actualizacion
            conn.rollback();
            throw e;
        } finally {
            // Restaura el autocommit a su estado original
            conn.setAutoCommit(true);
        }
    }

    // -------------------------------------------------------
    // DELETE - Elimina un pedido de la base de datos por su numero
    // -------------------------------------------------------
    @Override
    public boolean eliminar(int numeroPedido) throws SQLException {
        String sql = "DELETE FROM pedido WHERE Num_Pedido = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);

            // Devuelve true si se elimino al menos una fila
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // Auxiliar: obtiene todas las lineas de un pedido concreto
    // -------------------------------------------------------
    @Override
    public List<LineaPedido> buscarLineas(int numeroPedido) throws SQLException {
        String sql = "SELECT * FROM contiene WHERE Num_Pedido = ?";
        List<LineaPedido> lineas = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);

            try (ResultSet rs = ps.executeQuery()) {
                // Recorre cada fila y construye un objeto LineaPedido por cada una
                while (rs.next()) {
                    lineas.add(new LineaPedido(
                            rs.getInt("Num_Pedido"),
                            rs.getString("Cod_Pro"),
                            rs.getInt("Cantidad_Pro"),
                            rs.getFloat("Precio_Total")
                    ));
                }
            }
        }
        return lineas;
    }

    // -------------------------------------------------------
    // Convierte una fila del ResultSet en un objeto Pedido
    // -------------------------------------------------------
    @Override
    public Pedido mapearPedido(ResultSet rs) throws SQLException {
        return new Pedido(
                rs.getInt("Num_Pedido"),
                rs.getDate("Fecha_Ped").toLocalDate(),
                rs.getDate("Fecha_Ent").toLocalDate(),
                rs.getFloat("Precio_Total_Ped"),
                rs.getString("Nif_Cli")
        );
    }

    // -------------------------------------------------------
    // PROCEDIMIENTO ALMACENADO: llama a MODIFICAR_PEDIDO en la base de datos
    // -------------------------------------------------------
    @Override
    public boolean modificarPedidoProcedimiento(
            int numPedido,
            String accion,
            String listaProductos,
            String listaCantidades,
            LocalDate fechaPed,
            LocalDate fechaEnt
    ) throws SQLException {

        // Define la llamada al procedimiento almacenado con seis parametros
        String sql = "{ CALL MODIFICAR_PEDIDO(?, ?, ?, ?, ?, ?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Asigna cada parametro al CallableStatement en el orden correcto
            cs.setInt(1, numPedido);
            cs.setString(2, accion);
            cs.setString(3, listaProductos);
            cs.setString(4, listaCantidades);
            cs.setDate(5, Date.valueOf(fechaPed));
            cs.setDate(6, Date.valueOf(fechaEnt));

            // Ejecuta el procedimiento almacenado
            cs.execute();
            return true;
        }
    }

    // -------------------------------------------------------
    // Genera una cadena con los codigos de producto separados por comas
    // -------------------------------------------------------
    public String generarListaProductos(Pedido pedido) {
        StringBuilder sb = new StringBuilder();

        // Recorre las lineas del pedido y concatena cada codigo de producto
        for (LineaPedido lp : pedido.getLineas()) {
            sb.append(lp.getCod_Pro()).append(",");
        }

        // Elimina la coma final sobrante antes de devolver el resultado
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    // -------------------------------------------------------
    // Genera una cadena con las cantidades de cada linea separadas por comas
    // -------------------------------------------------------
    public String generarListaCantidades(Pedido pedido) {
        StringBuilder sb = new StringBuilder();

        // Recorre las lineas del pedido y concatena cada cantidad
        for (LineaPedido lp : pedido.getLineas()) {
            sb.append(lp.getCantidad_Pro()).append(",");
        }

        // Elimina la coma final sobrante antes de devolver el resultado
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}

/* Alvaro */
