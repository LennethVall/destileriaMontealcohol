package paraLosTeses;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoTest {

    private Producto p;

    @BeforeEach
    void setUp() {
        p = new Producto(1, "licor de cereza", "Gran Aroma", 30.00);
    }

    @AfterEach
    void destroyer() {
        p = null;
    }

    @Test
    void testConstructorVacio() {
        Producto p2 = new Producto();

        assertEquals(0, p2.getId());          
        assertNull(p2.getNombre());          
        assertNull(p2.getDescripcion());      
        assertEquals(0.0, p2.getPrecio());    
    }

    @Test
    void testConstructorLLeno() {
        assertEquals(1, p.getId());
        assertEquals("licor de cereza", p.getNombre());
        assertEquals("Gran Aroma", p.getDescripcion());
        assertEquals(30.00, p.getPrecio());
    }
}
