
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int Num_Pedido;
    private LocalDate Fecha_ped;
    private LocalDate Fecha_ent;
    private double Precio_Total_Ped;
    private String Nif_Cli;
    private List<LineaPedido> lineas = new ArrayList<>();

    public Pedido(int numPedido, LocalDate fechaPed, LocalDate fechaEnt, double precioTotal, String nifCli) {
    		this.Num_Pedido = numPedido;
    		this.Fecha_ped = fechaPed;
    		this.Fecha_ent = fechaEnt;
    		this.Precio_Total_Ped = precioTotal;
    		this.Nif_Cli = nifCli;
}

    
    public Pedido() {}

    // Getters y Setters
    public int getNum_Pedido() { return Num_Pedido; }
    public void setNum_Pedido(int num) { this.Num_Pedido = num; }
    public LocalDate getFecha_ped() { return Fecha_ped; }
    public void setFecha_ped(LocalDate fecha) { this.Fecha_ped = fecha; }
    public LocalDate getFecha_ent() { return Fecha_ent; }
    public void setFecha_ent(LocalDate fecha) { this.Fecha_ent = fecha; }
    public double getPrecio_Total_Ped() { return Precio_Total_Ped; }
    public void setPrecio_Total_Ped(double total) { this.Precio_Total_Ped = total; }
    public String getNif_Cli() { return Nif_Cli; }
    public void setNif_Cli(String nif) { this.Nif_Cli = nif; }
    public List<LineaPedido> getLineas() { return lineas; }
    public void setLineas(List<LineaPedido> lineas) { this.lineas = lineas; }

	

}
   
