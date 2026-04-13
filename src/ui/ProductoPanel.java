
package ui;

import dao.ProductoDAO;
import dao.ProveedorDAO;
import model.Producto;
import model.Proveedor;
import model.Tipo;
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
 * Panel CRUD para la gestión de Productos.
 * Código con formato 1 letra + 4 dígitos.
 * Tipo con JComboBox restringido a los valores del ENUM.
 */
public class ProductoPanel extends PanelMontealcohol implements ActionListener {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// ── GeneradorXML──────────────────────────────────────────
	private final XMLGenerator xml = new XMLGeneratorImpl();
	
	private final ProductoDAO  daoProducto  = new ProductoDAO();
    private final ProveedorDAO daoProveedor = new ProveedorDAO();

    private final String[] COLUMNAS = {"Código", "Nombre", "Precio (€)", "Stock", "Tipo", "NIF Proveedor"};


    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField  txtCodigo    = new JTextField(7);
    private final JTextField  txtPrecio    = new JTextField(8);
    private final JTextField  txtStock     = new JTextField(6);
    private final JTextField txtNombre 	= new JTextField(12);

    private final JComboBox<Tipo> cmbTipo = new JComboBox<>(Tipo.values());
    private final JComboBox<String> cmbProveedor = new JComboBox<>();

    private final JButton btnInsertar  = crearBtn(" Insertar",  new Color(200, 170, 120), "iconos/añadir.png");
    private final JButton btnBuscar    = crearBtn(" Buscar",    new Color(200, 170, 120),"iconos/buscar.png");
    private final JButton btnActualizar= crearBtn(" Actualizar", new Color(200, 170, 120), "iconos/actualizar.png");
    private final JButton btnEliminar  = crearBtn(" Eliminar",  new Color(200, 170, 120), "iconos/borrar.png");
    private final JButton btnListar    = crearBtn(" Listar todos", new Color(200, 170, 120),"iconos/cargarDatos.png");
    private final JButton btnLimpiar   = crearBtn(" Limpiar",   new Color(200, 170, 120), "iconos/limpiar.png");
    private final JButton btnInsertarStockInicial =
            crearBtn(" Insertar con stock inicial", new Color(200, 170, 120), "iconos/añadir.png");


    public ProductoPanel(VentanaMontealcohol ventana) {
        super(ventana);

    	setLayout(new BorderLayout(8, 8));
    	setOpaque(false); // para integrarse con VentanaMontealcohol


        try {
            cargarProveedoresCombo();
            cargarTodos();
        } catch (SQLException ex) {
        	DialogosMontealcohol.error(
        		    this,
        		    "Error al cargar datos: " + ex.getMessage()
        		);


        }

        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(),      BorderLayout.CENTER);
        add(crearBotones(),    BorderLayout.SOUTH);

