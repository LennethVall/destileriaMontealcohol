/**
 * Enumeración que representa los distintos tipos de productos.
 * 
 * <p>Se utiliza para clasificar los productos según su proceso de elaboración
 * o categoría dentro del sistema.</p>
 * 
 * <p>Cada tipo contiene una etiqueta ({@code label}) que se utiliza para
 * mostrar un nombre más descriptivo en la interfaz de usuario.</p>
 * 
 * <p>Incluye un método estático para convertir una etiqueta en su correspondiente
 * valor del enum.</p>
 * 
 * <p>Valores disponibles:</p>
 * <ul>
 *   <li>Fermentadas</li>
 *   <li>Destiladas</li>
 *   <li>Encabezadas</li>
 *   <li>Licores</li>
 *   <li>Nuestra Selección</li>
 * </ul>
 * 
 * @author Anartz
 * @version 1.0
 */

package model;

public enum Tipo {
    Fermentadas("Fermentadas"),
    Destiladas("Destiladas"),
    Encabezadas("Encabezadas"),
    Licores("Licores"),
    NuestraSelección("Nuestra Selección");

    private final String label;

    Tipo(String label) { this.label = label; }

    public String getLabel() { return label; }

    public static Tipo fromLabel(String label) {
        for (Tipo t : values()) {
            if (t.label.equals(label)) return t;
        }
        throw new IllegalArgumentException("Tipo desconocido: " + label);
    }
}