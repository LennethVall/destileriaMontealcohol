package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un pedido realizado por un cliente en el sistema.
 * <p>
 * Contiene la cabecera del pedido (fechas, precio total y cliente asociado)
 * así como la lista de {@link LineaPedido} que lo componen.
 * Implementa {@link Serializable} para permitir la persistencia del objeto.
 * </p>
 *
 * @author Alvaro
 * @version 1.0
 */
public class Pedido implements Serializable {

    /**
     * Identificador de versión para la serialización del objeto.
     */
    private static final long serialVersionUID = 1L;

    /** Número único que identifica al pedido en la base de datos. */
    private int Num_Pedido;

    /** Fecha en la que se realiza el pedido. */
    private LocalDate Fecha_ped;

    /** Fecha en la que se debe entregar el pedido. */
    private LocalDate Fecha_ent;

    /** Precio total acumulado de todas las líneas del pedido. */
    private float Precio_Total_Ped;

    /** NIF del cliente al que pertenece el pedido. */
    private String Nif_Cli;

    /** Lista de líneas de productos que componen el pedido. */
    private List<LineaPedido> lineas = new ArrayList<>();

    /**
     * Constructor completo con todos los campos principales del pedido.
     *
     * @param numPedido   Número identificador del pedido.
     * @param fechaPed    Fecha en que se realizó el pedido.
     * @param fechaEnt    Fecha de entrega prevista del pedido.
     * @param precioTotal Precio total acumulado del pedido.
     * @param nifCli      NIF del cliente asociado al pedido.
     */
    public Pedido(int numPedido, LocalDate fechaPed, LocalDate fechaEnt, float precioTotal, String nifCli) {
        this.Num_Pedido = numPedido;
        this.Fecha_ped = fechaPed;
        this.Fecha_ent = fechaEnt;
        this.Precio_Total_Ped = precioTotal;
        this.Nif_Cli = nifCli;
    }

    /**
     * Constructor vacío necesario para crear instancias sin datos iniciales.
     */
    public Pedido() {}

    /**
     * Obtiene el número del pedido.
     *
     * @return Número identificador del pedido.
     */
    public int getNum_Pedido() { return Num_Pedido; }

    /**
     * Asigna el número del pedido.
     *
     * @param num Número a asignar al pedido.
     */
    public void setNum_Pedido(int num) { this.Num_Pedido = num; }

    /**
     * Obtiene la fecha en que se realizó el pedido.
     *
     * @return Fecha de realización del pedido.
     */
    public LocalDate getFecha_ped() { return Fecha_ped; }

    /**
     * Asigna la fecha en que se realizó el pedido.
     *
     * @param fecha Fecha de pedido a asignar.
     */
    public void setFecha_ped(LocalDate fecha) { this.Fecha_ped = fecha; }

    /**
     * Obtiene la fecha de entrega del pedido.
     *
     * @return Fecha de entrega prevista.
     */
    public LocalDate getFecha_ent() { return Fecha_ent; }

    /**
     * Asigna la fecha de entrega del pedido.
     *
     * @param fecha Fecha de entrega a asignar.
     */
    public void setFecha_ent(LocalDate fecha) { this.Fecha_ent = fecha; }

    /**
     * Obtiene el precio total del pedido.
     *
     * @return Precio total acumulado de todas las líneas.
     */
    public float getPrecio_Total_Ped() { return Precio_Total_Ped; }

    /**
     * Asigna el precio total del pedido.
     *
     * @param total Precio total a asignar.
     */
    public void setPrecio_Total_Ped(float total) { this.Precio_Total_Ped = total; }

    /**
     * Obtiene el NIF del cliente asociado al pedido.
     *
     * @return NIF del cliente.
     */
    public String getNif_Cli() { return Nif_Cli; }

    /**
     * Asigna el NIF del cliente asociado al pedido.
     *
     * @param nif NIF del cliente a asignar.
     */
    public void setNif_Cli(String nif) { this.Nif_Cli = nif; }

    /**
     * Obtiene la lista de líneas de productos del pedido.
     *
     * @return Lista de {@link LineaPedido} que componen el pedido.
     */
    public List<LineaPedido> getLineas() { return lineas; }

    /**
     * Asigna la lista de líneas de productos del pedido.
     *
     * @param lineas Lista de {@link LineaPedido} a asignar.
     */
    public void setLineas(List<LineaPedido> lineas) { this.lineas = lineas; }
}