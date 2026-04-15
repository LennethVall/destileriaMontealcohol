package dao;

import model.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface IClienteDAO {

    boolean insertar(Cliente c) throws SQLException;

    Cliente buscarPorNif(String Nif_Cli) throws SQLException;

    List<Cliente> listarTodos() throws SQLException;

    boolean actualizar(Cliente c) throws SQLException;

    boolean eliminar(String Nif_Cli) throws SQLException;

   
}
