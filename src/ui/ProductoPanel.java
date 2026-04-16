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
 * Panel CRUD para la gestión de productos dentro del sistema Montealcohol.
 * Permite añadir stock mediante procedimiento almacenado, buscar, eliminar,
 * listar y visualizar información detallada de cada producto.
 *
 * Incluye formulario, tabla, botones de acción, selector de proveedores,
 * visualización de imágenes y generación automática de XML tras cada operación.
 *
 * Extiende {@link PanelMontealcohol} para mantener la estética corporativa
 * y los cursores personalizados.
 *
 * @author Ines
 * @version 1.0
 */
public class ProductoPanel extends PanelMontealcohol implements ActionListener {


    private static final long serialVersionUID = 1L;
    private static final Dimension TAM_BOTON = new Dimension(180, 42);

    private final XMLGenerator xml = new XMLGeneratorImpl();
    private final ProductoDAO daoProducto = new ProductoDAO();
    private final ProveedorDAO daoProveedor = new ProveedorDAO();

    private final String[] COLUMNAS = {"Código", "Nombre", "Precio (€)", "Stock", "Tipo", "NIF Proveedor"};

    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        private static final long serialVersionUID = 1L;
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable tabla = new JTable(modelo);

    private final JTextField txtCodigo = new JTextField(7);
    private final JTextField txtPrecio = new JTextField(8);
    private final JTextField txtNombre = new JTextField(12);

