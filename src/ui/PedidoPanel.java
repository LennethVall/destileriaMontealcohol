package ui;

import dao.ClienteDAO;
import dao.PedidoDAO;
import dao.ProductoDAO;
import model.Cliente;
import model.Pedido;
import model.LineaPedido;
import model.Producto;
import service.XMLGenerator;
import service.XMLGeneratorImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de interfaz gráfica que gestiona el CRUD completo de pedidos.
 * <p>
 * Muestra la cabecera del pedido (fechas, precio total y cliente), una tabla
 * de líneas de producto editable con checkbox de selección, y la tabla principal
 * con todos los pedidos. Incluye validaciones de fecha, precio y stock, además
 * de regenerar el XML tras cada operación de escritura.
 * </p>
 *
 * @author Alvaro
 * @version 1.0
 */
public class PedidoPanel extends PanelMontealcohol implements ActionListener {

    /** Identificador de versión para la serialización del componente Swing. */
    private static final long serialVersionUID = 1L;

    /** Dimensión estándar aplicada a todos los botones del panel. */
    private static final Dimension TAM_BOTON = new Dimension(180, 42);

    /** Instancia del generador de XML que se invoca tras cada operación CRUD. */
    private final XMLGenerator xml = new XMLGeneratorImpl();

    /** Formateador de fechas que trabaja con el formato {@code dd/MM/yyyy}. */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** DAO para operaciones sobre pedidos. */
    private final PedidoDAO   daoPedido   = new PedidoDAO();

    /** DAO para operaciones sobre clientes, usado para poblar el combo. */
    private final ClienteDAO  daoCliente  = new ClienteDAO();

    /** DAO para operaciones sobre productos, usado para poblar el combo y gestionar el stock. */
    private final ProductoDAO daoProducto = new ProductoDAO();

    /** Definición de las columnas que se muestran en la tabla principal de pedidos. */
    private final String[] COL_PEDIDOS = {"No Pedido", "F. Pedido", "F. Entrega",
                                           "Precio Total (EUR)", "NIF Cliente"};

