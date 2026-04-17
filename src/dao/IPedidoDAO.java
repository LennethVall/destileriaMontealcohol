package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import model.LineaPedido;
import model.Pedido;

/**
 * Interfaz que define las operaciones de acceso a datos disponibles para los pedidos.
 * <p>
 * Establece el contrato que deben cumplir todas las implementaciones del patrón DAO
 * para la entidad {@link Pedido}, incluyendo operaciones CRUD, búsquedas específicas
 * y la invocación de procedimientos almacenados en la base de datos.
 * </p>
 *
 * @author Alvaro
 * @version 1.0
 */
public interface IPedidoDAO {

    /**
     * Inserta un nuevo pedido junto con todas sus líneas en la base de datos
     * utilizando una transacción atómica.
     *
     * @param pedido Objeto {@link Pedido} con los datos a insertar, incluidas sus líneas.
     * @return Número de pedido generado automáticamente por la base de datos.
     * @throws SQLException Si ocurre un error durante la inserción o la transacción.
     */
    int insertar(Pedido pedido) throws SQLException;

    /**
     * Busca y devuelve un pedido concreto a partir de su número identificador,
     * incluyendo todas sus líneas de producto asociadas.
     *
     * @param numeroPedido Número identificador del pedido a buscar.
     * @return El objeto {@link Pedido} encontrado, o {@code null} si no existe.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    Pedido buscarPorNumero(int numeroPedido) throws SQLException;

    /**
     * Devuelve una lista con todos los pedidos existentes en la base de datos,
     * ordenados por fecha de pedido de forma descendente.
     *
     * @return Lista de {@link Pedido} con todos los registros encontrados.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    List<Pedido> listarTodos() throws SQLException;

    /**
     * Devuelve todos los pedidos asociados a un cliente concreto, identificado
     * por su NIF, ordenados por fecha de pedido de forma descendente.
     *
     * @param nifCliente NIF del cliente cuyos pedidos se desean obtener.
     * @return Lista de {@link Pedido} pertenecientes al cliente indicado.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    List<Pedido> listarPorCliente(String nifCliente) throws SQLException;

    /**
     * Actualiza los datos de la cabecera de un pedido existente y reemplaza
     * todas sus líneas de producto con las nuevas proporcionadas, usando una
     * transacción atómica.
     *
     * @param pedido Objeto {@link Pedido} con los datos actualizados y sus nuevas líneas.
     * @return {@code true} si la actualización se realizó correctamente.
     * @throws SQLException Si ocurre un error durante la actualización o la transacción.
     */
    boolean actualizar(Pedido pedido) throws SQLException;

    /**
     * Elimina un pedido de la base de datos a partir de su número identificador.
     * Las líneas asociadas se eliminan en cascada si así lo define la BD.
     *
     * @param numeroPedido Número del pedido a eliminar.
     * @return {@code true} si se eliminó al menos una fila; {@code false} en caso contrario.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    boolean eliminar(int numeroPedido) throws SQLException;

    /**
     * Devuelve todas las líneas de productos pertenecientes a un pedido concreto.
     *
     * @param numeroPedido Número del pedido cuyas líneas se desean obtener.
     * @return Lista de {@link LineaPedido} asociadas al pedido indicado.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    List<LineaPedido> buscarLineas(int numeroPedido) throws SQLException;

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Pedido},
     * mapeando cada columna al atributo correspondiente del modelo.
     *
     * @param rs {@link ResultSet} posicionado en la fila a mapear.
     * @return Objeto {@link Pedido} construido con los datos de la fila actual.
     * @throws SQLException Si ocurre un error al leer el {@link ResultSet}.
     */
    Pedido mapearPedido(ResultSet rs) throws SQLException;

    /**
     * Invoca el procedimiento almacenado {@code MODIFICAR_PEDIDO} en la base de datos,
     * pasando los parámetros necesarios para modificar un pedido existente.
     *
     * @param numPedido       Número del pedido a modificar.
     * @param accion          Acción a realizar dentro del procedimiento almacenado.
     * @param listaProductos  Cadena con los códigos de producto separados por comas.
     * @param listaCantidades Cadena con las cantidades de cada producto separadas por comas.
     * @param fechaPed        Nueva fecha de realización del pedido.
     * @param fechaEnt        Nueva fecha de entrega del pedido.
     * @return {@code true} si el procedimiento se ejecutó correctamente.
     * @throws SQLException Si ocurre un error durante la llamada al procedimiento.
     */
    boolean modificarPedidoProcedimiento(
            int numPedido,
            String accion,
            String listaProductos,
            String listaCantidades,
            LocalDate fechaPed,
            LocalDate fechaEnt
    ) throws SQLException;
}
