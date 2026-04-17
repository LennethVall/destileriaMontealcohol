
package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Clase utilitaria para mostrar diálogos personalizados con la estética
 * corporativa de Montealcohol. Proporciona métodos estáticos para mostrar
 * mensajes informativos, de error y de confirmación, utilizando colores,
 * iconos y paneles adaptados al estilo visual de la aplicación.
 *
 * Los diálogos se construyen mediante paneles personalizados para mantener
 * coherencia estética con el resto de la interfaz.
 *
 * @author Ines
 * @version 1.0
 */
public class DialogosMontealcohol {


    private static final Color FONDO = new Color(255, 248, 230);
    private static final Color BORDE = new Color(230, 210, 180);
    private static final Color TEXTO = new Color(120, 90, 50);

    /**
     * Carga y escala un icono desde la ruta indicada para usarlo en los diálogos.
     *
     * @param ruta Ruta del archivo de imagen.
     * @return     Icono escalado a 48x48 píxeles.
     */
    private static ImageIcon icono(String ruta) {

        Image img = new ImageIcon(ruta).getImage();
        Image esc = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        return new ImageIcon(esc);
    }

    /**
     * Crea un panel estilizado con fondo crema vainilla, borde suave y tipografía
     * corporativa para mostrar mensajes dentro de los diálogos.
     *
     * @param mensaje Texto que se mostrará en el panel.
     * @return        Panel configurado con el mensaje formateado.
     */
    private static JPanel crearPanel(String mensaje) {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FONDO);
        p.setBorder(BorderFactory.createLineBorder(BORDE, 3));

        JLabel lbl = new JLabel("<html><body style='width:260px'>" + mensaje + "</body></html>");
        lbl.setFont(new Font("Serif", Font.PLAIN, 16));
        lbl.setForeground(TEXTO);
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    /**
     * Muestra un diálogo informativo con estilo Montealcohol.
     *
     * @param parent  Componente padre sobre el que se centrará el diálogo.
     * @param mensaje Mensaje que se mostrará al usuario.
     */
    public static void info(Component parent, String mensaje) {

        JOptionPane.showMessageDialog(
                parent,
                crearPanel(mensaje),
                "Información",
                JOptionPane.PLAIN_MESSAGE,
                icono("iconos/info.png")
        );
    }

    /**
     * Muestra un diálogo de error con estilo Montealcohol.
     *
     * @param parent  Componente padre sobre el que se centrará el diálogo.
     * @param mensaje Mensaje que se mostrará al usuario.
     */
    public static void error(Component parent, String mensaje) {

        JOptionPane.showMessageDialog(
                parent,
                crearPanel(mensaje),
                "Error",
                JOptionPane.PLAIN_MESSAGE,
                icono("iconos/error.png")
        );
    }

    /**
     * Muestra un diálogo de confirmación con estilo Montealcohol.
     *
     * @param parent  Componente padre sobre el que se centrará el diálogo.
     * @param mensaje Mensaje que se mostrará al usuario.
     * @return        Opción seleccionada por el usuario (YES o NO).
     */
    public static int confirmar(Component parent, String mensaje) {

        return JOptionPane.showConfirmDialog(
                parent,
                crearPanel(mensaje),
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                icono("iconos/pregunta.png")
        );
    }
}