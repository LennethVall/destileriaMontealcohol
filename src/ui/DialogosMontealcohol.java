package ui;

import javax.swing.*;
import java.awt.*;

public class DialogosMontealcohol {

    private static final Color FONDO = new Color(255, 248, 230);
    private static final Color BORDE = new Color(230, 210, 180);
    private static final Color TEXTO = new Color(120, 90, 50);

    private static ImageIcon icono(String ruta) {
        Image img = new ImageIcon(ruta).getImage();
        Image esc = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        return new ImageIcon(esc);
    }

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

    public static void info(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(
                parent,
                crearPanel(mensaje),
                "Información",
                JOptionPane.PLAIN_MESSAGE,
                icono("iconos/info.png")
        );
    }

    public static void error(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(
                parent,
                crearPanel(mensaje),
                "Error",
                JOptionPane.PLAIN_MESSAGE,
                icono("iconos/error.png")
        );
    }

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
