// Anartz 
package dao;

import model.Proveedor;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz DAO para la entidad Proveedor.
 * Define las operaciones básicas (CRUD) y consultas específicas
 * que deben implementar las clases DAO.
 * 
 * @author Anartz
 */
public interface IProveedorDAO {

    // -------------------------------------------------------
    // CREATE → Insertar un nuevo proveedor
    // -------------------------------------------------------
    boolean insertar(Proveedor p) throws SQLException;

    // -------------------------------------------------------
    // READ → Buscar un proveedor por su NIF (clave primaria)
    // -------------------------------------------------------
    Proveedor buscarPorNif(String nif) throws SQLException;

    // -------------------------------------------------------
    // READ → Obtener todos los proveedores
    // -------------------------------------------------------
    List<Proveedor> listarTodos() throws SQLException;

    // -------------------------------------------------------
    // UPDATE → Actualizar los datos de un proveedor
    // -------------------------------------------------------
    boolean actualizar(Proveedor p) throws SQLException;

    // -------------------------------------------------------
    // DELETE → Eliminar un proveedor por su NIF
    // -------------------------------------------------------
    boolean eliminar(String nif) throws SQLException;

    // -------------------------------------------------------
    // CONSULTA EXTRA → Obtener el producto más vendido
    // de un proveedor concreto
    // -------------------------------------------------------
    String ProductoMasVendido(String nif) throws SQLException;
}
