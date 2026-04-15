
package ui;

import dao.ClienteDAO;
import model.Cliente;
import service.XMLGenerator;
import service.XMLGeneratorImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.ImageIcon;


/**
 * Panel CRUD completo para la gestión de Clientes.
 */
public class ClientePanel extends PanelMontealcohol implements ActionListener {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Dimension TAM_BOTON = new Dimension(180, 42);

	
	// ── GeneradorXML──────────────────────────────────────────
	private final XMLGenerator xml = new XMLGeneratorImpl();


	// ── DAO ──────────────────────────────────────────────────
    private final ClienteDAO dao = new ClienteDAO();

    // ── Tabla ────────────────────────────────────────────────
    private final String[] COLUMNAS = {"NIF", "Nombre", "Apellido", "Calle",
                                        "Nº", "Piso", "Localidad", "Provincia",
                                        "Teléfono", "Email"};
    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    


    // ── Campos del formulario ─────────────────────────────────
    private final JTextField txtNif       = new JTextField(10);
    private final JTextField txtNombre    = new JTextField(15);
    private final JTextField txtApellido  = new JTextField(15);
    private final JTextField txtCalle     = new JTextField(20);
    private final JTextField txtNumero    = new JTextField(5);
    private final JTextField txtPiso      = new JTextField(5);
    private final JTextField txtLocalidad = new JTextField(12);
    private final JTextField txtProvincia = new JTextField(12);
    private final JTextField txtTelefono  = new JTextField(12);
    private final JTextField txtEmail     = new JTextField(18);

    // ── Botones ───────────────────────────────────────────────
    private final JButton btnInsertar  = crearBtn(" Insertar",  new Color(200, 170, 120), "iconos/añadir.png");
    private final JButton btnBuscar    = crearBtn(" Buscar",    new Color(200, 170, 120),"iconos/buscar.png");
    private final JButton btnActualizar= crearBtn(" Actualizar", new Color(200, 170, 120), "iconos/actualizar.png");
    private final JButton btnEliminar  = crearBtn(" Eliminar",  new Color(200, 170, 120), "iconos/borrar.png");
    private final JButton btnListar    = crearBtn(" Listar todos", new Color(200, 170, 120),"iconos/cargarDatos.png");
    private final JButton btnLimpiar   = crearBtn(" Limpiar",   new Color(200, 170, 120), "iconos/limpiar.png");

