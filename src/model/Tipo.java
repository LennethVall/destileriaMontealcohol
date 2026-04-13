
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
