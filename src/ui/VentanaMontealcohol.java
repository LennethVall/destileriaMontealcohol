package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaMontealcohol extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel marco;
    private JPanel barraSuperior;
    private JPanel contenido;

    // Botones de ventana
    private JLabel closebtn;
    private JLabel minimicebtn;
    private JLabel maximicebtn;

    // Cursores personalizados
    private Cursor menuCur;
    private Cursor clientesCur;
    private Cursor productosCur;
    private Cursor proveedoresCur;
    private Cursor pedidosCur;

	private Component maximicebtn1;

   
    public VentanaMontealcohol() {
        cargarCursores();            // cursores temáticos (alambique, copa…)
        cargarCursoresEspeciales();  // manoCur y textoCur
        configurarVentana();
        crearMarco();
        crearBarraSuperior();
        crearContenido();
    }


    // ───────────────────────────────────────────────
    // ESCALAR ICONOS (SOLUCIÓN AL ICONO GIGANTE)
    // ───────────────────────────────────────────────
    private ImageIcon escalarIcono(String ruta, int w, int h) {
        Image img = new ImageIcon(ruta).getImage();
        Image esc = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(esc);
    }

    private void cargarCursores() {
        Toolkit tk = Toolkit.getDefaultToolkit();

        menuCur        = tk.createCustomCursor(cargarYEscalar("cursores/menuCur.png", 64),        new Point(0,0), "menuCur");
        clientesCur    = tk.createCustomCursor(cargarYEscalar("cursores/clientesCur.png", 64),    new Point(0,0), "clientesCur");
        productosCur   = tk.createCustomCursor(cargarYEscalar("cursores/productosCur.png", 64),   new Point(0,0), "productosCur");
        proveedoresCur = tk.createCustomCursor(cargarYEscalar("cursores/proveedoresCur.png", 64), new Point(0,0), "proveedoresCur");
        pedidosCur     = tk.createCustomCursor(cargarYEscalar("cursores/pedidosCur.png", 64),     new Point(0,0), "pedidosCur");
    }
    
    private Cursor manoCur;
    private Cursor textoCur;

    private void cargarCursoresEspeciales() {
        Toolkit tk = Toolkit.getDefaultToolkit();

        manoCur = tk.createCustomCursor(
            new ImageIcon("cursores/manoCur.png").getImage(),
            new Point(0, 0),
            "manoCur"
        );

        textoCur = tk.createCustomCursor(
            new ImageIcon("cursores/textoCur.png").getImage(),
            new Point(0, 0),
            "textoCur"
        );
    }



    private void configurarVentana() {
        setUndecorated(true);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setCursor(menuCur);
    }

    private void crearMarco() {
        Color cremaVainilla = new Color(255, 248, 230);
        Color bordeSuave = new Color(230, 210, 180);

        marco = new JPanel(new BorderLayout());
        marco.setBackground(cremaVainilla);
        marco.setBorder(BorderFactory.createLineBorder(bordeSuave, 6));

        setContentPane(marco);
    }

    private void crearBarraSuperior() {
        barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(new Color(255, 248, 230));
        barraSuperior.setPreferredSize(new Dimension(0, 40));

        hacerArrastrable(barraSuperior);
        crearBotonesVentana();

        // PANEL IZQUIERDO: ICONO + TÍTULO
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelIzquierdo.setOpaque(false);

        JLabel iconoVentana = new JLabel(escalarIcono("iconos/logoApp.png", 24, 24));
        JLabel tituloVentana = new JLabel("MONTEALCOHOL  -  Destilería Artesanal · Sistema de Gestión Integral");
        tituloVentana.setFont(new Font("Serif", Font.BOLD, 18));
        tituloVentana.setForeground(new Color(120, 90, 50));

        panelIzquierdo.add(iconoVentana);
        panelIzquierdo.add(tituloVentana);

        // PANEL DERECHO: BOTONES
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        panelBotones.setOpaque(false);

        panelBotones.add(minimicebtn);
        panelBotones.add(maximicebtn);
        panelBotones.add(closebtn);

        barraSuperior.add(panelIzquierdo, BorderLayout.WEST);
        barraSuperior.add(panelBotones, BorderLayout.EAST);

        marco.add(barraSuperior, BorderLayout.NORTH);
        
        
    }


    private void crearBotonesVentana() {

        closebtn    = new JLabel(escalarIcono("botones/closebtn.png", 24, 24));
        minimicebtn = new JLabel(escalarIcono("botones/minimicebtn.png", 24, 24));
        maximicebtn = new JLabel(escalarIcono("botones/maximicebtn.png", 24, 24));

        // cursores personalizados
        closebtn.setCursor(manoCur);
        minimicebtn.setCursor(manoCur);
        maximicebtn.setCursor(manoCur);

        añadirHover(closebtn);
        añadirHover(minimicebtn);
        añadirHover(maximicebtn);

        closebtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        minimicebtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setState(JFrame.ICONIFIED);
            }
        });

        maximicebtn.addMouseListener(new MouseAdapter() {
            boolean maximizado = false;
            Rectangle normalBounds;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!maximizado) {
                    normalBounds = getBounds();
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                } else {
                    setBounds(normalBounds);
                }
                maximizado = !maximizado;
            }
        });
    }


    private void añadirHover(JLabel boton) {
        boton.setOpaque(true); // importante para que el cursor no desaparezca
        boton.setBackground(new Color(0,0,0,0)); // transparente real

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(255, 240, 210));
                boton.setCursor(manoCur); // ← cursor personalizado de mano
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(0,0,0,0));
                boton.setCursor(menuCur); // ← vuelve al cursor normal personalizado
            }
        });
    }

    
    private void aplicarCursorTexto(JTextField campo) {
        campo.setCursor(textoCur); // ← cursor personalizado de texto
    }


    private void hacerArrastrable(JPanel panel) {
        final Point[] click = {null};

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                click[0] = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(
                    p.x + e.getX() - click[0].x,
                    p.y + e.getY() - click[0].y
                );
            }
        });
    }
    public void mostrarPanel(JPanel panel) {
        contenido.removeAll();
        contenido.add(panel, BorderLayout.CENTER);
        contenido.revalidate();
        contenido.repaint();
    }


    private void crearContenido() {
        contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BorderLayout()); // ← CORRECTO
        marco.add(contenido, BorderLayout.CENTER);
    }
    private Image cargarYEscalar(String ruta, int size) {
        Image img = new ImageIcon(ruta).getImage();
        return img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
    }

    public JPanel getContenido() {
        return contenido;
    }
    public Cursor getManoCur() { return manoCur; }
    public Cursor getTextoCur() { return textoCur; }
    public Cursor getMenuCur() { return menuCur; }

}