    /**
     * Modelo de datos de la tabla principal de pedidos con celdas no editables.
     * Sobreescribe {@code isCellEditable} para impedir la edición directa.
     */
    private final DefaultTableModel modeloPedidos = new DefaultTableModel(COL_PEDIDOS, 0) {
        private static final long serialVersionUID = 1L;

        /**
         * Impide que el usuario edite directamente las celdas de la tabla de pedidos.
         *
         * @param r Índice de fila.
         * @param c Índice de columna.
         * @return {@code false} siempre.
         */
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    /** Tabla visual que muestra el listado de pedidos. */
    private final JTable tablaPedidos = new JTable(modeloPedidos);

    /** Definición de las columnas de la tabla de líneas del pedido. */
    private final String[] COL_LINEAS = {"Check", "Codigo Producto", "Cantidad", "Precio Linea (EUR)"};

    /**
     * Modelo de datos de la tabla de líneas del pedido.
     * Solo la columna 0 (checkbox) es editable; el resto son de solo lectura.
     */
    private final DefaultTableModel modeloLineas = new DefaultTableModel(COL_LINEAS, 0) {

        /**
         * Devuelve {@link Boolean} para la columna del checkbox y {@link Object}
         * para el resto, de modo que Swing renderice correctamente el check.
         *
         * @param columnIndex Índice de la columna.
         * @return Tipo de la clase correspondiente a la columna indicada.
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : Object.class;
        }

        /**
         * Permite la edición únicamente en la columna del checkbox (columna 0).
         *
         * @param row Índice de fila.
         * @param col Índice de columna.
         * @return {@code true} solo si {@code col == 0}.
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0;
        }
    };

    /** Tabla visual que muestra las líneas del pedido seleccionado. */
    private final JTable tablaLineas = new JTable(modeloLineas);

    /** Campo de texto para el número de pedido (solo lectura, generado por la BD). */
    private final JTextField  txtNumero      = new JTextField(6);

    /** Campo de texto para la fecha de realización del pedido en formato {@code dd/MM/yyyy}. */
    private final JTextField  txtFechaPedido = new JTextField(10);

    /** Campo de texto para la fecha de entrega del pedido en formato {@code dd/MM/yyyy}. */
    private final JTextField  txtFechaEntrega= new JTextField(10);

    /** Campo de texto para el precio total del pedido (solo lectura, calculado automáticamente). */
    private final JTextField  txtPrecioTotal = new JTextField(10);

    /** ComboBox para seleccionar el cliente asociado al pedido. */
    private final JComboBox<String> cmbCliente = new JComboBox<>();

    /** ComboBox para seleccionar el producto a incluir en una nueva línea. */
    private final JComboBox<String> cmbProducto = new JComboBox<>();

    /** Campo de texto para introducir la cantidad del producto de la línea. */
    private final JTextField txtCantidad    = new JTextField(5);

    /** Campo de texto que muestra el precio calculado de la línea (solo lectura). */
    private final JTextField txtPrecioLinea = new JTextField(8);

    /** Lista en memoria que almacena las líneas del pedido mientras se edita en el formulario. */
    private final List<LineaPedido> lineasActuales = new ArrayList<>();

    /** Botón para insertar un nuevo pedido en la base de datos. */
    private final JButton btnInsertar  = crearBtn(" Insertar",  new Color(200, 170, 120), "iconos/anadir.png");

    /** Botón para buscar un pedido por su número. */
    private final JButton btnBuscar    = crearBtn(" Buscar",    new Color(200, 170, 120), "iconos/buscar.png");

    /** Botón para actualizar el pedido seleccionado mediante el procedimiento almacenado. */
    private final JButton btnActualizar= crearBtn(" Actualizar", new Color(200, 170, 120), "iconos/actualizar.png");

    /** Botón para eliminar el pedido indicado por número. */
    private final JButton btnEliminar  = crearBtn(" Eliminar",  new Color(200, 170, 120), "iconos/borrar.png");

    /** Botón para listar todos los pedidos de la base de datos. */
    private final JButton btnListar    = crearBtn(" Listar",    new Color(200, 170, 120), "iconos/cargarDatos.png");

    /** Botón para limpiar todos los campos del formulario y restablecer el estado inicial. */
    private final JButton btnLimpiar   = crearBtn(" Limpiar",   new Color(200, 170, 120), "iconos/limpiar.png");

    /** Botón para añadir una nueva línea de producto al pedido. */
    private final JButton btnAddLinea  = crearBtn("Anadir linea",  new Color(200, 170, 120), "iconos/ok.png");

    /** Botón para eliminar la línea seleccionada del pedido y devolver su stock. */
    private final JButton btnDelLinea  = crearBtn("Quitar linea",  new Color(200, 170, 120), "iconos/error.png");

    /** Botón para aplicar un descuento del 10% a las líneas marcadas con 6 o más unidades. */
    private final JButton btnDescuento = crearBtn("Descuento",     new Color(200, 170, 120), "iconos/descuento.png");

    {
        btnDescuento.setPreferredSize(new Dimension(120, 36));
    }

    /**
     * Constructor que ensambla todos los componentes del panel y registra los eventos.
     * <p>
     * Carga los combos de clientes y productos, construye los subpaneles de cabecera,
     * líneas y tabla principal, configura los listeners y carga todos los pedidos al arrancar.
     * </p>
     *
     * @param ventana Referencia a la ventana principal {@link VentanaMontealcohol}.
     */
    public PedidoPanel(VentanaMontealcohol ventana) {
        super(ventana);

        setLayout(new BorderLayout(6, 6));
        setOpaque(false);

        cargarCombos();

        JPanel norte = new JPanel(new BorderLayout(6, 6));
        norte.setBackground(MenuPrincipal.COLOR_FONDO);
        norte.add(crearPanelCabecera(), BorderLayout.NORTH);
        norte.add(crearPanelLineas(),   BorderLayout.CENTER);

        add(norte,              BorderLayout.NORTH);
        add(crearTablaPedidos(), BorderLayout.CENTER);
        add(crearBotones(),     BorderLayout.SOUTH);

        configurarEventos();

        estandarizarBotones(
            btnInsertar, btnBuscar, btnActualizar,
            btnEliminar, btnListar, btnLimpiar,
            btnAddLinea, btnDelLinea, btnDescuento
        );

        // Recalcula el precio de la línea cuando cambia el producto seleccionado
        cmbProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recalcularPrecioLinea();
            }
        });

