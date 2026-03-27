package dao;

import model.Proveedor;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz DAO para la entidad Proveedor.
 */
public interface IProveedorDAO {

    // CREATE
    boolean insertar(Proveedor p) throws SQLException;

    // READ
    Proveedor buscarPorNif(String nif) throws SQLException;

    List<Proveedor> listarTodos() throws SQLException;

    // UPDATE
    boolean actualizar(Proveedor p) throws SQLException;

    // DELETE
    boolean eliminar(String nif) throws SQLException;
}
