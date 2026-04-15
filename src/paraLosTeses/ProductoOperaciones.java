package paraLosTeses;

public class ProductoOperaciones {

    // Suma el precio de dos productos
    public double sumar(Producto p1, Producto p2) {
        if (p1 == null || p2 == null) {
            throw new NullPointerException("Producto no puede ser null");
        }
        return p1.getPrecio() + p2.getPrecio();
    }

    // Aplica un descuento fijo en euros
    public double aplicarDescuento(Producto p, double descuento) {
        if (p == null) {
            throw new NullPointerException("Producto no puede ser null");
        }
        if (descuento < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        if (descuento > p.getPrecio()) {
            throw new IllegalArgumentException("El descuento no puede superar el precio");
        }
        return p.getPrecio() - descuento;
    }

    // Multiplica el precio por una cantidad
    public double multiplicar(Producto p, int cantidad) {
        if (p == null) {
            throw new NullPointerException("Producto no puede ser null");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("Cantidad no puede ser negativa");
        }
        return p.getPrecio() * cantidad;
    }
    

public double pedidoConEnvio(Producto p, String ciudad, EnvioService servicio) {
    if (p == null) {
        throw new NullPointerException("Producto no puede ser null");
    }
    if (servicio == null) {
        throw new NullPointerException("Servicio no puede ser null");
    }

    double coste = servicio.obtenerCosteEnvio(ciudad);
    return p.getPrecio() + coste;
}
}
