package paraLosTeses;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProductoOperacionesTest {

    private Producto p1;
    private Producto p2;
    private ProductoOperaciones op;

    @BeforeEach
    void setUp() {
        p1 = new Producto(1, "A", "desc", 10);
        p2 = new Producto(2, "B", "desc", 20);
        op = new ProductoOperaciones();
    }

    @AfterEach
    void tearDown() {
        p1 = null;
        p2 = null;
        op = null;
    }

    // ---------------------------------------------------------
    // TESTS DE EXCEPCIONES (assertThrows)
    // ---------------------------------------------------------
    @Test
    void testSumarProductoNull() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                op.sumar(null, p2);
            }
        });
    }

    @Test
    void testDescuentoNegativo() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                op.aplicarDescuento(p1, -1);
            }
        });
    }

    @Test
    void testDescuentoMayorQuePrecio() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                op.aplicarDescuento(p1, 50);
            }
        });
    }

    @Test
    void testMultiplicarCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                op.multiplicar(p1, -1);
            }
        });
    }

    // ---------------------------------------------------------
    // TESTS NORMALES (equals, notEquals, true, false)
    // ---------------------------------------------------------
    @Test
    void testSumarCorrecto() {
        double resultado = op.sumar(p1, p2);

        assertEquals(30, resultado);          // equals
        assertNotEquals(25, resultado);       // not equals
        assertTrue(resultado > 0);            // true
        assertFalse(resultado < 0);           // false
    }

    @Test
    void testAplicarDescuentoCorrecto() {
        double resultado = op.aplicarDescuento(p1, 5);

        assertEquals(5, resultado);           // equals
        assertNotEquals(10, resultado);       // not equals
        assertTrue(resultado < p1.getPrecio());
    }

    @Test
    void testMultiplicarCorrecto() {
        double resultado = op.multiplicar(p1, 3);

        assertEquals(30, resultado);
        assertTrue(resultado == 30);
        assertFalse(resultado == 20);
    }

    // ---------------------------------------------------------
    // TESTS DE NULL / NOT NULL
    // ---------------------------------------------------------
    @Test
    void testProductoNoEsNull() {
        assertNotNull(p1);
    }

    @Test
    void testNombreEsNullEnConstructorVacio() {
        Producto p3 = new Producto();
        assertNull(p3.getNombre());
    }
}
