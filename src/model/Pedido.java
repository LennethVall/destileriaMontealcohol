/* Alvaro */

package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Clase que representa un pedido realizado por un cliente en el sistema
public class Pedido implements Serializable {

    // Identificador de version para la serializacion del objeto
    private static final long serialVersionUID = 1L;

    // Numero unico que identifica al pedido en la base de datos
    private int Num_Pedido;

    // Fecha en la que se realiza el pedido
    private LocalDate Fecha_ped;

    // Fecha en la que se debe entregar el pedido
    private LocalDate Fecha_ent;

    // Precio total acumulado de todas las lineas del pedido
    private float Precio_Total_Ped;

    // NIF del cliente al que pertenece el pedido
    private String Nif_Cli;

    // Lista de lineas de productos que componen el pedido
    private List<LineaPedido> lineas = new ArrayList<>();

    // Constructor con todos los campos principales del pedido
    public Pedido(int numPedido, LocalDate fechaPed, LocalDate fechaEnt, float precioTotal, String nifCli) {
        this.Num_Pedido = numPedido;
        this.Fecha_ped = fechaPed;
        this.Fecha_ent = fechaEnt;
        this.Precio_Total_Ped = precioTotal;
        this.Nif_Cli = nifCli;
    }

    // Constructor vacio necesario para crear instancias sin datos iniciales
    public Pedido() {}

    // Devuelve el numero del pedido
    public int getNum_Pedido() { return Num_Pedido; }

    // Asigna el numero del pedido
    public void setNum_Pedido(int num) { this.Num_Pedido = num; }

    // Devuelve la fecha en que se realizo el pedido
    public LocalDate getFecha_ped() { return Fecha_ped; }

    // Asigna la fecha en que se realizo el pedido
    public void setFecha_ped(LocalDate fecha) { this.Fecha_ped = fecha; }

    // Devuelve la fecha de entrega del pedido
    public LocalDate getFecha_ent() { return Fecha_ent; }

    // Asigna la fecha de entrega del pedido
    public void setFecha_ent(LocalDate fecha) { this.Fecha_ent = fecha; }

    // Devuelve el precio total del pedido
    public float getPrecio_Total_Ped() { return Precio_Total_Ped; }

    // Asigna el precio total del pedido
    public void setPrecio_Total_Ped(float total) { this.Precio_Total_Ped = total; }

    // Devuelve el NIF del cliente asociado al pedido
    public String getNif_Cli() { return Nif_Cli; }

    // Asigna el NIF del cliente asociado al pedido
    public void setNif_Cli(String nif) { this.Nif_Cli = nif; }

    // Devuelve la lista de lineas de productos del pedido
    public List<LineaPedido> getLineas() { return lineas; }

    // Asigna la lista de lineas de productos del pedido
    public void setLineas(List<LineaPedido> lineas) { this.lineas = lineas; }
}

/* Alvaro */
