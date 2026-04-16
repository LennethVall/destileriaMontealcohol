package dao;

import java.sql.SQLException;
import java.util.List;

import model.Producto;

/**
 * Interfaz DAO para la gestión de productos en la base de datos.
 * Define las operaciones CRUD principales, así como métodos adicionales
 * que utilizan procedimientos almacenados para modificar o eliminar productos.
 *
 * Implementada por {@link ProductoDAO}.
 *
 * @author Ines
 * @version 1.0
 */
public interface IProductoDAO {

    
	/**
	 * Inserta un nuevo producto en la base de datos.
	 *
	 * @param p Producto a insertar.
	 * @return true si la operación fue exitosa.
	 * @throws SQLException Si ocurre un error durante la inserción.
	 */
	boolean insertar(Producto p) throws SQLException;

    
	/**
	 * Busca un producto por su código.
	 *
	 * @param codigo Código del producto.
	 * @return Producto encontrado o null si no existe.
	 * @throws SQLException Si ocurre un error durante la consulta.
	 */
	Producto buscarPorCodigo(String codigo) throws SQLException;

    
	/**
	 * Obtiene un listado completo de productos.
	 *
	 * @return Lista de todos los productos.
	 * @throws SQLException Si ocurre un error durante la consulta.
	 */
	List<Producto> listarTodos() throws SQLException;

    
	/**
	 * Obtiene todos los productos pertenecientes a un tipo específico.
	 *
	 * @param tipo Tipo de producto (según etiqueta almacenada en la BD).
	 * @return Lista de productos filtrados por tipo.
	 * @throws SQLException Si ocurre un error durante la consulta.
	 */
	List<Producto> listarPorTipo(String tipo) throws SQLException;

    
	/**
	 * Actualiza los datos de un producto existente.
	 *
	 * @param p Producto con los datos actualizados.
	 * @return true si la actualización fue exitosa.
	 * @throws SQLException Si ocurre un error durante la operación.
	 */
	boolean actualizar(Producto p) throws SQLException;

    
	/**
	 * Elimina un producto por su código.
	 *
	 * @param codigo Código del producto a eliminar.
	 * @return true si la eliminación fue exitosa.
	 * @throws SQLException Si ocurre un error durante la operación.
	 */
	boolean eliminar(String codigo) throws SQLException;


	/**
	 * Elimina un producto utilizando el procedimiento almacenado
	 * ELIMINAR_PRODUCTO.
	 *
	 * @param codProducto Código del producto.
	 * @return true si el procedimiento se ejecutó correctamente.
	 * @throws SQLException Si ocurre un error durante la ejecución.
	 */
	boolean eliminarProductoProcedimiento(String codProducto) throws SQLException;


    // Nuevo método para añadir stock usando el procedimiento AÑADIR_PRODUCTO
	/**
	 * Añade stock a un producto utilizando el procedimiento almacenado
	 * AÑADIR_PRODUCTO. El procedimiento devuelve un mensaje informativo.
	 *
	 * @param cod      Código del producto.
	 * @param cantidad Cantidad a añadir.
	 * @return Mensaje devuelto por el procedimiento.
	 */
	String añadirProductoProcedimiento(String cod, int cantidad);

}
