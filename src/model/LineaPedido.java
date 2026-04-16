/* Alvaro */

package model;

import java.io.Serializable;

// Clase que representa una linea del pedido, equivalente a la tabla "Contiene" del diagrama E-R
public class LineaPedido implements Serializable {

    // Identificador de version para la serializacion del objeto
    private static final long serialVersionUID = 1L;

    // Numero del pedido al que pertenece esta linea
    private int Num_Pedido;

    // Codigo del producto incluido en esta linea
    private String Cod_Pro;

    // Cantidad de unidades del producto pedidas
    private int Cantidad_Pro;

    // Precio total de esta linea (cantidad x precio unitario)
    private float Precio_Total;

    // Constructor vacio necesario para instanciar sin datos iniciales
    public LineaPedido() {}

    // Constructor que inicializa todos los campos de la linea del pedido
    public LineaPedido(int numPedido, String cod, int cant, float precio) {
        this.Num_Pedido = numPedido;
        this.Cod_Pro = cod;
        this.Cantidad_Pro = cant;
        this.Precio_Total = precio;
    }

    // Devuelve el numero del pedido al que pertenece esta linea
    public int getNum_Pedido() { return Num_Pedido; }

    // Devuelve el codigo del producto de esta linea
    public String getCod_Pro() { return Cod_Pro; }

    // Devuelve la cantidad de unidades pedidas en esta linea
    public int getCantidad_Pro() { return Cantidad_Pro; }

    // Devuelve el precio total calculado para esta linea
    public float getPrecio_Total() { return Precio_Total; }

    // Asigna el numero del pedido al que pertenece esta linea
    public void setNum_Pedido(int num_Pedido) { this.Num_Pedido = num_Pedido; }

    // Asigna el codigo del producto de esta linea
    public void setCod_Pro(String cod) { this.Cod_Pro = cod; }

    // Asigna la cantidad de unidades pedidas en esta linea
    public void setCantidad_Pro(int cant) { this.Cantidad_Pro = cant; }

    // Asigna el precio total de esta linea
    public void setPrecio_Total(float precio) { this.Precio_Total = precio; }
}

/* Alvaro */
