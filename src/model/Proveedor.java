
package model;

import java.io.Serializable;

public class Proveedor implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Nif_Prove;
    private String Nombre;
    private String Localidad;
    private String Telefono;
    private String email;

    public Proveedor() {}

    public Proveedor(String nif, String nom, String loc, String tel, String email) {
        this.Nif_Prove = nif;
        this.Nombre = nom;
        this.Localidad = loc;
        this.Telefono = tel;
        this.email = email;
    }

    public String getNif_Prove() { return Nif_Prove; }
    public void setNif_Prove(String nif) { this.Nif_Prove = nif; }
    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { this.Nombre = nombre; }
    public String getLocalidad() { return Localidad; }
    public void setLocalidad(String loc) { this.Localidad = loc; }
    public String getTelefono() { return Telefono; }
    public void setTelefono(String tel) { this.Telefono = tel; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
