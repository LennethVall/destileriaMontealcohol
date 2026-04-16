package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel base utilizado por los distintos módulos del sistema Montealcohol.
 * Proporciona un estilo visual unificado, incluyendo fondo crema vainilla
 * semitransparente, borde redondeado y tipografía corporativa.
 *
 * Además, mantiene una referencia a la ventana principal {@link VentanaMontealcohol}
 * para permitir interacción con la interfaz general (cambio de paneles, cursores, etc.).
 *
 * @author Ines Carrasco
 * @version 1.0
 */
public class PanelMontealcohol extends JPanel {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VentanaMontealcohol ventana;

	/**
	 * Crea un panel estilizado con la estética Montealcohol y lo asocia a la
	 * ventana principal desde la que se está utilizando.
	 *
	 * @param ventana Instancia de la ventana principal que contiene este panel.
	 */
	public PanelMontealcohol(VentanaMontealcohol ventana) {

        this.ventana = ventana;

        setOpaque(false);
        setLayout(new BorderLayout());
        setFont(new Font("Serif", Font.PLAIN, 16));
        setForeground(new Color(60, 40, 20));
    }

	/**
	 * Devuelve la ventana principal asociada a este panel.
	 *
	 * @return VentanaMontealcohol que contiene este panel.
	 */
	public VentanaMontealcohol getVentana() {

        return ventana;
    }
   
 

    // ⭐⭐⭐ ESTILO MONTEALCOHOL PARA CAMPOS NO EDITABLES
	/**
	 * Aplica el estilo Montealcohol a un campo de texto no editable, utilizando
	 * colores más oscuros, borde tenue y tipografía discreta.
	 *
	 * @param tf Campo de texto que se desea estilizar.
	 */
	public void estilizarNoEditable(JTextField tf) {

        tf.setBackground(new Color(30, 22, 12)); // más oscuro
        tf.setForeground(new Color(180, 150, 110)); // texto apagado
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 60, 20)), // borde más tenue
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }



   
    /**
     * Pinta el fondo característico del panel Montealcohol: un rectángulo
     * redondeado en tono crema vainilla semitransparente, con borde suave y
     * suavizado activado para mejorar la calidad visual.
     *
     * @param g Contexto gráfico utilizado para dibujar el panel.
     */
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
