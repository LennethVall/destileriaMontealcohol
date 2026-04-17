package model;

import java.io.Serializable;

/**
 * Clase que representa a un cliente del sistema.
 * <p>
 * Contiene los datos personales y de contacto del cliente,
 * incluyendo su identificación fiscal, dirección y medios de contacto.
 * Implementa {@link Serializable} para permitir la persistencia del objeto.
 * </p>
 *
 * @author Alvaro
 * @version 1.0
 */
public class Cliente implements Serializable {

    /**
     * Identificador de versión para la serialización del objeto.
     */
    private static final long serialVersionUID = 1L;

    /** NIF (Número de Identificación Fiscal) del cliente. */
    private String Nif_Cli;

    /** Nombre del cliente. */
    private String Nombre;

    /** Apellido del cliente. */
    private String Apellido;

    /** Nombre de la calle donde reside el cliente. */
    private String Calle;

    /** Número del portal o edificio en la dirección del cliente. */
    private int Numero;

    /** Piso o planta de la dirección del cliente. */
    private String Piso;

    /** Localidad de residencia del cliente. */
    private String Localidad;

    /** Provincia de residencia del cliente. */
    private String Provincia;

    /** Número de teléfono de contacto del cliente. */
    private String Telefono;

    /** Dirección de correo electrónico del cliente (campo e-mail del diagrama). */
    private String Email;

    /**
     * Constructor vacío requerido por {@code AccesoBD} para instanciar
     * y rellenar el objeto mediante reflexión o acceso directo a los campos.
     */
    public Cliente() {}

    /**
     * Constructor completo para crear un nuevo cliente con todos sus datos.
     * <p>
     * Útil para instanciar clientes desde el panel de administración u otras vistas.
     * </p>
     *
     * @param nif       NIF del cliente.
     * @param nombre    Nombre del cliente.
     * @param apellido  Apellido del cliente.
     * @param calle     Nombre de la calle de la dirección del cliente.
     * @param numero    Número del portal o edificio.
     * @param piso      Piso o planta de la dirección.
     * @param localidad Localidad de residencia.
     * @param provincia Provincia de residencia.
     * @param telefono  Número de teléfono de contacto.
     * @param email     Dirección de correo electrónico.
     */
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

    // --- GETTERS Y SETTERS ---

    /**
     * Obtiene el NIF del cliente.
     *
     * @return NIF del cliente.
     */
    public String getNif_Cli() { return Nif_Cli; }

    /**
     * Establece el NIF del cliente.
     *
     * @param nif_Cli NIF a asignar al cliente.
     */
    public void setNif_Cli(String nif_Cli) { this.Nif_Cli = nif_Cli; }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return Nombre del cliente.
     */
    public String getNombre() { return Nombre; }

    /**
     * Establece el nombre del cliente.
     *
     * @param nombre Nombre a asignar al cliente.
     */
    public void setNombre(String nombre) { this.Nombre = nombre; }

    /**
     * Obtiene el apellido del cliente.
     *
     * @return Apellido del cliente.
     */
    public String getApellido() { return Apellido; }

    /**
     * Establece el apellido del cliente.
     *
     * @param apellido Apellido a asignar al cliente.
     */
    public void setApellido(String apellido) { this.Apellido = apellido; }

    /**
     * Obtiene la calle de la dirección del cliente.
     *
     * @return Nombre de la calle.
     */
    public String getCalle() { return Calle; }

    /**
     * Establece la calle de la dirección del cliente.
     *
     * @param calle Nombre de la calle a asignar.
     */
    public void setCalle(String calle) { this.Calle = calle; }

    /**
     * Obtiene el número del portal o edificio del cliente.
     *
     * @return Número de la dirección.
     */
    public int getNumero() { return Numero; }

    /**
     * Establece el número del portal o edificio del cliente.
     *
     * @param numero Número a asignar.
     */
    public void setNumero(int numero) { this.Numero = numero; }

    /**
     * Obtiene el piso de la dirección del cliente.
     *
     * @return Piso o planta de la dirección.
     */
    public String getPiso() { return Piso; }

    /**
     * Establece el piso de la dirección del cliente.
     *
     * @param piso Piso o planta a asignar.
     */
    public void setPiso(String piso) { this.Piso = piso; }

    /**
     * Obtiene la localidad de residencia del cliente.
     *
     * @return Localidad del cliente.
     */
    public String getLocalidad() { return Localidad; }

    /**
     * Establece la localidad de residencia del cliente.
     *
     * @param localidad Localidad a asignar.
     */
    public void setLocalidad(String localidad) { this.Localidad = localidad; }

    /**
     * Obtiene la provincia de residencia del cliente.
     *
     * @return Provincia del cliente.
     */
    public String getProvincia() { return Provincia; }

    /**
     * Establece la provincia de residencia del cliente.
     *
     * @param provincia Provincia a asignar.
     */
    public void setProvincia(String provincia) { this.Provincia = provincia; }

    /**
     * Obtiene el número de teléfono del cliente.
     *
     * @return Teléfono de contacto del cliente.
     */
    public String getTelefono() { return Telefono; }

    /**
     * Establece el número de teléfono del cliente.
     *
     * @param telefono Teléfono a asignar.
     */
    public void setTelefono(String telefono) { this.Telefono = telefono; }

    /**
     * Obtiene el correo electrónico del cliente.
     *
     * @return Email del cliente.
     */
    public String getEmail() { return Email; }

    /**
     * Establece el correo electrónico del cliente.
     *
     * @param email Email a asignar al cliente.
     */
    public void setEmail(String email) { this.Email = email; }
}
