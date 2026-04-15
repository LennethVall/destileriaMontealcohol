
package ui;

import dao.ProveedorDAO;
import model.Proveedor;
import service.XMLGenerator;
import service.XMLGeneratorImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel CRUD para la gestión de Proveedores.
 */
public class ProveedorPanel extends PanelMontealcohol implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Dimension TAM_BOTON = new Dimension(180, 42);

	
	// ── GeneradorXML──────────────────────────────────────────
	private final XMLGenerator xml = new XMLGeneratorImpl();

	private final ProveedorDAO dao = new ProveedorDAO();

    private final String[] COLUMNAS = {"NIF", "Nombre/Empresa", "Localidad", "Teléfono", "Email"};
    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField txtNif       = new JTextField(10);
    private final JTextField txtNombre    = new JTextField(22);
    private final JTextField txtLocalidad = new JTextField(15);
    private final JTextField txtTelefono  = new JTextField(12);
    private final JTextField txtEmail     = new JTextField(18);

    private final JButton btnInsertar  = crearBtn(" Insertar",  new Color(200, 170, 120), "iconos/añadir.png");
    private final JButton btnBuscar    = crearBtn(" Buscar",    new Color(200, 170, 120),"iconos/buscar.png");
    private final JButton btnActualizar= crearBtn(" Actualizar", new Color(200, 170, 120), "iconos/actualizar.png");
    private final JButton btnEliminar  = crearBtn(" Eliminar",  new Color(200, 170, 120), "iconos/borrar.png");
    private final JButton btnListar    = crearBtn(" Listar todos", new Color(200, 170, 120),"iconos/cargarDatos.png");
    private final JButton btnLimpiar   = crearBtn(" Limpiar",   new Color(200, 170, 120), "iconos/limpiar.png");


    public ProveedorPanel(VentanaMontealcohol ventana) {
        super(ventana);

        setLayout(new BorderLayout(8, 8));
        setOpaque(false);

        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(),      BorderLayout.CENTER);
        add(crearBotones(),    BorderLayout.SOUTH);

        configurarEventos();

        estandarizarBotones(
            btnInsertar,
            btnBuscar,
            btnActualizar,
            btnEliminar,
            btnListar,
            btnLimpiar
        );

        try {
            cargarTodos();
        } catch (SQLException ex) {
            DialogosMontealcohol.error(this, "Error al cargar proveedores");
        }

    }

        private ImageIcon escalarIcono(String ruta, int ancho, int alto) {
            Image img = new ImageIcon(ruta).getImage();
            Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(nueva);
        
    }


    private JPanel crearFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        estilizar(txtNif, txtNombre, txtLocalidad, txtTelefono, txtEmail);

        Object[][] campos = {
            {"NIF *", txtNif, "Nombre/Empresa *", txtNombre, "Localidad *", txtLocalidad},
            {"Teléfono ", txtTelefono, "Email", txtEmail, null, null}
        };

        int fila = 0;
        for (Object[] row : campos) {
            for (int c = 0; c < row.length; c += 2) {
                if (row[c] == null) break;
                g.gridx = c; g.gridy = fila; g.weightx = 0;
                p.add(etiqueta(row[c].toString()), g);
                g.gridx = c + 1; g.weightx = 1;
                p.add((Component) row[c + 1], g);
            }
            fila++;
        }
        return p;
    }

    private JScrollPane crearTabla() {
        tabla.setBackground(new Color(40, 28, 15));
        tabla.setForeground(MenuPrincipal.COLOR_TEXTO);
        tabla.setGridColor(new Color(80, 60, 30));
        tabla.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setBackground(MenuPrincipal.COLOR_BOTON_BG);
        tabla.getTableHeader().setForeground(MenuPrincipal.COLOR_PRIMARIO);
        tabla.getTableHeader().setFont(MenuPrincipal.FUENTE_BTN);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0)
                rellenarFormulario(tabla.getSelectedRow());
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        sp.getViewport().setBackground(new Color(40, 28, 15));
        return sp;
    }

    private JPanel crearBotones() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_FONDO);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.gridy = 0;

        JButton[] botones = {
            btnInsertar,
            btnBuscar,
            btnActualizar,
            btnEliminar,
            btnListar,
            btnLimpiar
        };

        for (int i = 0; i < botones.length; i++) {
            g.gridx = i;
            p.add(botones[i], g);
        }

        return p;
    }


    private void configurarEventos() {
        btnInsertar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnActualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnListar.addActionListener(this);
        btnLimpiar.addActionListener(this);
    }

    private void insertar() throws SQLException {
        Proveedor pv = getProveedor();
        if (pv == null) return;

        if (dao.insertar(pv)) {
            try {
				xml.generarXML();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            info("Proveedor insertado correctamente.");
            limpiar();
            cargarTodos();
        }
    }


    private void buscar() throws SQLException{
        String nif = txtNif.getText().trim();
        if (nif.isEmpty()) { error("Introduce el NIF para buscar."); return; }
        
            Proveedor pv = dao.buscarPorNif(nif);
            modelo.setRowCount(0);
            if (pv != null) { agregarFila(pv); rellenarCampos(pv); }
            else info("No se encontró proveedor con NIF: " + nif);
       
    }

    private void actualizar() throws SQLException {
        Proveedor pv = getProveedor();
        if (pv == null) return;

        if (dao.actualizar(pv)) {
            try {
                xml.generarXML();   // ← AQUÍ, solo si se actualizó correctamente
            } catch (Exception e) {
                e.printStackTrace();
            }

            info("Proveedor actualizado.");
            cargarTodos();
        } else {
            info("No existe proveedor con ese NIF.");
        }
    }


    private void eliminar() throws SQLException {
        String nif = txtNif.getText().trim();
        if (nif.isEmpty()) {
            error("Introduce el NIF del proveedor a eliminar.");
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar proveedor con NIF " + nif + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (op != JOptionPane.YES_OPTION) return;

        if (dao.eliminar(nif)) {

            try {
                xml.generarXML();   // ⭐ GENERAR XML SIEMPRE QUE SE MODIFICA BD
            } catch (Exception e) {
                e.printStackTrace();
            }

            info("Proveedor eliminado correctamente.");
            limpiar();
            cargarTodos();

        } else {
            info("No existe proveedor con ese NIF.");
        }
    }



    private void cargarTodos() throws SQLException{
        
            List<Proveedor> lista = dao.listarTodos();
            modelo.setRowCount(0);
            for (Proveedor pv : lista) agregarFila(pv);
       
    }

    // ── Helpers ───────────────────────────────────────────────
    private Proveedor getProveedor() {
        String nif       = txtNif.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String email     = txtEmail.getText().trim();
        if (nif.isEmpty() || nombre.isEmpty() || localidad.isEmpty()) {
            error("Los campos NIF, Nombre y Localidad son obligatorios."); return null;
        }
        return new Proveedor(nif, nombre, localidad, telefono, email.isEmpty() ? null : email);
    }

    private void agregarFila(Proveedor pv) {
        modelo.addRow(new Object[]{
            pv.getNif_Prove(),
            pv.getNombre(),
            pv.getLocalidad(),
            pv.getTelefono() != null ? pv.getTelefono() : "",
            pv.getEmail()    != null ? pv.getEmail()    : ""
        });
    }

    private void rellenarFormulario(int fila) {
        txtNif.setText(valorOVacio(fila, 0));
        txtNombre.setText(valorOVacio(fila, 1));
        txtLocalidad.setText(valorOVacio(fila, 2));
        txtTelefono.setText(valorOVacio(fila, 3));
        txtEmail.setText(valorOVacio(fila, 4));
        
        txtNif.setEditable(false);
        estilizarNoEditable(txtNif);


    }

    private String valorOVacio(int fila, int columna) {
        Object val = modelo.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }

    private void rellenarCampos(Proveedor pv) {
        txtNif.setText(pv.getNif_Prove());
        txtNif.setEditable(false);
        estilizarNoEditable(txtNif);

        txtNombre.setText(pv.getNombre());
        txtLocalidad.setText(pv.getLocalidad());
        txtTelefono.setText(pv.getTelefono());
        txtEmail.setText(pv.getEmail() != null ? pv.getEmail() : "");
    }

    private void limpiar() {
        for (JTextField tf : new JTextField[]{txtNif, txtNombre, txtLocalidad, txtTelefono, txtEmail})
            tf.setText("");
        
        txtNif.setEditable(true);
        estilizar(txtNif);   // vuelve al estilo editable normal


        
        modelo.setRowCount(0);  // ← AÑADIR ESTO
        tabla.clearSelection();
    }

    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(MenuPrincipal.COLOR_PRIMARIO);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    private void estilizar(JTextField... campos) {
        for (JTextField tf : campos) {
            tf.setBackground(new Color(50, 35, 16));
            tf.setForeground(MenuPrincipal.COLOR_TEXTO);
            tf.setCaretColor(MenuPrincipal.COLOR_PRIMARIO);
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 80, 30)),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
            ));
            tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
            tf.setCursor(getVentana().getTextoCur());

        }
    }

    private JButton crearBtn(String texto, Color bg,  String rutaIcono) {
        JButton b = new JButton(texto);
        
         if (rutaIcono != null) {
        	 ImageIcon icono = escalarIcono(rutaIcono, 28, 28);
        	 b.setIcon(icono);
        	 b.setHorizontalTextPosition(SwingConstants.RIGHT);
        	 b.setIconTextGap(10);
         }
        b.setFont(MenuPrincipal.FUENTE_BTN);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(getVentana().getManoCur());

        return b;
    }

    private void info(String m)  { DialogosMontealcohol.info(this, m); }
    private void error(String m) { DialogosMontealcohol.error(this, m); }
    private void estandarizarBotones(JButton... botones) {
        for (JButton b : botones) {
            b.setPreferredSize(TAM_BOTON);
        }
    }

    

public void actionPerformed(ActionEvent e) {

	try {
		if (e.getSource() == btnInsertar)
			insertar();
		else if (e.getSource() == btnBuscar)
			buscar();
		else if (e.getSource() == btnActualizar)
			actualizar();
		else if (e.getSource() == btnEliminar)
			eliminar();
		else if (e.getSource() == btnListar)
			cargarTodos();
		else if (e.getSource() == btnLimpiar)
			limpiar();
		
		
	 } catch (SQLException ex) {
		 error ("proceso fallido");
}
}


public XMLGenerator getXml() {
	return xml;
}
}
