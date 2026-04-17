/* Alvaro */

package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import model.LineaPedido;
import model.Pedido;

// Interfaz que define las operaciones disponibles para el acceso a datos de pedidos
public interface IPedidoDAO {

    // Inserta un nuevo pedido con sus lineas y devuelve el numero generado
    int insertar(Pedido pedido) throws SQLException;

    // Busca y devuelve un pedido concreto a partir de su numero
    Pedido buscarPorNumero(int numeroPedido) throws SQLException;

    // Devuelve una lista con todos los pedidos existentes en la base de datos
    List<Pedido> listarTodos() throws SQLException;

    // Devuelve todos los pedidos asociados a un cliente concreto por su NIF
    List<Pedido> listarPorCliente(String nifCliente) throws SQLException;

    // Actualiza los datos de un pedido existente junto a sus lineas
    boolean actualizar(Pedido pedido) throws SQLException;

    // Elimina un pedido de la base de datos a partir de su numero
    boolean eliminar(int numeroPedido) throws SQLException;

    // Devuelve todas las lineas de productos pertenecientes a un pedido concreto
    List<LineaPedido> buscarLineas(int numeroPedido) throws SQLException;

    // Convierte una fila del ResultSet en un objeto Pedido
    Pedido mapearPedido(ResultSet rs) throws SQLException;

    /**
     * Modifica un pedido utilizando el procedimiento almacenado MODIFICAR_PEDIDO.
     *
     * @param numPedido Número del pedido.
     * @param listaPro  Lista CSV de códigos de producto (ej: "A0001,B0002").
     * @param listaCan  Lista CSV de cantidades (ej: "2,5").
     * @param fechaPed  Nueva fecha de pedido.
     * @param fechaEnt  Nueva fecha de entrega.
     * @return true si el procedimiento se ejecutó correctamente.
     * @throws SQLException Si ocurre un error durante la ejecución.
     */
    boolean modificarPedidoProcedimiento(int numPedido,
                                         String listaPro,
                                         String listaCan,
                                         java.sql.Date fechaPed,
                                         java.sql.Date fechaEnt) throws SQLException;
}


/* Alvaro */