        configurarEventos();
    }
    private ImageIcon escalarIcono(String ruta, int ancho, int alto) {
        Image img = new ImageIcon(ruta).getImage();
        Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(nueva);
    

}


    // ── Cargar proveedores en combo ───────────────────────────
    private void cargarProveedoresCombo() {
        try {
            List<Proveedor> lista = daoProveedor.listarTodos();
            cmbProveedor.removeAllItems();
            for (Proveedor pv : lista)
                cmbProveedor.addItem(pv.getNif_Prove() + " - " + pv.getNombre());
        } catch (SQLException ex) {
        	DialogosMontealcohol.error(
        		    this,
        		    "Error al cargar proveedores: " + ex.getMessage()
        		);

        }
    }

    // ── Formulario ────────────────────────────────────────────
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

        estilizar(txtCodigo, txtPrecio, txtStock, txtNombre);
        estilizarCombo(cmbTipo);
        estilizarCombo(cmbProveedor);

        // Fila 0
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        p.add(etiqueta("Código * (A0000)"), g);
        g.gridx = 1; g.weightx = 0.5; p.add(txtCodigo, g);

        g.gridx = 2; g.weightx = 0; p.add(etiqueta("Precio (€) *"), g);
        g.gridx = 3; g.weightx = 0.5; p.add(txtPrecio, g);

        g.gridx = 4; g.weightx = 0; p.add(etiqueta("Stock *"), g);
        g.gridx = 5; g.weightx = 0.5; p.add(txtStock, g);

        // Fila 1
     // ✅ CORRECTO
     // Fila 1 - Nombre
     g.gridx = 0; g.gridy = 1; g.weightx = 0; g.gridwidth = 1;
     p.add(etiqueta("Nombre *"), g);
     g.gridx = 1; g.weightx = 1; g.gridwidth = 5;
     p.add(txtNombre, g);

     // Fila 2 - Tipo y Proveedor
     g.gridx = 0; g.gridy = 2; g.weightx = 0; g.gridwidth = 1;
     p.add(etiqueta("Tipo *"), g);
     g.gridx = 1; g.weightx = 1; g.gridwidth = 2;
     p.add(cmbTipo, g);

     g.gridx = 3; g.weightx = 0; g.gridwidth = 1;
     p.add(etiqueta("Proveedor *"), g);
     g.gridx = 4; g.weightx = 1; g.gridwidth = 2;
     p.add(cmbProveedor, g);

        return p;
    }

    // ── Tabla ─────────────────────────────────────────────────
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
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setBackground(MenuPrincipal.COLOR_FONDO);
        for (JButton b : new JButton[]{btnInsertar, btnInsertarStockInicial, btnBuscar,
                btnActualizar, btnEliminar, btnListar, btnLimpiar}) p.add(b);

        return p;
    }

    private void configurarEventos() {
        btnInsertar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnActualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnListar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnInsertarStockInicial.addActionListener(this);

    }

    // ── CRUD ──────────────────────────────────────────────────
    private void insertar() throws SQLException {
        Producto pr = getProducto();
        if (pr == null) return;

        try {
            if (daoProducto.insertar(pr)) {

                try {
                    xml.generarXML();   // ← AQUÍ, solo si se insertó correctamente
                } catch (Exception e) {
                    e.printStackTrace();
                }

                info("Producto insertado correctamente.");
                limpiar();
                cargarTodos();
            }
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void buscar() throws SQLException{
        String cod = txtCodigo.getText().trim();
        if (cod.isEmpty()) { error("Introduce el código del producto."); return; }
        
            Producto pr = daoProducto.buscarPorCodigo(cod);
            modelo.setRowCount(0);
            if (pr != null) { agregarFila(pr); seleccionarProveedor(pr.getNif_Prove()); }
            else info("No se encontró producto con código: " + cod);
        
    }

    private void actualizar() throws SQLException {
        Producto pr = getProducto();
        if (pr == null) return;

        if (daoProducto.actualizar(pr)) {

            try {
                xml.generarXML();   // ← AQUÍ, solo si se actualizó correctamente
            } catch (Exception e) {
                e.printStackTrace();
            }

            info("Producto actualizado.");
            cargarTodos();

        } else {
            info("No existe producto con ese código.");
        }
    }


    private void eliminar() throws SQLException {
        String cod = txtCodigo.getText().trim();
        if (cod.isEmpty()) {
            error("Introduce el código del producto a eliminar.");
            return;
        }

        int op = DialogosMontealcohol.confirmar(
                this,
                "¿Eliminar el producto " + cod + " y todas sus líneas asociadas?"
        );

        if (op != JOptionPane.YES_OPTION) return;

        // LLAMADA AL PROCEDIMIENTO CON CURSOR
        daoProducto.eliminarProductoProcedimiento(cod);

        try {
            xml.generarXML();   // ← solo si se eliminó correctamente
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Producto eliminado correctamente.");
        limpiar();
        cargarTodos();
    }

    private void insertarConStockInicial() throws SQLException {
        String cod     = txtCodigo.getText().trim();
        String nombre  = txtNombre.getText().trim();
        String precio  = txtPrecio.getText().trim();
        Tipo tipoEnum  = (Tipo) cmbTipo.getSelectedItem();
        String nifProv = getNifSeleccionado();

        if (cod.isEmpty() || nombre.isEmpty() || precio.isEmpty()) {
            error("Código, nombre y precio son obligatorios.");
            return;
        }

        double p;
        try {
            p = Double.parseDouble(precio);
            if (p <= 0) {
                error("El precio debe ser mayor que 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            error("El precio debe ser numérico.");
            return;
        }

        daoProducto.crearProductoConStockInicial(
                cod,
                nombre,
                p,
                tipoEnum.getLabel(),
                nifProv
        );

        try {
            xml.generarXML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Producto creado con stock inicial correctamente.");
        limpiar();
        cargarTodos();
    }



    private void cargarTodos()throws SQLException {
        
            List<Producto> lista = daoProducto.listarTodos();
            modelo.setRowCount(0);
            for (Producto pr : lista) agregarFila(pr);
    }

    // ── Helpers ───────────────────────────────────────────────
    private Producto getProducto() {
        String cod     = txtCodigo.getText().trim();
        String nombre  = txtNombre.getText().trim();
        String precio  = txtPrecio.getText().trim();
        String stock   = txtStock.getText().trim();
        Tipo tipoEnum  = (Tipo) cmbTipo.getSelectedItem();  // ← CORREGIDO
        String nifProv = getNifSeleccionado();

        if (cod.isEmpty() || precio.isEmpty() || stock.isEmpty()) {
            error("Código, precio y stock son obligatorios."); return null;
        }
        if (!Producto.codigoValido(cod)) {
            error("El código debe tener formato: 1 letra + 4 dígitos (ej: D0001)"); return null;
        }
        try {
            double p = Double.parseDouble(precio);
            int    s = Integer.parseInt(stock);
            if (p <= 0) { error("El precio debe ser mayor que 0."); return null; }
            if (s < 0)  { error("El stock no puede ser negativo."); return null; }
            return new Producto(cod, nombre, p, s, tipoEnum, nifProv);
        } catch (NumberFormatException ex) {
            error("Precio y stock deben ser valores numéricos."); return null;
        }
    }
    private String getNifSeleccionado() {
        String item = (String) cmbProveedor.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    private void seleccionarProveedor(String nif) {
        for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
            if (cmbProveedor.getItemAt(i).startsWith(nif)) {
                cmbProveedor.setSelectedIndex(i); break;
            }
        }
    }

    private void agregarFila(Producto pr) {
        modelo.addRow(new Object[]{
            pr.getCod_Pro(),
            pr.getNom_Pro(),
            String.format("%.2f", pr.getPrecio_Pro()),
            pr.getStock(),
            pr.getTipo() != null ? pr.getTipo().getLabel() : "",
            pr.getNif_Prove() != null ? pr.getNif_Prove() : ""
        });
    }

    private void rellenarFormulario(int fila) {
        txtCodigo.setText(valorOVacio(fila, 0));
        txtNombre.setText(valorOVacio(fila, 1));
        txtPrecio.setText(valorOVacio(fila, 2));
        txtStock.setText(valorOVacio(fila, 3));
        cmbTipo.setSelectedItem(Tipo.fromLabel(valorOVacio(fila, 4)));  // ← CORREGIDO
        seleccionarProveedor(valorOVacio(fila, 5));
    }

    private String valorOVacio(int fila, int columna) {
        Object val = modelo.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }

    private void limpiar() {
        txtCodigo.setText(""); txtNombre.setText(""); txtPrecio.setText(""); txtStock.setText("");
        cmbTipo.setSelectedIndex(0); cmbProveedor.setSelectedIndex(0);
        modelo.setRowCount(0);  
        tabla.clearSelection();
    }

    // ── Estilos ───────────────────────────────────────────────
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
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
            tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
            tf.setCursor(getVentana().getTextoCur());

        }
    }

    private void estilizarCombo(JComboBox<?> cmb) {
        cmb.setBackground(new Color(50, 35, 16));
        cmb.setForeground(MenuPrincipal.COLOR_TEXTO);
        cmb.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
		else if (e.getSource() == btnInsertarStockInicial)
		    insertarConStockInicial();

				
	 } catch (SQLException ex) {
		 error ("proceso fallido");
}
}


public XMLGenerator getXml() {
	return xml;
}
}