        // Recalcula el precio de la línea cada vez que el usuario modifica la cantidad
        txtCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
        });

        try {
            cargarTodos();
        } catch (SQLException ex) {
            DialogosMontealcohol.error(
                this,
                "Error al cargar los pedidos: " + ex.getMessage());
        }
    }

    /**
     * Escala un icono de imagen al tamaño indicado para usarlo en los botones.
     *
     * @param ruta  Ruta relativa al fichero de imagen del icono.
     * @param ancho Anchura deseada en píxeles.
     * @param alto  Altura deseada en píxeles.
     * @return {@link ImageIcon} con la imagen escalada al tamaño indicado.
     */
    private ImageIcon escalarIcono(String ruta, int ancho, int alto) {
        Image img = new ImageIcon(ruta).getImage();
        Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(nueva);
    }

    /**
     * Construye y devuelve el panel con los campos de la cabecera del pedido.
     * <p>
     * Incluye número de pedido (bloqueado), fechas de pedido y entrega,
     * precio total (bloqueado) y selector de cliente.
     * </p>
     *
     * @return {@link JPanel} configurado con el formulario de cabecera del pedido.
     */
    private JPanel crearPanelCabecera() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);

        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO),
                " Datos del Pedido ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                MenuPrincipal.COLOR_PRIMARIO
            ),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        estilizar(txtNumero, txtFechaPedido, txtFechaEntrega, txtPrecioTotal);
        estilizarCombo(cmbCliente);

        // El número de pedido es generado por la BD, no editable
        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);

        // El precio total se calcula automáticamente, no editable
        txtPrecioTotal.setEditable(false);
        estilizarNoEditable(txtPrecioTotal);

        // Fila 0: número de pedido, fecha de pedido y fecha de entrega
        g.gridx=0; g.gridy=0; g.weightx=0; p.add(etiqueta("No Pedido"),        g);
        g.gridx=1; g.weightx=0.3;           p.add(txtNumero,                    g);
        g.gridx=2; g.weightx=0;             p.add(etiqueta("F. Pedido *"),       g);
        g.gridx=3; g.weightx=0.5;           p.add(txtFechaPedido,               g);
        g.gridx=4; g.weightx=0;             p.add(etiqueta("F. Entrega *"),      g);
        g.gridx=5; g.weightx=0.5;           p.add(txtFechaEntrega,              g);

        // Fila 1: precio total y selector de cliente
        g.gridx=0; g.gridy=1; g.weightx=0; p.add(etiqueta("Precio Total (EUR) *"), g);
        g.gridx=1; g.weightx=0.4;           p.add(txtPrecioTotal,               g);
        g.gridx=2; g.weightx=0;             p.add(etiqueta("Cliente *"),         g);
        g.gridx=3; g.weightx=1; g.gridwidth=3; p.add(cmbCliente,                g);

        return p;
    }

    /**
     * Construye y devuelve el panel con el formulario y la tabla de líneas del pedido.
     * <p>
     * Incluye los controles para seleccionar producto, cantidad y precio de línea,
     * así como los botones de añadir, quitar línea y aplicar descuento.
     * </p>
     *
     * @return {@link JPanel} configurado con el formulario y la tabla de líneas.
     */
    private JPanel crearPanelLineas() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);

        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO),
                " Lineas del Pedido ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                MenuPrincipal.COLOR_PRIMARIO
            ),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JPanel fLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fLinea.setBackground(MenuPrincipal.COLOR_BOTON_BG);
        fLinea.setPreferredSize(new Dimension(0, 90));

        estilizar(txtCantidad, txtPrecioLinea);
        estilizarCombo(cmbProducto);

        // El precio de línea es calculado automáticamente, no editable
        txtPrecioLinea.setEditable(false);
        estilizarNoEditable(txtPrecioLinea);

        fLinea.add(etiqueta("Producto:"));    fLinea.add(cmbProducto);
        fLinea.add(etiqueta("Cantidad:"));    fLinea.add(txtCantidad);
        fLinea.add(etiqueta("Precio linea:")); fLinea.add(txtPrecioLinea);
        fLinea.add(btnAddLinea);
        fLinea.add(btnDelLinea);
        fLinea.add(btnDescuento);

        tablaLineas.setBackground(new Color(40, 28, 15));
        tablaLineas.setForeground(MenuPrincipal.COLOR_TEXTO);
        tablaLineas.setGridColor(new Color(80, 60, 30));
        tablaLineas.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tablaLineas.setSelectionForeground(Color.BLACK);
        tablaLineas.setRowHeight(22);
        tablaLineas.getTableHeader().setBackground(MenuPrincipal.COLOR_BOTON_BG);
        tablaLineas.getTableHeader().setForeground(MenuPrincipal.COLOR_PRIMARIO);
        tablaLineas.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        JScrollPane spLineas = new JScrollPane(tablaLineas);
        spLineas.setPreferredSize(new Dimension(0, 100));
        spLineas.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        spLineas.getViewport().setBackground(new Color(40, 28, 15));

        p.add(fLinea,   BorderLayout.NORTH);
        p.add(spLineas, BorderLayout.CENTER);
        return p;
    }

    /**
     * Construye y devuelve el scroll con la tabla principal de pedidos.
     * <p>
     * Registra un listener de selección que carga automáticamente los datos
     * del pedido seleccionado en el formulario.
     * </p>
     *
     * @return {@link JScrollPane} que contiene la tabla principal de pedidos.
     */
    private JScrollPane crearTablaPedidos() {
        tablaPedidos.setBackground(new Color(40, 28, 15));
        tablaPedidos.setForeground(MenuPrincipal.COLOR_TEXTO);
        tablaPedidos.setGridColor(new Color(80, 60, 30));
        tablaPedidos.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tablaPedidos.setSelectionForeground(Color.BLACK);
        tablaPedidos.setRowHeight(24);
        tablaPedidos.getTableHeader().setBackground(MenuPrincipal.COLOR_BOTON_BG);
        tablaPedidos.getTableHeader().setForeground(MenuPrincipal.COLOR_PRIMARIO);
        tablaPedidos.getTableHeader().setFont(MenuPrincipal.FUENTE_BTN);

        // Al seleccionar una fila, carga el pedido correspondiente en el formulario
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPedidos.getSelectedRow() >= 0)
                cargarPedidoEnFormulario(tablaPedidos.getSelectedRow());
        });

        JScrollPane sp = new JScrollPane(tablaPedidos);
        sp.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        sp.getViewport().setBackground(new Color(40, 28, 15));
        return sp;
    }

    /**
     * Construye y devuelve el panel inferior con los botones principales del CRUD,
     * dispuestos en una fila horizontal.
     *
     * @return {@link JPanel} con los seis botones principales alineados en fila.
     */
    private JPanel crearBotones() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_FONDO);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.gridy = 0;

        JButton[] botones = {
            btnInsertar, btnBuscar, btnActualizar,
            btnEliminar, btnListar, btnLimpiar
        };

        for (int i = 0; i < botones.length; i++) {
            g.gridx = i;
            p.add(botones[i], g);
        }

        return p;
    }

    /**
     * Registra los listeners de acción en todos los botones del panel.
     */
    private void configurarEventos() {
        btnInsertar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnActualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnListar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnAddLinea.addActionListener(this);
        btnDelLinea.addActionListener(this);
        btnDescuento.addActionListener(this);
    }

    /**
     * Añade una nueva línea al pedido tras validar producto, cantidad y actualizar el stock.
     * <p>
     * Comprueba que la cantidad sea mayor que cero, descuenta el stock del producto
     * mediante el DAO y actualiza la tabla visual y la lista en memoria.
     * </p>
     */
    private void addLinea() {
        String cod  = getNifProductoSeleccionado();
        String cant = txtCantidad.getText().trim();

        if (cod.isEmpty() || cant.isEmpty()) {
            error("Selecciona producto y cantidad.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cant);

            if (cantidad <= 0) {
                error("La cantidad debe ser mayor que 0.");
                return;
            }

            daoProducto.restarStock(cod, cantidad);

            float precioUnit = getPrecioProductoSeleccionado();
            float precioLinea = cantidad * precioUnit;

            LineaPedido lp = new LineaPedido(0, cod, cantidad, precioLinea);
            lineasActuales.add(lp);

            modeloLineas.addRow(new Object[]{
                false,
                cod,
                cantidad,
                String.format("%.2f", precioLinea)
            });

            txtCantidad.setText("");
            txtPrecioLinea.setText("");
            recalcularPrecioTotalPedido();

        } catch (Exception ex) {
            error("Error al anadir linea o actualizar stock.");
        }
    }

    /**
     * Elimina la línea seleccionada en la tabla de líneas y devuelve el stock al producto.
     * <p>
     * Si no hay ninguna fila seleccionada, muestra un mensaje de error.
     * </p>
     */
    private void delLinea() {
        int fila = tablaLineas.getSelectedRow();

        if (fila < 0) { error("Selecciona una linea para eliminar."); return; }

        String cod = modeloLineas.getValueAt(fila, 1).toString();
        int cantidad = Integer.parseInt(modeloLineas.getValueAt(fila, 2).toString());

        try {
            daoProducto.sumarStock(cod, cantidad);
        } catch (Exception ex) {
            error("Error al devolver stock.");
            return;
        }

        lineasActuales.remove(fila);
        modeloLineas.removeRow(fila);
        recalcularPrecioTotalPedido();
    }

    // ── CRUD ──────────────────────────────────────────────────

    /**
     * Valida los datos del formulario, inserta el pedido en la base de datos
     * y regenera el XML para mantenerlo sincronizado.
     *
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    private void insertar() throws SQLException {
        Pedido p = getPedido();
        if (p == null) return;

        int num = daoPedido.insertar(p);

        try {
            xml.generarXML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Pedido insertado con numero: " + num);
        limpiar();
        cargarTodos();
    }

    /**
     * Busca un pedido por su número y lo muestra en la tabla principal.
     * Muestra un mensaje informativo si no existe ningún pedido con ese número.
     *
     * @throws SQLException Si ocurre un error durante la búsqueda.
     */
    private void buscar() throws SQLException {
        String num = txtNumero.getText().trim();

        if (num.isEmpty()) { error("Introduce el numero de pedido."); return; }

        try {
            Pedido p = daoPedido.buscarPorNumero(Integer.parseInt(num));
            modeloPedidos.setRowCount(0);

            if (p != null) { agregarFilaPedido(p); mostrarLineas(p); }
            else info("No se encontro pedido con numero: " + num);

        } catch (NumberFormatException ex) {
            error("El numero de pedido debe ser entero.");
        }
    }

    /**
     * Valida los datos del formulario y actualiza el pedido en la base de datos
     * mediante el procedimiento almacenado {@code MODIFICAR_PEDIDO}.
     * Regenera el XML tras la actualización.
     *
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    private void actualizar() throws SQLException {
        Pedido p = getPedido();
        if (p == null) return;

        if (p.getNum_Pedido() == 0) {
            error("Selecciona un pedido existente para actualizar.");
            return;
        }

        String listaPro = daoPedido.generarListaProductos(p);
        String listaCan = daoPedido.generarListaCantidades(p);

        daoPedido.modificarPedidoProcedimiento(
                p.getNum_Pedido(),
                listaPro,
                listaCan,
                java.sql.Date.valueOf(p.getFecha_ped()),
                java.sql.Date.valueOf(p.getFecha_ent())
        );

        try {
            xml.generarXML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Pedido modificado correctamente.");
        cargarTodos();
    }

    /**
     * Solicita confirmación al usuario y elimina el pedido indicado por número.
     * Regenera el XML si el pedido se elimina correctamente.
     *
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    private void eliminar() throws SQLException {
        String num = txtNumero.getText().trim();

        if (num.isEmpty()) {
            error("Introduce el numero de pedido a eliminar.");
            return;
        }

        int op = DialogosMontealcohol.confirmar(
            this,
            "Eliminar el pedido no " + num + " y todas sus lineas?"
        );

        if (op != JOptionPane.YES_OPTION) return;

        try {
            if (daoPedido.eliminar(Integer.parseInt(num))) {

                try {
                    xml.generarXML();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                info("Pedido eliminado.");
                limpiar();
                cargarTodos();

            } else {
                info("No existe pedido con ese numero.");
            }
        } catch (NumberFormatException ex) {
            error("Numero de pedido invalido.");
        }
    }

    /**
     * Aplica un descuento del 10% a las líneas del pedido que tengan el checkbox
     * marcado y una cantidad igual o superior a 6 unidades.
     * Actualiza tanto la tabla visual como la lista en memoria y recalcula el total.
     */
    private void aplicarDescuento() {
        for (int i = 0; i < modeloLineas.getRowCount(); i++) {

            Boolean marcado = (Boolean) modeloLineas.getValueAt(i, 0);
            if (marcado == null || !marcado) continue;

            int cantidad = Integer.parseInt(modeloLineas.getValueAt(i, 2).toString());
            String precioStr = modeloLineas.getValueAt(i, 3).toString().replace(",", ".");
            float precioLinea = Float.parseFloat(precioStr);

            if (cantidad >= 6) {
                float nuevoPrecio = precioLinea * 0.90f;
                modeloLineas.setValueAt(String.format("%.2f", nuevoPrecio).replace(".", ","), i, 3);
                lineasActuales.get(i).setPrecio_Total(nuevoPrecio);
            }
        }

        recalcularPrecioTotalPedido();
        info("Descuento aplicado a las lineas marcadas.");
    }

    /**
     * Carga todos los pedidos de la base de datos y los muestra en la tabla principal.
     *
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    private void cargarTodos() throws SQLException {
        List<Pedido> lista = daoPedido.listarTodos();
        modeloPedidos.setRowCount(0);
        for (Pedido p : lista) agregarFilaPedido(p);
    }

    /**
     * Construye y valida un objeto {@link Pedido} a partir de los datos introducidos
     * en el formulario, comprobando fechas, precio y existencia de líneas.
     *
     * @return Objeto {@link Pedido} validado, o {@code null} si los datos son incorrectos.
     */
    private Pedido getPedido() {
        String numStr  = txtNumero.getText().trim();
        String fp      = txtFechaPedido.getText().trim();
        String fe      = txtFechaEntrega.getText().trim();
        String precStr = txtPrecioTotal.getText().trim();

        if (fp.isEmpty() || fe.isEmpty() || precStr.isEmpty()) {
            error("Fecha pedido, fecha entrega y precio total son obligatorios.");
            return null;
        }

        LocalDate fechaPedido, fechaEntrega;
        try {
            fechaPedido  = LocalDate.parse(fp, FMT);
            fechaEntrega = LocalDate.parse(fe, FMT);
        } catch (DateTimeParseException ex) {
            error("Formato de fecha incorrecto. Usa dd/MM/yyyy");
            return null;
        }

        if (!fechaEntrega.isAfter(fechaPedido)) {
            error("La fecha de entrega debe ser posterior a la fecha del pedido.");
            return null;
        }

        precStr = precStr.replace("EUR", "").replace(",", ".").trim();

        float precioTotal;
        try {
            precioTotal = Float.parseFloat(precStr);
            if (precioTotal == 0) {
                error("El precio total no puede ser 0.");
                return null;
            }
        } catch (NumberFormatException ex) {
            error("El precio total debe ser un numero.");
            return null;
        }

        String nifCliente = getNifClienteSeleccionado();
        if (nifCliente.isEmpty()) {
            error("Selecciona un cliente.");
            return null;
        }

        if (lineasActuales.isEmpty()) {
            error("El pedido debe tener al menos una linea de producto.");
            return null;
        }

        int numPedido = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
        Pedido p = new Pedido(numPedido, fechaPedido, fechaEntrega, precioTotal, nifCliente);
        p.setLineas(new ArrayList<>(lineasActuales));
        return p;
    }

    /**
     * Carga los datos del pedido de la fila indicada en los campos del formulario
     * y bloquea la edición del número y la fecha de pedido al tratarse de un registro existente.
     *
     * @param fila Índice de la fila seleccionada en la tabla principal de pedidos.
     */
    private void cargarPedidoEnFormulario(int fila) {
        txtNumero.setText(valorOVacio(modeloPedidos, fila, 0));
        txtFechaPedido.setText(valorOVacio(modeloPedidos, fila, 1));
        txtFechaEntrega.setText(valorOVacio(modeloPedidos, fila, 2));
        txtPrecioTotal.setText(valorOVacio(modeloPedidos, fila, 3));
        seleccionarCliente(valorOVacio(modeloPedidos, fila, 4));

        try {
            String numStr = valorOVacio(modeloPedidos, fila, 0);
            if (!numStr.isEmpty()) {
                Pedido p = daoPedido.buscarPorNumero(Integer.parseInt(numStr));
                if (p != null) mostrarLineas(p);
            }
        } catch (SQLException ex) { error("Error al cargar lineas."); }

        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);
        txtFechaPedido.setEditable(false);
        estilizarNoEditable(txtFechaPedido);
    }

    /**
     * Devuelve el valor de una celda de la tabla como texto, o cadena vacía si es nulo.
     *
     * @param m       Modelo de tabla del que se obtiene el valor.
     * @param fila    Índice de fila.
     * @param columna Índice de columna.
     * @return Valor de la celda como {@link String}, o {@code ""} si es nulo.
     */
    private String valorOVacio(DefaultTableModel m, int fila, int columna) {
        Object val = m.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }

    /**
     * Muestra las líneas del pedido indicado en la tabla de líneas y actualiza
     * la lista en memoria y el precio total del pedido.
     *
     * @param p Objeto {@link Pedido} cuyas líneas se desean mostrar.
     */
    private void mostrarLineas(Pedido p) {
        modeloLineas.setRowCount(0);
        lineasActuales.clear();

        for (LineaPedido l : p.getLineas()) {
            lineasActuales.add(l);
            modeloLineas.addRow(new Object[]{
                false,
                l.getCod_Pro(),
                l.getCantidad_Pro(),
                String.format("%.2f", l.getPrecio_Total())
            });
        }

        tablaLineas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaLineas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaLineas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaLineas.getColumnModel().getColumn(3).setPreferredWidth(120);

        recalcularPrecioTotalPedido();
    }

    /**
     * Añade una fila con los datos del pedido al modelo de la tabla principal.
     *
     * @param p Objeto {@link Pedido} cuyos datos se añaden como nueva fila.
     */
    private void agregarFilaPedido(Pedido p) {
        modeloPedidos.addRow(new Object[]{
            p.getNum_Pedido(),
            p.getFecha_ped()  != null ? p.getFecha_ped().format(FMT)  : "",
            p.getFecha_ent()  != null ? p.getFecha_ent().format(FMT)  : "",
            String.format("%.2f", p.getPrecio_Total_Ped()),
            p.getNif_Cli()    != null ? p.getNif_Cli()                : ""
        });
    }

    /**
     * Carga los combos de clientes y productos con los datos actuales de la base de datos.
     * Cada entrada del combo de clientes muestra su NIF y nombre completo;
     * cada entrada del combo de productos muestra su código, tipo y precio.
     */
    private void cargarCombos() {
        try {
            List<Cliente> clientes = daoCliente.listarTodos();
            cmbCliente.removeAllItems();
            for (Cliente c : clientes)
                cmbCliente.addItem(c.getNif_Cli() + " - " + c.getNombre() + " " + c.getApellido());

            List<Producto> productos = daoProducto.listarTodos();
            cmbProducto.removeAllItems();
            for (Producto pr : productos)
                cmbProducto.addItem(pr.getCod_Pro() + " - " + pr.getTipo()
                    + " (" + String.format("%.2fEUR", pr.getPrecio_Pro()) + ")");

        } catch (SQLException ex) {
            error("Error al cargar combos: " + ex.getMessage());
        }
    }

    /**
     * Extrae y devuelve el precio unitario del producto actualmente seleccionado
     * en el combo, parseando la cadena de texto del ítem.
     *
     * @return Precio unitario del producto seleccionado, o {@code 0} si no se puede obtener.
     */
    private float getPrecioProductoSeleccionado() {
        String item = (String) cmbProducto.getSelectedItem();
        if (item == null) return 0;

        try {
            int ini = item.indexOf('(');
            int fin = item.indexOf('E');
            if (ini == -1 || fin == -1) return 0;

            String precioStr = item.substring(ini + 1, fin)
                                   .replace(",", ".")
                                   .trim();

            return Float.parseFloat(precioStr);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calcula y muestra en el campo de precio de línea el resultado de multiplicar
     * la cantidad introducida por el precio unitario del producto seleccionado.
     * Limpia el campo si la cantidad está vacía o es inválida.
     */
    private void recalcularPrecioLinea() {
        String cantStr = txtCantidad.getText().trim();

        if (cantStr.isEmpty()) {
            txtPrecioLinea.setText("");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);

            if (cantidad <= 0) {
                txtPrecioLinea.setText("");
                return;
            }

            float precioUnit = getPrecioProductoSeleccionado();
            float total = cantidad * precioUnit;
            txtPrecioLinea.setText(String.format("%.2f", total));

        } catch (NumberFormatException ex) {
            txtPrecioLinea.setText("");
        }
    }

    /**
     * Suma los precios de todas las líneas activas en memoria y actualiza
     * el campo de precio total del pedido con el resultado formateado a dos decimales.
     */
    private void recalcularPrecioTotalPedido() {
        float total = 0;
        for (LineaPedido lp : lineasActuales) {
            total += lp.getPrecio_Total();
        }
        txtPrecioTotal.setText(String.format("%.2f", total));
    }

    /**
     * Extrae y devuelve el NIF del cliente actualmente seleccionado en el combo.
     *
     * @return NIF del cliente seleccionado, o cadena vacía si no hay selección.
     */
    private String getNifClienteSeleccionado() {
        String item = (String) cmbCliente.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    /**
     * Extrae y devuelve el código del producto actualmente seleccionado en el combo.
     *
     * @return Código del producto seleccionado, o cadena vacía si no hay selección.
     */
    private String getNifProductoSeleccionado() {
        String item = (String) cmbProducto.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    /**
     * Selecciona en el combo de clientes el elemento cuyo texto comienza por el NIF indicado.
     *
     * @param nif NIF del cliente a seleccionar en el combo.
     */
    private void seleccionarCliente(String nif) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).startsWith(nif)) {
                cmbCliente.setSelectedIndex(i); break;
            }
        }
    }

    /**
     * Limpia todos los campos del formulario y restablece el estado inicial del panel,
     * incluyendo combos, tablas, lista en memoria y estado de edición de los campos.
     */
    private void limpiar() {
        txtNumero.setText("");
        txtFechaPedido.setText("");
        txtFechaEntrega.setText("");
        txtPrecioTotal.setText("");
        txtCantidad.setText("");
        txtPrecioLinea.setText("");

        if (cmbCliente.getItemCount() > 0)  cmbCliente.setSelectedIndex(0);
        if (cmbProducto.getItemCount() > 0) cmbProducto.setSelectedIndex(0);

        modeloLineas.setRowCount(0);
        modeloPedidos.setRowCount(0);
        lineasActuales.clear();
        tablaPedidos.clearSelection();

        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);
        txtFechaPedido.setEditable(true);
        estilizar(txtFechaPedido);
        estilizar(txtPrecioTotal);
    }

    /**
     * Crea y devuelve una etiqueta con el estilo visual del panel.
     *
     * @param t Texto a mostrar en la etiqueta.
     * @return {@link JLabel} con el color primario y fuente en negrita.
     */
    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(MenuPrincipal.COLOR_PRIMARIO);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    /**
     * Aplica el estilo visual estándar (fondo oscuro, texto claro y borde dorado)
     * a uno o varios campos de texto del panel.
     *
     * @param campos Campos de texto a los que se aplicará el estilo.
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
     * Aplica el estilo visual estándar a un {@link JComboBox} del panel.
     *
     * @param cmb ComboBox al que se aplicará el estilo.
     */
    private void estilizarCombo(JComboBox<?> cmb) {
        cmb.setBackground(new Color(50, 35, 16));
        cmb.setForeground(MenuPrincipal.COLOR_TEXTO);
        cmb.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    /**
     * Crea y devuelve un botón con texto, color de fondo e icono escalado,
     * aplicando el estilo visual estándar del panel.
     *
     * @param texto     Texto a mostrar en el botón.
     * @param bg        Color de fondo del botón.
     * @param rutaIcono Ruta relativa al fichero de imagen del icono, o {@code null} para no usarlo.
     * @return {@link JButton} configurado con el estilo estándar.
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
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 140, 60), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        b.setCursor(getVentana().getManoCur());
        b.setMargin(new Insets(5, 10, 5, 10));

        return b;
    }

    /**
     * Muestra un diálogo informativo con el mensaje indicado.
     *
     * @param m Mensaje a mostrar en el diálogo.
     */
    private void info(String m)  { DialogosMontealcohol.info(this, m); }

    /**
     * Muestra un diálogo de error con el mensaje indicado.
     *
     * @param m Mensaje de error a mostrar en el diálogo.
     */
    private void error(String m) { DialogosMontealcohol.error(this, m); }

    /**
     * Aplica el tamaño estándar definido en {@link #TAM_BOTON} a todos los botones indicados.
     *
     * @param botones Botones a los que se aplicará el tamaño estándar.
     */
    private void estandarizarBotones(JButton... botones) {
        for (JButton b : botones) {
            b.setPreferredSize(TAM_BOTON);
        }
    }

    /**
     * Gestiona los eventos de los botones del panel delegando en el método correspondiente
     * según el origen del evento.
     *
     * @param e Evento de acción generado por uno de los botones del panel.
     */
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
            else if (e.getSource() == btnAddLinea)
                addLinea();
            else if (e.getSource() == btnDelLinea)
                delLinea();
            else if (e.getSource() == btnDescuento)
                aplicarDescuento();

        } catch (SQLException ex) {
            error("proceso fallido");
        }
    }

    /**
     * Devuelve la instancia del generador XML asociada a este panel.
     *
     * @return Instancia de {@link XMLGenerator} utilizada por el panel.
     */
    public XMLGenerator getXml() {
        return xml;
    }
}