    public ClientePanel(VentanaMontealcohol ventana) {
        super(ventana);

        setLayout(new BorderLayout(8, 8));

        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelTabla(),      BorderLayout.CENTER);
        add(crearPanelBotones(),    BorderLayout.SOUTH);

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
            DialogosMontealcohol.error(this, "Error al cargar los clientes: " + ex.getMessage());
        }

    }

        
        private ImageIcon escalarIcono(String ruta, int ancho, int alto) {
            Image img = new ImageIcon(ruta).getImage();
            Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(nueva);
        

    }

    // ── Formulario ────────────────────────────────────────────
    private JPanel crearPanelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        Object[][] filas = {
            {"NIF *",       txtNif,       "Nombre *",    txtNombre,    "Apellido *",  txtApellido},
            {"Calle *",     txtCalle,     "Número *",    txtNumero,    "Piso ",      txtPiso},
            {"Localidad *", txtLocalidad, "Provincia *", txtProvincia, "Teléfono ",  txtTelefono},
            {"Email",       txtEmail,     null,          null,         null,          null}
        };

        estilizarCampos(txtNif, txtNombre, txtApellido, txtCalle, txtNumero,
                        txtPiso, txtLocalidad, txtProvincia, txtTelefono, txtEmail);

        int fila = 0;
        for (Object[] rowData : filas) {
            for (int col = 0; col < rowData.length; col += 2) {
                if (rowData[col] == null) break;
                g.gridx = col; g.gridy = fila; g.weightx = 0;
                p.add(etiqueta(rowData[col].toString()), g);
                g.gridx = col + 1; g.weightx = 1;
                p.add((Component) rowData[col + 1], g);
            }
            fila++;
        }
        return p;
    }

    // ── Tabla ─────────────────────────────────────────────────
    private JScrollPane crearPanelTabla() {
        tabla.setBackground(new Color(40, 28, 15));
        tabla.setForeground(MenuPrincipal.COLOR_TEXTO);
        tabla.setGridColor(new Color(80, 60, 30));
        tabla.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setBackground(MenuPrincipal.COLOR_BOTON_BG);
        tabla.getTableHeader().setForeground(MenuPrincipal.COLOR_PRIMARIO);
        tabla.getTableHeader().setFont(MenuPrincipal.FUENTE_BTN);

        // Al hacer clic en fila → rellenar formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                rellenarFormulario(tabla.getSelectedRow());
            }
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        sp.getViewport().setBackground(new Color(40, 28, 15));
        return sp;
    }

    // ── Botones ───────────────────────────────────────────────
    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setBackground(MenuPrincipal.COLOR_FONDO);
        for (JButton b : new JButton[]{btnInsertar, btnBuscar, btnActualizar,
                                        btnEliminar, btnListar, btnLimpiar}) {
            p.add(b);
        }
        return p;
    }

    // ── Eventos ───────────────────────────────────────────────
    private void configurarEventos() {
        btnInsertar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnActualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnListar.addActionListener(this);
        btnLimpiar.addActionListener(this);
    }

    
    
    private void insertar() throws SQLException {

        Cliente c = obtenerClienteFormulario();
        if (c == null) return;

        if (dao.insertar(c)) {

            try {
                xml.generarXML();   // ← AQUÍ, solo si se insertó correctamente
            } catch (Exception e) {
                e.printStackTrace();
            }

            mostrarInfo("Cliente insertado correctamente.");
            limpiarFormulario();
            cargarTodos();
        }
    }

    

    private void buscar() throws SQLException {
        String nif = txtNif.getText().trim();
        if (nif.isEmpty()) { mostrarError("Introduce el NIF para buscar."); return; }
        
            Cliente c = dao.buscarPorNif(nif);
            modelo.setRowCount(0);
            if (c != null) {
                agregarFila(c);
                rellenarCampos(c);
            } else {
                mostrarInfo("No se encontró ningún cliente con NIF: " + nif);
            }
        
    }

    private void actualizar() throws SQLException {

        Cliente c = obtenerClienteFormulario();
        if (c == null) return;

        if (dao.actualizar(c)) {

            try {
                xml.generarXML();   // ← AQUÍ, solo si se actualizó correctamente
            } catch (Exception e) {
                e.printStackTrace();
            }

            mostrarInfo("Cliente actualizado correctamente.");
            cargarTodos();

        } else {
            mostrarInfo("No existe cliente con ese NIF.");
        }
    }


    private void eliminar() throws SQLException {
        String nif = txtNif.getText().trim();
        if (nif.isEmpty()) {
            mostrarError("Introduce el NIF del cliente a eliminar.");
            return;
        }

        int op = DialogosMontealcohol.confirmar(
        	    this,
        	    "¿Eliminar el cliente con NIF " + nif + "?\nSe eliminarán también sus pedidos."
        	);

        	if (op != JOptionPane.YES_OPTION) return;


        if (dao.eliminar(nif)) {

            try {
                xml.generarXML();   // ← AQUÍ, solo si se eliminó correctamente
            } catch (Exception e) {
                e.printStackTrace();
            }

            mostrarInfo("Cliente eliminado correctamente.");
            limpiarFormulario();
            cargarTodos();

        } else {
            mostrarInfo("No existe cliente con ese NIF.");
        }
    }


    private void cargarTodos() throws SQLException{
       
            List<Cliente> lista = dao.listarTodos();
            modelo.setRowCount(0);
            for (Cliente c : lista) agregarFila(c);
        
    }

    // ── Helpers ───────────────────────────────────────────────
    private Cliente obtenerClienteFormulario() {
        String Nif_Cli       = txtNif.getText().trim();
        String Nombre    = txtNombre.getText().trim();
        String Apellido  = txtApellido.getText().trim();
        String Calle     = txtCalle.getText().trim();
        String Numero = txtNumero.getText().trim();   // ← String del formulario
        String Piso      = txtPiso.getText().trim();
        String Localidad = txtLocalidad.getText().trim();
        String Provincia = txtProvincia.getText().trim();
        String Telefono  = txtTelefono.getText().trim();
        String Email     = txtEmail.getText().trim();

        if (Nif_Cli.isEmpty() || Nombre.isEmpty() || Apellido.isEmpty() || Calle.isEmpty()
            || Numero.isEmpty() || Localidad.isEmpty()
            || Provincia.isEmpty()) {
            mostrarError("Todos los campos marcados con * son obligatorios.");
            return null;
        }

        int numero;
        try {
            numero = Integer.parseInt(Numero);
        } catch (NumberFormatException e) {
            mostrarError("El número debe ser un valor numérico.");
            return null;
        }

        return new Cliente(
            Nif_Cli, Nombre, Apellido, Calle, numero, Piso,
            Localidad, Provincia, Telefono,
            Email.isEmpty() ? null : Email
        );
    }
    
 
    private void agregarFila(Cliente c) {
        modelo.addRow(new Object[]{
            c.getNif_Cli(),
            c.getNombre(),
            c.getApellido(),
            c.getCalle(),
            c.getNumero(),
            c.getPiso()     != null ? c.getPiso()     : "",
            c.getLocalidad(),
            c.getProvincia(),
            c.getTelefono() != null ? c.getTelefono() : "",
            c.getEmail() != null ? c.getEmail() : ""
        });
    }

    



    private void rellenarFormulario(int fila) {
        txtNif.setText(valorOVacio(fila, 0));
        txtNombre.setText(valorOVacio(fila, 1));
        txtApellido.setText(valorOVacio(fila, 2));
        txtCalle.setText(valorOVacio(fila, 3));
        txtNumero.setText(valorOVacio(fila, 4));
        txtPiso.setText(valorOVacio(fila, 5));
        txtLocalidad.setText(valorOVacio(fila, 6));
        txtProvincia.setText(valorOVacio(fila, 7));
        txtTelefono.setText(valorOVacio(fila, 8));
        txtEmail.setText(valorOVacio(fila, 9));

        // ⭐ BLOQUEAR NIF
        txtNif.setEditable(false);
    }


    // Método auxiliar para evitar NullPointerException
    private String valorOVacio(int fila, int columna) {
        Object val = modelo.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }
    private void rellenarCampos(Cliente c) {
        txtNif.setText(c.getNif_Cli());
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        txtCalle.setText(c.getCalle());
        txtNumero.setText(String.valueOf(c.getNumero())); // ← CORREGIDO
        txtPiso.setText(c.getPiso());
        txtLocalidad.setText(c.getLocalidad());
        txtProvincia.setText(c.getProvincia());
        txtTelefono.setText(c.getTelefono());
        txtEmail.setText(c.getEmail() != null ? c.getEmail() : "");
    }


    private void limpiarFormulario() {
        for (JTextField tf : new JTextField[]{txtNif, txtNombre, txtApellido, txtCalle,
                txtNumero, txtPiso, txtLocalidad, txtProvincia, txtTelefono, txtEmail}) {
            tf.setText("");
        }

        // ⭐ DESBLOQUEAR NIF
        txtNif.setEditable(true);

        modelo.setRowCount(0);
        tabla.clearSelection();
    }


    // ── Estilos ───────────────────────────────────────────────
    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(MenuPrincipal.COLOR_PRIMARIO);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    private void estilizarCampos(JTextField... campos) {
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

    private void mostrarInfo(String msg)  {
        DialogosMontealcohol.info(this, msg);
    }

    private void mostrarError(String msg) {
        DialogosMontealcohol.error(this, msg);
    }
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
    			limpiarFormulario();
    		
    	 } catch (SQLException ex) {
    		 mostrarError ("proceso fallido");
}
}

	public XMLGenerator getXml() {
		return xml;
	}
}
