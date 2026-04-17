package model;

import java.io.Serializable;

/**
 * Clase que representa una línea de pedido dentro del sistema.
 * <p>
 * Equivale a la tabla {@code Contiene} del diagrama Entidad-Relación.
 * Cada instancia almacena el producto, la cantidad y el precio total
 * correspondientes a una entrada concreta de un pedido.
 * Implementa {@link Serializable} para permitir la persistencia del objeto.
 * </p>
 *
 * @author Alvaro
 * @version 1.0
 */
public class LineaPedido implements Serializable {

    /**
     * Identificador de versión para la serialización del objeto.
     */
    private static final long serialVersionUID = 1L;

    /** Número del pedido al que pertenece esta línea. */
    private int Num_Pedido;

    /** Código del producto incluido en esta línea. */
    private String Cod_Pro;

    /** Cantidad de unidades del producto pedidas. */
    private int Cantidad_Pro;

    /** Precio total de esta línea, calculado como cantidad × precio unitario. */
    private float Precio_Total;

    /**
     * Constructor vacío necesario para instanciar el objeto sin datos iniciales.
     */
    public LineaPedido() {}

    /**
     * Constructor completo que inicializa todos los campos de la línea de pedido.
     *
     * @param numPedido Número del pedido al que pertenece esta línea.
     * @param cod       Código del producto incluido en la línea.
     * @param cant      Cantidad de unidades pedidas.
     * @param precio    Precio total calculado para esta línea.
     */
    public LineaPedido(int numPedido, String cod, int cant, float precio) {
        this.Num_Pedido = numPedido;
        this.Cod_Pro = cod;
        this.Cantidad_Pro = cant;
        this.Precio_Total = precio;
    }

    /**
     * Obtiene el número del pedido al que pertenece esta línea.
     *
     * @return Número del pedido.
     */
    public int getNum_Pedido() { return Num_Pedido; }

    /**
     * Obtiene el código del producto de esta línea.
     *
     * @return Código del producto.
     */
    public String getCod_Pro() { return Cod_Pro; }

    /**
     * Obtiene la cantidad de unidades pedidas en esta línea.
     *
     * @return Cantidad de unidades.
     */
    public int getCantidad_Pro() { return Cantidad_Pro; }

    /**
     * Obtiene el precio total calculado para esta línea.
     *
     * @return Precio total de la línea.
     */
    public float getPrecio_Total() { return Precio_Total; }

    /**
     * Asigna el número del pedido al que pertenece esta línea.
     *
     * @param num_Pedido Número de pedido a asignar.
     */
    public void setNum_Pedido(int num_Pedido) { this.Num_Pedido = num_Pedido; }

    /**
     * Asigna el código del producto de esta línea.
     *
     * @param cod Código de producto a asignar.
     */
    public void setCod_Pro(String cod) { this.Cod_Pro = cod; }

    /**
     * Asigna la cantidad de unidades pedidas en esta línea.
     *
     * @param cant Cantidad a asignar.
     */
    public void setCantidad_Pro(int cant) { this.Cantidad_Pro = cant; }

    /**
     * Asigna el precio total de esta línea.
     *
     * @param precio Precio total a asignar.
     */
    public void setPrecio_Total(float precio) { this.Precio_Total = precio; }
}
