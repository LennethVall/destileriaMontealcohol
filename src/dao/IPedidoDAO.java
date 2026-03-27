package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.LineaPedido;
import model.Pedido;

public interface IPedidoDAO {
	
	int insertar(Pedido pedido) throws SQLException;
	
	Pedido buscarPorNumero(int numeroPedido) throws SQLException;
	
	List<Pedido> listarTodos() throws SQLException;
	
	List<Pedido> listarPorCliente(String nifCliente) throws SQLException;
	
	boolean actualizar(Pedido pedido) throws SQLException;
	
	boolean eliminar(int numeroPedido) throws SQLException;
	
	List<LineaPedido> buscarLineas(int numeroPedido) throws SQLException;
	
	Pedido mapearPedido(ResultSet rs) throws SQLException;

}
