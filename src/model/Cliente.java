package model;

import java.io.Serializable;

/**
 *
 */
public class Cliente implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Nif_Cli;
    private String Nombre;
    private String Apellido;
    private String Calle;
    private int Numero; 
    private String Piso;
    private String Localidad;
    private String Provincia;
    private String Telefono;
    private String Email; // Representa el campo e-mail del diagrama

    // Constructor vacío (Obligatorio para que AccesoBD pueda rellenarlo)
    public Cliente() {}

    // Constructor lleno (Útil para crear clientes nuevos desde el panel)
    public Cliente(String nif, String nombre, String apellido, String calle, int numero, 
                   String piso, String localidad, String provincia, String telefono, String email) {
        this.Nif_Cli = nif;
        this.Nombre = nombre;
        this.Apellido = apellido;
        this.Calle = calle;
        this.Numero = numero;
        this.Piso = piso;
        this.Localidad = localidad;
        this.Provincia = provincia;
        this.Telefono = telefono;
        this.Email = email;
    }

    // --- GETTERS Y SETTERS (Nombres exactos para AccesoBD) ---
    public String getNif_Cli() { return Nif_Cli; }
    public void setNif_Cli(String nif_Cli) { this.Nif_Cli = nif_Cli; }

    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { this.Nombre = nombre; }

    public String getApellido() { return Apellido; }
    public void setApellido(String apellido) { this.Apellido = apellido; }

    public String getCalle() { return Calle; }
    public void setCalle(String calle) { this.Calle = calle; }

    public int getNumero() { return Numero; }
    public void setNumero(int numero) { this.Numero = numero; }

    public String getPiso() { return Piso; }
    public void setPiso(String piso) { this.Piso = piso; }

    public String getLocalidad() { return Localidad; }
    public void setLocalidad(String localidad) { this.Localidad = localidad; }

    public String getProvincia() { return Provincia; }
    public void setProvincia(String provincia) { this.Provincia = provincia; }

    public String getTelefono() { return Telefono; }
    public void setTelefono(String telefono) { this.Telefono = telefono; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { this.Email = email; }
}