    // ⭐ NUEVO: SPINNER PARA CANTIDAD
    private final JSpinner spnCantidad = new JSpinner(
            new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)
    );

    private final JComboBox<Tipo> cmbTipo = new JComboBox<>(Tipo.values());
    private final JComboBox<String> cmbProveedor = new JComboBox<>();

    // BOTONES
    private final JButton btnInsertar   = crearBtn(" Añadir stock", new Color(200,170,120), "iconos/añadir.png");
    private final JButton btnBuscar     = crearBtn(" Buscar",       new Color(200,170,120), "iconos/buscar.png");
    
    private final JButton btnEliminar   = crearBtn(" Eliminar",     new Color(200,170,120), "iconos/borrar.png");
    private final JButton btnListar     = crearBtn(" Listar todos", new Color(200,170,120), "iconos/cargarDatos.png");
    private final JButton btnLimpiar    = crearBtn(" Limpiar",      new Color(200,170,120), "iconos/limpiar.png");

    /**
     * Construye el panel de gestión de productos, inicializa el formulario,
     * carga proveedores, carga productos existentes y configura la interfaz.
     *
     * @param ventana Ventana principal desde la que se muestra este panel.
     */
    public ProductoPanel(VentanaMontealcohol ventana) {

        super(ventana);

        setLayout(new BorderLayout(8, 8));
        setOpaque(false);

        try {
            cargarProveedoresCombo();
            cargarTodos();
        } catch (SQLException ex) {
            DialogosMontealcohol.error(this, "Error al cargar datos: " + ex.getMessage());
        }

        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);

        configurarEventos();

        estandarizarBotones(
                btnInsertar, btnBuscar, 
                btnEliminar, btnListar, btnLimpiar
        );
    }

    /**
     * Carga una imagen desde la ruta indicada y la escala al tamaño especificado.
     *
     * @param ruta  Ruta del archivo de imagen.
     * @param ancho Ancho deseado.
     * @param alto  Alto deseado.
     * @return Icono escalado.
     */
    private ImageIcon escalarIcono(String ruta, int ancho, int alto) {

        Image img = new ImageIcon(ruta).getImage();
        Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(nueva);
    }

    /**
     * Carga todos los proveedores desde la base de datos y los inserta en el
     * combo de selección con formato "NIF - Nombre".
     */
    private void cargarProveedoresCombo() {

        try {
            List<Proveedor> lista = daoProveedor.listarTodos();
            cmbProveedor.removeAllItems();
            for (Proveedor pv : lista)
                cmbProveedor.addItem(pv.getNif_Prove() + " - " + pv.getNombre());
        } catch (SQLException ex) {
            DialogosMontealcohol.error(this, "Error al cargar proveedores: " + ex.getMessage());
        }
    }

    /**
     * Construye el formulario de entrada para productos, incluyendo campos de
     * código, nombre, precio, tipo, proveedor y cantidad a añadir.
     *
     * @return Panel del formulario configurado.
     */
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
        g.fill = GridBagConstraints.HORIZONTAL;

        estilizar(txtCodigo, txtPrecio, txtNombre);
        estilizarCombo(cmbTipo);
        estilizarCombo(cmbProveedor);

        // Fila 0
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        p.add(etiqueta("Código * (A0000)"), g);
        g.gridx = 1; g.weightx = 0.5; p.add(txtCodigo, g);

        g.gridx = 2; g.weightx = 0; p.add(etiqueta("Precio (€) *"), g);
        g.gridx = 3; g.weightx = 0.5; p.add(txtPrecio, g);

        g.gridx = 4; g.weightx = 0; p.add(etiqueta("Cantidad a añadir *"), g);
        g.gridx = 5; g.weightx = 0.5; p.add(spnCantidad, g);

        // ⭐ Estilizar el editor del spinner para que coincida con los JTextField
        JComponent editor = spnCantidad.getEditor();
        JFormattedTextField txtSpin = ((JSpinner.DefaultEditor) editor).getTextField();

        txtSpin.setBackground(new Color(50, 35, 16));
        txtSpin.setForeground(MenuPrincipal.COLOR_TEXTO);
        txtSpin.setCaretColor(MenuPrincipal.COLOR_PRIMARIO);
        txtSpin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 80, 30)),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        txtSpin.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSpin.setCursor(getVentana().getTextoCur());


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

    /**
     * Construye la tabla de productos con estilo corporativo y añade listeners
     * para rellenar el formulario y mostrar imágenes al hacer clic en el nombre.
     *
     * @return JScrollPane con la tabla configurada.
     */
    private JScrollPane crearTabla() {

        tabla.setBackground(new Color(40, 28, 15));
        tabla.setForeground(MenuPrincipal.COLOR_TEXTO);
        tabla.setGridColor(new Color(80, 60, 30));
        tabla.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setRowHeight(24);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0)
                rellenarFormulario(tabla.getSelectedRow());
        });

     // ⭐ Mostrar imagen SOLO al hacer clic en el NOMBRE (columna 1)
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.getSelectedRow();
                int columna = tabla.getSelectedColumn();

                if (fila >= 0 && columna == 1) {   // SOLO nombre
                    String codProducto = modelo.getValueAt(fila, 0).toString();
                    mostrarDialogoImagenProducto(codProducto);
                }
            }
        });



        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        sp.getViewport().setBackground(new Color(40, 28, 15));
        return sp;
    }

    /**
     * Construye el panel que contiene los botones CRUD del módulo.
     *
     * @return Panel con los botones de acción.
     */
    private JPanel crearBotones() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setBackground(MenuPrincipal.COLOR_FONDO);

        for (JButton b : new JButton[]{
                btnInsertar, btnBuscar, 
                btnEliminar, btnListar, btnLimpiar
        }) p.add(b);

        return p;
    }

    /**
     * Asocia los botones del panel a sus respectivos eventos de acción.
     */
    private void configurarEventos() {

        btnInsertar.addActionListener(this);
        btnBuscar.addActionListener(this);
        
        btnEliminar.addActionListener(this);
        btnListar.addActionListener(this);
        btnLimpiar.addActionListener(this);
    }

    // ⭐⭐⭐ AÑADIR STOCK (INSERTAR)
    /**
     * Añade stock a un producto utilizando el procedimiento almacenado
     * AÑADIR_PRODUCTO. Genera XML tras la operación y recarga la tabla.
     *
     * @throws SQLException Si ocurre un error durante la operación.
     */
    private void añadirStock() throws SQLException {

        String cod = txtCodigo.getText().trim();
        int cantidad = (int) spnCantidad.getValue();

        if (cod.isEmpty()) {
            error("Debes introducir el código del producto.");
            return;
        }

        String mensaje = daoProducto.añadirProductoProcedimiento(cod, cantidad);

        try {
            xml.generarXML();   // ⭐ GENERAR XML SIEMPRE QUE SE MODIFICA BD
        } catch (Exception e) {
            e.printStackTrace();
        }

        info(mensaje);
        cargarTodos();
    }


    /**
     * Busca un producto por su código y muestra el resultado en la tabla.
     *
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    private void buscar() throws SQLException {

        String cod = txtCodigo.getText().trim();
        if (cod.isEmpty()) { error("Introduce el código del producto."); return; }

        Producto pr = daoProducto.buscarPorCodigo(cod);
        modelo.setRowCount(0);
        if (pr != null) { agregarFila(pr); seleccionarProveedor(pr.getNif_Prove()); }
        else info("No se encontró producto con código: " + cod);
    }

    /**
     * Actualiza los datos de un producto existente. No modifica el stock desde
     * el formulario, solo precio, nombre, tipo y proveedor.
     *
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    private void actualizar() throws SQLException {

        Producto pr = getProducto();
        if (pr == null) return;

        if (daoProducto.actualizar(pr)) {
            try { xml.generarXML(); } catch (Exception e) { e.printStackTrace(); }
            info("Producto actualizado.");
            cargarTodos();
        } else {
            info("No existe producto con ese código.");
        }
    }

    /**
     * Elimina un producto utilizando el procedimiento almacenado correspondiente.
     * Solicita confirmación al usuario, genera XML y recarga la tabla.
     *
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
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

        daoProducto.eliminarProductoProcedimiento(cod);

        try { xml.generarXML(); } catch (Exception e) { e.printStackTrace(); }

        info("Producto eliminado correctamente.");
        limpiar();
        cargarTodos();
    }

    /**
     * Carga todos los productos desde la base de datos y los muestra en la tabla.
     *
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    private void cargarTodos() throws SQLException {

        List<Producto> lista = daoProducto.listarTodos();
        modelo.setRowCount(0);
        for (Producto pr : lista) agregarFila(pr);
    }

    // ⭐⭐⭐ getProducto() YA NO USA STOCK DEL FORMULARIO
    /**
     * Construye un objeto Producto a partir de los datos del formulario.
     * No modifica el stock: conserva el stock actual mostrado en la tabla.
     *
     * @return Producto construido o null si hay errores de validación.
     */
    private Producto getProducto() {

        String cod = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precio = txtPrecio.getText().trim();
        Tipo tipoEnum = (Tipo) cmbTipo.getSelectedItem();
        String nifProv = getNifSeleccionado();

        if (cod.isEmpty() || precio.isEmpty()) {
            error("Código y precio son obligatorios.");
            return null;
        }

        if (!Producto.codigoValido(cod)) {
            error("El código debe tener formato: 1 letra + 4 dígitos (ej: D0001)");
            return null;
        }

        try {
            float p = Float.parseFloat(precio);
            if (p <= 0) {
                error("El precio debe ser mayor que 0.");
                return null;
            }

            // ⭐ El stock NO se toca aquí
            // Se mantiene el que ya tiene el producto en la tabla
            int stockActual = 0;
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                stockActual = Integer.parseInt(modelo.getValueAt(fila, 3).toString());
            }

            return new Producto(cod, nombre, p, stockActual, tipoEnum, nifProv);

        } catch (NumberFormatException ex) {
            error("El precio debe ser numérico.");
            return null;
        }
    }

    /**
     * Obtiene el NIF del proveedor seleccionado en el combo.
     *
     * @return NIF del proveedor o cadena vacía si no hay selección.
     */
    private String getNifSeleccionado() {

        String item = (String) cmbProveedor.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    /**
     * Selecciona en el combo el proveedor cuyo NIF coincide con el indicado.
     *
     * @param nif NIF del proveedor a seleccionar.
     */
    private void seleccionarProveedor(String nif) {

        for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
            if (cmbProveedor.getItemAt(i).startsWith(nif)) {
                cmbProveedor.setSelectedIndex(i); break;
            }
        }
    }

    /**
     * Añade una fila a la tabla con los datos del producto indicado.
     *
     * @param pr Producto cuyos datos se insertarán en la tabla.
     */
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

    /**
     * Rellena el formulario con los datos de la fila seleccionada en la tabla.
     * El campo código se bloquea y se aplica estilo no editable.
     *
     * @param fila Índice de la fila seleccionada.
     */
    private void rellenarFormulario(int fila) {

        txtCodigo.setText(valorOVacio(fila, 0));
        txtNombre.setText(valorOVacio(fila, 1));
        txtPrecio.setText(valorOVacio(fila, 2));
        spnCantidad.setValue(1);
        cmbTipo.setSelectedItem(Tipo.fromLabel(valorOVacio(fila, 4)));
        seleccionarProveedor(valorOVacio(fila, 5));

     // ⭐ BLOQUEAR CÓDIGO + ESTILO NO EDITABLE
        txtCodigo.setEditable(false);
        estilizarNoEditable(txtCodigo);

    }

    /**
     * Devuelve el valor de una celda o una cadena vacía si es null.
     *
     * @param fila    Fila de la tabla.
     * @param columna Columna de la tabla.
     * @return Valor de la celda o "" si es null.
     */
    private String valorOVacio(int fila, int columna) {

        Object val = modelo.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }

    /**
     * Limpia todos los campos del formulario, restablece el estado editable del
     * código y reinicia la tabla y la selección.
     */
    private void limpiar() {

        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        spnCantidad.setValue(1);
        cmbTipo.setSelectedIndex(0);
        cmbProveedor.setSelectedIndex(0);

        // ⭐ DESBLOQUEAR CÓDIGO
        txtCodigo.setEditable(true);
        estilizar(txtCodigo);   // vuelve al estilo editable normal


        modelo.setRowCount(0);
        tabla.clearSelection();
    }

    /**
     * Crea una etiqueta con estilo corporativo Montealcohol.
     *
     * @param t Texto de la etiqueta.
     * @return JLabel configurado.
     */
    private JLabel etiqueta(String t) {

        JLabel l = new JLabel(t);
        l.setForeground(MenuPrincipal.COLOR_PRIMARIO);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    /**
     * Aplica el estilo Montealcohol a los campos de texto del formulario.
     *
     * @param campos Campos a estilizar.
     */
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

    /**
     * Aplica estilo corporativo a un JComboBox.
     *
     * @param cmb Combo a estilizar.
     */
    private void estilizarCombo(JComboBox<?> cmb) {

        cmb.setBackground(new Color(50, 35, 16));
        cmb.setForeground(MenuPrincipal.COLOR_TEXTO);
        cmb.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    /**
     * Crea un botón estilizado con icono opcional y cursor personalizado.
     *
     * @param texto     Texto del botón.
     * @param bg        Color de fondo.
     * @param rutaIcono Ruta del icono o null si no se desea icono.
     * @return Botón configurado.
     */
    private JButton crearBtn(String texto, Color bg, String rutaIcono) {

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
    /**
     * Muestra un diálogo informativo con estilo Montealcohol.
     *
     * @param m Mensaje a mostrar.
     */
    private void info(String m) {}

    /**
     * Muestra un diálogo de error con estilo Montealcohol.
     *
     * @param m Mensaje a mostrar.
     */
    private void error(String m) {}


    /**
     * Aplica un tamaño estándar a los botones recibidos.
     *
     * @param botones Botones a estandarizar.
     */
    private void estandarizarBotones(JButton... botones) {

        for (JButton b : botones) {
            b.setPreferredSize(TAM_BOTON);
        }
        }
    /**
     * Muestra un diálogo modal con la imagen del producto indicado.
     * Busca primero .png y luego .jpg. Si no existe, muestra advertencia.
     *
     * @param codProducto Código del producto cuya imagen se desea mostrar.
     */
    private void mostrarDialogoImagenProducto(String codProducto) {


        // Probar primero con .png
        String rutaPng = "img/" + codProducto + ".png";
        ImageIcon icono = new ImageIcon(rutaPng);

        // Si no existe, probar con .jpg
        if (icono.getIconWidth() == -1) {
            String rutaJpg = "img/" + codProducto + ".jpg";
            icono = new ImageIcon(rutaJpg);

            if (icono.getIconWidth() == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró la imagen del producto " + codProducto +
                                " en img/" + codProducto + ".png ni en img/" + codProducto + ".jpg",
                        "Imagen no disponible",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        // Escalar imagen
        Image img = icono.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
        ImageIcon iconoEscalado = new ImageIcon(img);

        // Crear diálogo
        JDialog dialogo = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Imagen del producto " + codProducto,
                true
        );

        dialogo.setLayout(new BorderLayout());
        dialogo.add(new JLabel(iconoEscalado), BorderLayout.CENTER);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    /**
     * Gestiona los eventos de los botones del panel, ejecutando la operación
     * correspondiente según el origen del evento.
     *
     * @param e Evento de acción generado por un botón.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getSource() == btnInsertar)
                añadirStock();  // ⭐ INSERTAR = AÑADIR STOCK

            else if (e.getSource() == btnBuscar)
                buscar();

            

            else if (e.getSource() == btnEliminar)
                eliminar();

            else if (e.getSource() == btnListar)
                cargarTodos();

            else if (e.getSource() == btnLimpiar)
                limpiar();

        } catch (SQLException ex) {
            error("Proceso fallido");
        }
    }

    /**
     * Devuelve la instancia del generador XML asociada al panel.
     *
     * @return Implementación de {@link XMLGenerator}.
     */
    public XMLGenerator getXml() {

        return xml;
    }
}
