
package dao;

import java.sql.SQLException;
import java.util.List;

import model.Producto;

public interface IProductoDAO {
	
	boolean insertar(Producto p) throws SQLException;
	
	Producto buscarPorCodigo(String codigo) throws SQLException;
	
	List<Producto> listarTodos() throws SQLException;
	
	List<Producto> listarPorTipo(String tipo) throws SQLException;
	
	boolean actualizar(Producto p) throws SQLException;
	
	boolean eliminar(String codigo) throws SQLException;

	boolean eliminarProductoProcedimiento(String codProducto) throws SQLException;

	boolean crearProductoConStockInicial(
	        String cod,
	        String nombre,
	        double precio,
	        String tipo,
	        String nifProveedor
	) throws SQLException;

}

