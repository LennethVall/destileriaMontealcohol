
package model;

import java.io.Serializable;

/**
 * Clase que representa la relación "Contiene" del diagrama E-R.
 */
public class LineaPedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private int Num_Pedido;      // ⭐ FALTABA ESTE CAMPO
    private String Cod_Pro;
    private int Cantidad_Pro;
    private double Precio_Total;

    // Constructores
    public LineaPedido() {}

    public LineaPedido(int numPedido, String cod, int cant, double precio) {
        this.Num_Pedido = numPedido;
        this.Cod_Pro = cod;
        this.Cantidad_Pro = cant;
        this.Precio_Total = precio;
    }

    // Getters
    public int getNum_Pedido() { return Num_Pedido; }
    public String getCod_Pro() { return Cod_Pro; }
    public int getCantidad_Pro() { return Cantidad_Pro; }
    public double getPrecio_Total() { return Precio_Total; }

    // Setters
    public void setNum_Pedido(int num_Pedido) { this.Num_Pedido = num_Pedido; }
    public void setCod_Pro(String cod) { this.Cod_Pro = cod; }
    public void setCantidad_Pro(int cant) { this.Cantidad_Pro = cant; }
    public void setPrecio_Total(double precio) { this.Precio_Total = precio; }
}
