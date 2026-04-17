package paraLosTeses;

import static org.junit.jupiter.api.Assertions.*;
import config.*;
import dao.*;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MiTest {

	 private static ClienteDAO dao;
	    private Cliente clientePrueba;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		 ClienteDAO dao = new ClienteDAO();
	}

	@BeforeEach
	void setUp() throws Exception {
		Cliente clientePrueba = new Cliente(
		            "12345678A",
		            "Juan",
		            "Perez",
		            "Calle Falsa",
		            123,
		            "1A",
		            "Madrid",
		            "Madrid",
		            "123456789",
		            "juan@test.com");
	}

	@Test
	void testInsertar() throws SQLException{
		boolean resultado=dao.insertar(clientePrueba);
		assertTrue(resultado);
	}
	
	@Test
	void testBuscarPorNif() throws SQLException {
	    dao.insertar(clientePrueba);

	    Cliente c = dao.buscarPorNif("12345678A");

	    assertNotNull(c);
	    assertEquals("Juan", c.getNombre());
	}
	
	@Test
	void testListarTodos() throws SQLException {
	    List<Cliente> lista = dao.listarTodos();

	    assertNotNull(lista);
	    assertTrue(lista.size() >= 0);
	}
	
	@Test
	void testActualizar() throws SQLException {
	    dao.insertar(clientePrueba);

	    clientePrueba.setNombre("Carlos");

	    boolean actualizado = dao.actualizar(clientePrueba);

	    assertTrue(actualizado);

	    Cliente c = dao.buscarPorNif("12345678A");
	    assertEquals("Carlos", c.getNombre());
	}
	
	@Test
	void testEliminar() throws SQLException {
	    dao.insertar(clientePrueba);

	    boolean eliminado = dao.eliminar("12345678A");

	    assertTrue(eliminado);

	    Cliente c = dao.buscarPorNif("12345678A");
	    assertNull(c);
	}
	
	@Test
	void testInsertarClienteInvalido() {
	    Cliente c = new Cliente(null, null, null, null, 0, null, null, null, null, null);

	    assertThrows(SQLException.class, () -> {
	        dao.insertar(c);
	    });
	}
}
