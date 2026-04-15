package paraLosTeses;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

class ProductoOperacionesMockitoTest {

    @Test
    void testPedidoConEnvioMock() {

        Producto p = new Producto(1, "A", "desc", 50);

        // Crear mock del servicio
        EnvioService servicioMock = mock(EnvioService.class);

        // Definir comportamiento del mock
        when(servicioMock.obtenerCosteEnvio("Madrid")).thenReturn(5.0);

        ProductoOperaciones op = new ProductoOperaciones();

        // Llamar al método que usa el mock
        double resultado = op.pedidoConEnvio(p, "Madrid", servicioMock);

        // Comprobaciones
        assertEquals(55.0, resultado);
        verify(servicioMock).obtenerCosteEnvio("Madrid");
    }

    @Test
    void testServicioNullLanzaExcepcion() {
        Producto p = new Producto(1, "A", "desc", 50);
        ProductoOperaciones op = new ProductoOperaciones();

        assertThrows(NullPointerException.class,
                () -> op.pedidoConEnvio(p, "Madrid", null));
    }

    @Test
    void testProductoNullLanzaExcepcion() {
        EnvioService servicioMock = mock(EnvioService.class);
        ProductoOperaciones op = new ProductoOperaciones();

        assertThrows(NullPointerException.class,
                () -> op.pedidoConEnvio(null, "Madrid", servicioMock));
    }
}
