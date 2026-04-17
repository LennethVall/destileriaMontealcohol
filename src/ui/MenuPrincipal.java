
package ui;

import config.DatabaseConnection;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Menú principal del sistema Montealcohol. Actúa como pantalla inicial desde la
 * que se accede a los distintos módulos de gestión: clientes, proveedores,
 * productos y pedidos.
 *
 * Extiende la ventana base personalizada {@link VentanaMontealcohol} para
 * mantener la estética corporativa y los cursores temáticos.
 *
 * Incluye cabecera, botones de navegación y pie informativo.
 *
 * @author Ines Carrasco
 * @version 1.0
 */
public class MenuPrincipal extends VentanaMontealcohol {


    private static final long serialVersionUID = 1L;

    // ── Paleta corporativa ────────────────────────────────────
    static final Color COLOR_FONDO    = new Color(28, 20, 14);
    static final Color COLOR_PRIMARIO = new Color(180, 120, 40);
    static final Color COLOR_TEXTO    = new Color(240, 230, 210);
    static final Color COLOR_BOTON_BG = new Color(55, 38, 20);
    static final Color COLOR_HOVER    = new Color(200, 140, 55);
    static final Font  FUENTE_TITULO  = new Font("Serif",     Font.BOLD,   28);
    static final Font  FUENTE_BTN     = new Font("SansSerif", Font.BOLD,   14);
    static final Font  FUENTE_SUB     = new Font("Serif",     Font.ITALIC, 14);

    /**
     * Construye el menú principal, configura el comportamiento al cerrar la ventana
     * y genera toda la interfaz gráfica del menú.
     */
    public MenuPrincipal() {

    	 super();
        // ❌ ELIMINADO: new MenuPrincipal().setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int op = JOptionPane.showConfirmDialog(MenuPrincipal.this,
                        "¿Desea salir de la aplicación?", "Confirmar salida",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (op == JOptionPane.YES_OPTION) {
                    DatabaseConnection.close();
                    System.exit(0);
                }
            }
        });

        construirUI();
    }

    /**
     * Construye la interfaz del menú principal: cabecera, botones de acceso a los
     * módulos y pie de información. Utiliza el panel de contenido heredado de
     * {@link VentanaMontealcohol}.
     */
    private void construirUI() {

        // ✔️ Usar SIEMPRE el panel contenido de VentanaMontealcohol
        getContenido().setLayout(new BorderLayout(0, 0));

        // ── Cabecera ─────────────────────────────────────────
        JPanel cabecera = new JPanel(new GridLayout(3, 1, 0, 4));
        cabecera.setBackground(COLOR_FONDO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel lblTitulo = new JLabel("MONTEALCOHOL", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(COLOR_PRIMARIO);

        JLabel lblSub = new JLabel("Destilería Artesanal · Sistema de Gestión Integral", SwingConstants.CENTER);
        lblSub.setFont(FUENTE_SUB);
        lblSub.setForeground(COLOR_TEXTO);

        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_PRIMARIO);

        cabecera.add(lblTitulo);
        cabecera.add(lblSub);
        cabecera.add(sep);

        getContenido().add(cabecera, BorderLayout.NORTH);

        // ── Panel de botones ──────────────────────────────────
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 20, 20));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        panelBotones.add(crearBotonModulo(" Gestión de Clientes", e -> abrirModulo(new ClientePanel(this))));
        panelBotones.add(crearBotonModulo(" Gestión de Proveedores", e -> abrirModulo(new ProveedorPanel(this))));
        panelBotones.add(crearBotonModulo(" Gestión de Productos", e -> abrirModulo(new ProductoPanel(this))));
        panelBotones.add(crearBotonModulo(" Gestión de Pedidos", e -> abrirModulo(new PedidoPanel(this))));

        getContenido().add(panelBotones, BorderLayout.CENTER);

        // ── Pie ───────────────────────────────────────────────
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setBackground(COLOR_FONDO);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));

        JLabel lblVersion = new JLabel("v1.0  |  © 2024 Destilería Montealcohol");
        lblVersion.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(120, 100, 70));

        pie.add(lblVersion);

        getContenido().add(pie, BorderLayout.SOUTH);
    }

    /**
     * Crea un botón estilizado para acceder a un módulo del sistema. Aplica la
     * paleta corporativa, efectos hover y el cursor personalizado.
     *
     * @param titulo Texto que se mostrará en el botón.
     * @param al     Acción que se ejecutará al pulsar el botón.
     * @return       Botón configurado y listo para añadir a la interfaz.
     */
    private JButton crearBotonModulo(String titulo, ActionListener al) {

        JButton btn = new JButton("<html><center>" + titulo + "</center></html>") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };

        btn.setUI(new BasicButtonUI());
        btn.setFont(FUENTE_BTN);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_FONDO);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARIO, 1),
                BorderFactory.createEmptyBorder(18, 12, 18, 12)
        ));
        btn.setCursor(getManoCur());



        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_BOTON_BG);
                btn.setForeground(COLOR_HOVER);
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_FONDO);
                btn.setForeground(COLOR_TEXTO);
                btn.repaint();
            }
        });

        btn.addActionListener(al);
        return btn;
    }

    /**
     * Abre un módulo en una nueva ventana de tipo {@link VentanaMontealcohol}.
     * Inserta el panel recibido dentro del área de contenido de la nueva ventana.
     *
     * @param panel Panel correspondiente al módulo que se desea mostrar.
     */
    private void abrirModulo(JPanel panel) {

        VentanaMontealcohol ventana = new VentanaMontealcohol();
        panel.setBounds(20, 20, 1000, 600);
        ventana.getContenido().add(panel);
        ventana.setVisible(true);
    }

    /**
     * Punto de entrada de la aplicación. Inicia el menú principal dentro del hilo
     * de eventos de Swing.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}