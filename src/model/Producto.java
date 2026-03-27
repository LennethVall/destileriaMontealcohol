package model;

import java.io.Serializable;

/**
 * Clase Producto: Refleja la entidad 'Producto' y su relación 'Suministra' con Proveedor.
 */
public class Producto implements Serializable, Comparable<Producto> {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Atributos según el diagrama E-R
    private String Cod_Pro;      // Clave Primaria
    private String Nom_Pro;
    private double Precio_Pro;
    private int Stock;
    private Tipo tipo;
    private String Nif_Prove;    // Clave Ajena (Relación con Proveedor)

    // Constructor vacío (Obligatorio para frameworks y carga de BD)
    public Producto() {}

    // Constructor completo (Para crear productos nuevos)
    public Producto(String cod, String nom, double precio, int stock, Tipo tipo, String nifProv) {
        this.Cod_Pro = cod;
        this.Nom_Pro = nom;
        this.Precio_Pro = precio;
        this.Stock = stock;
        this.setTipo(tipo);
        this.Nif_Prove = nifProv;
    }

    // --- MÉTODOS DE COMPORTAMIENTO ---

    @Override
    public int compareTo(Producto otro) {
        // Permite que los Mapas o Listas se ordenen por Código automáticamente
        return this.Cod_Pro.compareTo(otro.getCod_Pro());
    }
    
    public static boolean codigoValido(String cod) {
        // 1 letra + 4 dígitos
        return cod != null && cod.matches("[A-Za-z][0-9]{4}");
    }


    // --- GETTERS Y SETTERS (Nombres exactos para AccesoBD) ---

    public String getCod_Pro() { return Cod_Pro; }
    public void setCod_Pro(String cod_Pro) { this.Cod_Pro = cod_Pro; }

    public String getNom_Pro() { return Nom_Pro; }
    public void setNom_Pro(String nom_Pro) { this.Nom_Pro = nom_Pro; }

    public double getPrecio_Pro() { return Precio_Pro; }
    public void setPrecio_Pro(double precio_Pro) { this.Precio_Pro = precio_Pro; }

    public int getStock() { return Stock; }
    public void setStock(int stock) { this.Stock = stock; }

    public String getNif_Prove() { return Nif_Prove; }
    public void setNif_Prove(String nif_Prove) { this.Nif_Prove = nif_Prove; }

    public Tipo getTipo() {return tipo;}
	public void setTipo(Tipo tipo) {this.tipo = tipo;}
}