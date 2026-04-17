package dao;

import model.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz DAO para la entidad Cliente.
 * Define las operaciones básicas (CRUD) que deben implementarse
 * para gestionar clientes en la base de datos.
 * 
 * @author Anartz
 */
public interface IClienteDAO {

    // -------------------------------------------------------
    // CREATE → Insertar un nuevo cliente
    // -------------------------------------------------------
    boolean insertar(Cliente c) throws SQLException;

    // -------------------------------------------------------
    // READ → Buscar cliente por su NIF (clave primaria)
    // -------------------------------------------------------
    Cliente buscarPorNif(String Nif_Cli) throws SQLException;

    // -------------------------------------------------------
    // READ → Obtener todos los clientes
    // -------------------------------------------------------
    List<Cliente> listarTodos() throws SQLException;

    // -------------------------------------------------------
    // UPDATE → Actualizar los datos de un cliente
    // -------------------------------------------------------
    boolean actualizar(Cliente c) throws SQLException;

    // -------------------------------------------------------
    // DELETE → Eliminar un cliente por su NIF
    // -------------------------------------------------------
    boolean eliminar(String Nif_Cli) throws SQLException;
}