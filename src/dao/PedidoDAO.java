package dao;

import config.DatabaseConnection;
import model.Pedido;
import model.LineaPedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del patrón DAO para la entidad {@link Pedido}.
 * <p>
 * Proporciona todas las operaciones de acceso a datos sobre la tabla {@code pedido}
 * y la tabla de líneas {@code contiene}, incluyendo transacciones, consultas,
 * inserciones en batch y llamadas a procedimientos almacenados.
 * </p>
 *
 * @author Alvaro
 * @version 1.0
 * @see IPedidoDAO
 */
public class PedidoDAO implements IPedidoDAO {

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    /**
     * Inserta un nuevo pedido junto con todas sus líneas en la base de datos,
     * utilizando una transacción manual para garantizar la atomicidad.
     * <p>
     * Primero inserta la cabecera del pedido y recupera el número generado;
     * a continuación inserta todas las líneas en batch dentro de la misma transacción.
     * Si ocurre cualquier error, se realiza un {@code rollback} completo.
     * </p>
     *
     * @param pedido Objeto {@link Pedido} con los datos a insertar, incluidas sus líneas.
     * @return Número de pedido generado automáticamente por la base de datos.
     * @throws SQLException Si ocurre un error durante la inserción o la transacción.
     */
    @Override
    public int insertar(Pedido pedido) throws SQLException {

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String sqlPedido = "INSERT INTO pedido (Fecha_Ped, Fecha_Ent, Precio_Total_Ped, Nif_Cli) "
                    + "VALUES (?, ?, ?, ?)";

            int numeroPedido;

            try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(pedido.getFecha_ped()));
                ps.setDate(2, Date.valueOf(pedido.getFecha_ent()));
                ps.setFloat(3, pedido.getPrecio_Total_Ped());
                ps.setString(4, pedido.getNif_Cli());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    numeroPedido = rs.getInt(1);
                }
            }

            String sqlLinea = "INSERT INTO contiene (Num_Pedido, Cod_Pro, Cantidad_Pro, Precio_Total) "
                    + "VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sqlLinea)) {
                for (LineaPedido linea : pedido.getLineas()) {
                    ps.setInt(1, numeroPedido);
                    ps.setString(2, linea.getCod_Pro());
                    ps.setInt(3, linea.getCantidad_Pro());
                    ps.setFloat(4, linea.getPrecio_Total());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return numeroPedido;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // -------------------------------------------------------
    // READ - Por número
    // -------------------------------------------------------

    /**
     * Busca y devuelve un pedido concreto a partir de su número identificador,
     * cargando también todas sus líneas de producto asociadas.
     *
     * @param numeroPedido Número identificador del pedido a buscar.
     * @return El objeto {@link Pedido} encontrado con sus líneas, o {@code null} si no existe.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    @Override
    public Pedido buscarPorNumero(int numeroPedido) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE Num_Pedido = ?";
        Pedido pedido = null;

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedido = mapearPedido(rs);
                    pedido.setLineas(buscarLineas(numeroPedido));
                }
            }
        }
        return pedido;
    }

    // -------------------------------------------------------
    // READ - Todos los pedidos
    // -------------------------------------------------------

    /**
     * Devuelve todos los pedidos existentes en la base de datos ordenados
     * por fecha de pedido de forma descendente, con sus líneas cargadas.
     *
     * @return Lista de {@link Pedido} con todos los registros encontrados.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    @Override
    public List<Pedido> listarTodos() throws SQLException {
        String sql = "SELECT * FROM pedido ORDER BY Fecha_Ped DESC";
        List<Pedido> lista = new ArrayList<>();

        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Pedido p = mapearPedido(rs);
                p.setLineas(buscarLineas(p.getNum_Pedido()));
                lista.add(p);
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // READ - Por cliente
    // -------------------------------------------------------

    /**
     * Devuelve todos los pedidos de un cliente concreto, identificado por su NIF,
     * ordenados por fecha de pedido de forma descendente, con sus líneas cargadas.
     *
     * @param nifCliente NIF del cliente cuyos pedidos se desean obtener.
     * @return Lista de {@link Pedido} pertenecientes al cliente indicado.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    @Override
    public List<Pedido> listarPorCliente(String nifCliente) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE Nif_Cli = ? ORDER BY Fecha_Ped DESC";
        List<Pedido> lista = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nifCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = mapearPedido(rs);
                    p.setLineas(buscarLineas(p.getNum_Pedido()));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    /**
     * Actualiza la cabecera de un pedido existente y reemplaza todas sus líneas
     * eliminando las antiguas e insertando las nuevas, usando una transacción atómica.
     * Si ocurre cualquier error, se realiza un {@code rollback} completo.
     *
     * @param pedido Objeto {@link Pedido} con los datos actualizados y sus nuevas líneas.
     * @return {@code true} si la actualización se realizó correctamente.
     * @throws SQLException Si ocurre un error durante la actualización o la transacción.
     */
    @Override
    public boolean actualizar(Pedido pedido) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String sqlPedido = "UPDATE pedido SET Fecha_Ped=?, Fecha_ent=?, "
                    + "Precio_Total_Ped=?, Nif_Cli=? WHERE Num_Pedido=?";

            try (PreparedStatement ps = conn.prepareStatement(sqlPedido)) {
                ps.setDate(1, Date.valueOf(pedido.getFecha_ped()));
                ps.setDate(2, Date.valueOf(pedido.getFecha_ent()));
                ps.setFloat(3, pedido.getPrecio_Total_Ped());
                ps.setString(4, pedido.getNif_Cli());
                ps.setInt(5, pedido.getNum_Pedido());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM contiene WHERE Num_Pedido = ?")) {
                ps.setInt(1, pedido.getNum_Pedido());
                ps.executeUpdate();
            }

            String sqlLinea = "INSERT INTO contiene (Num_Pedido, Cod_Pro, Cantidad_Pro, Precio_Total) "
                    + "VALUES (?, ?, ?, ?)";

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

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    /**
     * Elimina un pedido de la base de datos a partir de su número identificador.
     *
     * @param numeroPedido Número del pedido a eliminar.
     * @return {@code true} si se eliminó al menos una fila; {@code false} en caso contrario.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    @Override
    public boolean eliminar(int numeroPedido) throws SQLException {
        String sql = "DELETE FROM pedido WHERE Num_Pedido = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // Auxiliar: líneas del pedido
    // -------------------------------------------------------

    /**
     * Obtiene todas las líneas de productos pertenecientes a un pedido concreto,
     * consultando la tabla {@code contiene} de la base de datos.
     *
     * @param numeroPedido Número del pedido cuyas líneas se desean obtener.
     * @return Lista de {@link LineaPedido} asociadas al pedido indicado.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    @Override
    public List<LineaPedido> buscarLineas(int numeroPedido) throws SQLException {
        String sql = "SELECT * FROM contiene WHERE Num_Pedido = ?";
        List<LineaPedido> lineas = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);

            try (ResultSet rs = ps.executeQuery()) {
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
    // Mapeo de ResultSet
    // -------------------------------------------------------

    /**
     * Convierte la fila actual de un {@link ResultSet} en un objeto {@link Pedido},
     * mapeando cada columna al atributo correspondiente del modelo.
     *
     * @param rs {@link ResultSet} posicionado en la fila a mapear.
     * @return Objeto {@link Pedido} construido con los datos de la fila actual.
     * @throws SQLException Si ocurre un error al leer el {@link ResultSet}.
     */
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
    // Procedimiento almacenado
    // -------------------------------------------------------

    /**
     * Invoca el procedimiento almacenado {@code MODIFICAR_PEDIDO} en la base de datos
     * pasando los seis parámetros necesarios para realizar la modificación.
     *
     * @param numPedido       Número del pedido a modificar.
     * @param accion          Acción a ejecutar dentro del procedimiento almacenado.
     * @param listaProductos  Cadena con los códigos de producto separados por comas.
     * @param listaCantidades Cadena con las cantidades de cada producto separadas por comas.
     * @param fechaPed        Nueva fecha de realización del pedido.
     * @param fechaEnt        Nueva fecha de entrega del pedido.
     * @return {@code true} si el procedimiento se ejecutó sin errores.
     * @throws SQLException Si ocurre un error durante la llamada al procedimiento.
     */
    @Override
    public boolean modificarPedidoProcedimiento(
            int numPedido,
            String accion,
            String listaProductos,
            String listaCantidades,
            LocalDate fechaPed,
            LocalDate fechaEnt
    ) throws SQLException {

        String sql = "{ CALL MODIFICAR_PEDIDO(?, ?, ?, ?, ?, ?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, numPedido);
            cs.setString(2, accion);
            cs.setString(3, listaProductos);
            cs.setString(4, listaCantidades);
            cs.setDate(5, Date.valueOf(fechaPed));
            cs.setDate(6, Date.valueOf(fechaEnt));

            cs.execute();
            return true;
        }
    }

    // -------------------------------------------------------
    // Auxiliares de generación de listas
    // -------------------------------------------------------

    /**
     * Genera una cadena de texto con los códigos de producto de todas las líneas
     * del pedido, separados por comas, para su uso como parámetro del procedimiento almacenado.
     * <p>
     * Ejemplo de resultado: {@code "PRD001,PRD002,PRD003"}
     * </p>
     *
     * @param pedido Objeto {@link Pedido} cuyas líneas se desean procesar.
     * @return Cadena de códigos de producto separados por comas.
     */
    public String generarListaProductos(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        for (LineaPedido lp : pedido.getLineas()) {
            sb.append(lp.getCod_Pro()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Genera una cadena de texto con las cantidades de cada línea del pedido,
     * separadas por comas, para su uso como parámetro del procedimiento almacenado.
     * <p>
     * Ejemplo de resultado: {@code "2,5,1"}
     * </p>
     *
     * @param pedido Objeto {@link Pedido} cuyas líneas se desean procesar.
     * @return Cadena de cantidades separadas por comas.
     */
    public String generarListaCantidades(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        for (LineaPedido lp : pedido.getLineas()) {
            sb.append(lp.getCantidad_Pro()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
