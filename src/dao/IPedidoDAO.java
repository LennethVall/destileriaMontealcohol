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

    // Llama al procedimiento almacenado MODIFICAR_PEDIDO con los parametros necesarios
    boolean modificarPedidoProcedimiento(
            int numPedido,
            String accion,
            String listaProductos,
            String listaCantidades,
            LocalDate fechaPed,
            LocalDate fechaEnt
    ) throws SQLException;
}

/* Alvaro */
