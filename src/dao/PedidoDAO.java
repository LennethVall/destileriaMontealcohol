package dao;

import config.DatabaseConnection;
import model.Pedido;
import model.LineaPedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO implements IPedidoDAO {

    // -------------------------------------------------------
    // CREATE - Inserta pedido + sus líneas en transacción
    // -------------------------------------------------------
    @Override
    public int insertar(Pedido pedido) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1. Insertar cabecera
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

            // 2. Insertar líneas
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
    // READ - Buscar por número de pedido
    // -------------------------------------------------------
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
    // READ - Listado completo
    // -------------------------------------------------------
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
    // READ - Pedidos por cliente
    // -------------------------------------------------------
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
    // UPDATE - Actualiza cabecera y líneas
    // -------------------------------------------------------
    @Override
    public boolean actualizar(Pedido pedido) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1. Actualizar cabecera
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

            // 2. Eliminar líneas antiguas
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM contiene WHERE Num_Pedido = ?")) {
                ps.setInt(1, pedido.getNum_Pedido());
                ps.executeUpdate();
            }

            // 3. Insertar nuevas líneas
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
    // DELETE - Eliminar pedido
    // -------------------------------------------------------
    @Override
    public boolean eliminar(int numeroPedido) throws SQLException {
        String sql = "DELETE FROM pedido WHERE Num_Pedido = ?";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, numeroPedido);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // Auxiliar: obtener líneas
    // -------------------------------------------------------
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
    // Mapeo ResultSet -> Pedido
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
    // PROCEDIMIENTO ALMACENADO: MODIFICAR_PEDIDO
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
    // GENERAR LISTAS PARA EL PROCEDIMIENTO
    // -------------------------------------------------------
    public String generarListaProductos(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        for (LineaPedido lp : pedido.getLineas()) {
            sb.append(lp.getCod_Pro()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String generarListaCantidades(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        for (LineaPedido lp : pedido.getLineas()) {
            sb.append(lp.getCantidad_Pro()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

	
}
