package model;

import java.io.Serializable;

/**
 * Clase modelo que representa la entidad Proveedor.
 * 
 * <p>Forma parte de la capa de modelo en la arquitectura MVC y se utiliza
 * para transportar datos entre la base de datos, el DAO y la interfaz gráfica.</p>
 * 
 * <p>Contiene los atributos básicos de un proveedor, junto con sus métodos
 * de acceso (getters y setters).</p>
 * 
 * <p>Implementa {@link Serializable} para permitir la persistencia o
 * transmisión del objeto (por ejemplo, en ficheros o a través de red).</p>
 * 
 * <p><b>Nota:</b> El atributo NIF actúa como clave primaria.</p>
 * 
 * @author Anartz
 * @version 1.0
 */
public class Proveedor implements Serializable {
   
    // Identificador de versión para la serialización
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------
    // 🔹 ATRIBUTOS
    // -------------------------------------------------------

    // Clave primaria del proveedor
    private String Nif_Prove;

    // Nombre o empresa del proveedor
    private String Nombre;

    // Localidad donde se encuentra
    private String Localidad;

    // Teléfono de contacto (puede ser null)
    private String Telefono;

    // Email de contacto (puede ser null)
    private String email;

    // -------------------------------------------------------
    // 🔹 CONSTRUCTORES
    // -------------------------------------------------------

    /**
     * Constructor vacío.
     * 
     * <p>Necesario para frameworks, serialización o creación de objetos
     * sin inicialización inmediata.</p>
     */
    public Proveedor() {}

    /**
     * Constructor que inicializa todos los atributos del proveedor.
     * 
     * @param nif NIF del proveedor
     * @param nom nombre o empresa
     * @param loc localidad
     * @param tel teléfono de contacto (puede ser {@code null})
     * @param email correo electrónico (puede ser {@code null})
     */
    public Proveedor(String nif, String nom, String loc, String tel, String email) {
        this.Nif_Prove = nif;
        this.Nombre = nom;
        this.Localidad = loc;
        this.Telefono = tel;
        this.email = email;
    }

    /**
     * Obtiene el NIF del proveedor.
     * 
     * @return NIF del proveedor
     */
    // Devuelve el NIF del proveedor
    public String getNif_Prove() { return Nif_Prove; }

    // Establece el NIF
    public void setNif_Prove(String nif) { this.Nif_Prove = nif; }

    // Devuelve el nombre
    public String getNombre() { return Nombre; }

    // Establece el nombre
    public void setNombre(String nombre) { this.Nombre = nombre; }

    // Devuelve la localidad
    public String getLocalidad() { return Localidad; }

    // Establece la localidad
    public void setLocalidad(String loc) { this.Localidad = loc; }

    // Devuelve el teléfono
    public String getTelefono() { return Telefono; }

    // Establece el teléfono
    public void setTelefono(String tel) { this.Telefono = tel; }

    // Devuelve el email
    public String getEmail() { return email; }

    // Establece el email
    public void setEmail(String email) { this.email = email; }
}
