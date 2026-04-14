package paraLosTeses;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProductoOperacionesMockitoTest {

    @Test
    void testCalcularEnvioConMock() {

        Producto p = new Producto(1, "A", "desc", 50);

        // Mock de la clase EnvioService
        EnvioService servicioMock = mock(EnvioService.class);

        when(servicioMock.obtenerCosteEnvio("Madrid")).thenReturn(5.0);

        ProductoOperaciones op = new ProductoOperaciones();

        double resultado = op.calcularEnvio(p, "Madrid", servicioMock);

        assertEquals(55.0, resultado);
        assertNotEquals(50.0, resultado);
        assertTrue(resultado > p.getPrecio());
        assertFalse(resultado < p.getPrecio());
        assertNotNull(servicioMock);

        verify(servicioMock).obtenerCosteEnvio("Madrid");
    }

    @Test
    void testServicioNullLanzaExcepcion() {
        Producto p = new Producto(1, "A", "desc", 50);
        ProductoOperaciones op = new ProductoOperaciones();

        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                op.calcularEnvio(p, "Madrid", null);
            }
        });
    }

    @Test
    void testProductoNullLanzaExcepcion() {
        EnvioService servicioMock = mock(EnvioService.class);
        ProductoOperaciones op = new ProductoOperaciones();

        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                op.calcularEnvio(null, "Madrid", servicioMock);
            }
        });
    }
}
