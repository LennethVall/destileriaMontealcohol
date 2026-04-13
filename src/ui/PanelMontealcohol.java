package ui;

import javax.swing.*;
import java.awt.*;

public class PanelMontealcohol extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VentanaMontealcohol ventana;

    public PanelMontealcohol(VentanaMontealcohol ventana) {
        this.ventana = ventana;

        setOpaque(false);
        setLayout(new BorderLayout());
        setFont(new Font("Serif", Font.PLAIN, 16));
        setForeground(new Color(60, 40, 20));
    }

    public VentanaMontealcohol getVentana() {
        return ventana;
    }
   
    


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Activar suavizado
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo crema vainilla suave (semi-transparente)
        g2.setColor(new Color(255, 248, 230, 220));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Borde suave
        g2.setColor(new Color(230, 210, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
    }
}
