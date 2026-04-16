/* Alvaro */

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

// Panel de interfaz grafica que gestiona el CRUD completo de pedidos
// Incluye cabecera del pedido, tabla de lineas y validaciones de fecha y precio
public class PedidoPanel extends PanelMontealcohol implements ActionListener {

    // Identificador de version para la serializacion del componente
    private static final long serialVersionUID = 1L;

    // Dimension estandar que se aplica a todos los botones del panel
    private static final Dimension TAM_BOTON = new Dimension(180, 42);

    // Instancia del generador de XML que se invoca tras cada operacion CRUD
    private final XMLGenerator xml = new XMLGeneratorImpl();

    // Formateador de fechas que trabaja con el formato dia/mes/anio
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Instancias de los DAO necesarios para operar con pedidos, clientes y productos
    private final PedidoDAO   daoPedido   = new PedidoDAO();
    private final ClienteDAO  daoCliente  = new ClienteDAO();
    private final ProductoDAO daoProducto = new ProductoDAO();

    // Definicion de las columnas que se muestran en la tabla principal de pedidos
    private final String[] COL_PEDIDOS = {"No Pedido", "F. Pedido", "F. Entrega",
                                           "Precio Total (EUR)", "NIF Cliente"};

    // Modelo de datos de la tabla de pedidos con celdas no editables
    private final DefaultTableModel modeloPedidos = new DefaultTableModel(COL_PEDIDOS, 0) {
        private static final long serialVersionUID = 1L;

        // Impide que el usuario edite directamente las celdas de la tabla
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // Tabla visual que muestra el listado de pedidos
    private final JTable tablaPedidos = new JTable(modeloPedidos);

    // Definicion de las columnas de la tabla de lineas del pedido
    private final String[] COL_LINEAS = {"Check", "Codigo Producto", "Cantidad", "Precio Linea (EUR)"};

    // Modelo de datos de la tabla de lineas donde solo la columna del check es editable
    private final DefaultTableModel modeloLineas = new DefaultTableModel(COL_LINEAS, 0) {

        // Devuelve Boolean para la primera columna para mostrar un checkbox
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : Object.class;
        }

        // Permite editar solo la columna del checkbox (columna 0)
        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0;
        }
    };

    // Tabla visual que muestra las lineas del pedido seleccionado
    private final JTable tablaLineas = new JTable(modeloLineas);

    // Campos de texto para los datos de la cabecera del pedido
    private final JTextField  txtNumero      = new JTextField(6);
    private final JTextField  txtFechaPedido = new JTextField(10);
    private final JTextField  txtFechaEntrega= new JTextField(10);
    private final JTextField  txtPrecioTotal = new JTextField(10);

    // ComboBox para seleccionar el cliente del pedido
    private final JComboBox<String> cmbCliente = new JComboBox<>();

    // ComboBox para seleccionar el producto a incluir en una linea
    private final JComboBox<String> cmbProducto = new JComboBox<>();

    // Campo de texto para introducir la cantidad del producto de la linea
    private final JTextField txtCantidad    = new JTextField(5);

    // Campo de texto que muestra el precio calculado de la linea (no editable)
    private final JTextField txtPrecioLinea = new JTextField(8);

    // Lista en memoria que almacena las lineas del pedido mientras se edita
    private final List<LineaPedido> lineasActuales = new ArrayList<>();

    // Botones de operaciones CRUD sobre el pedido
    private final JButton btnInsertar  = crearBtn(" Insertar",  new Color(200, 170, 120), "iconos/anadir.png");
    private final JButton btnBuscar    = crearBtn(" Buscar",    new Color(200, 170, 120), "iconos/buscar.png");
    private final JButton btnActualizar= crearBtn(" Actualizar", new Color(200, 170, 120), "iconos/actualizar.png");
    private final JButton btnEliminar  = crearBtn(" Eliminar",  new Color(200, 170, 120), "iconos/borrar.png");
    private final JButton btnListar    = crearBtn(" Listar",    new Color(200, 170, 120), "iconos/cargarDatos.png");
    private final JButton btnLimpiar   = crearBtn(" Limpiar",   new Color(200, 170, 120), "iconos/limpiar.png");

    // Botones para gestion de las lineas del pedido
    private final JButton btnAddLinea  = crearBtn("Anadir linea",  new Color(200, 170, 120), "iconos/ok.png");
    private final JButton btnDelLinea  = crearBtn("Quitar linea",  new Color(200, 170, 120), "iconos/error.png");
    private final JButton btnDescuento = crearBtn("Descuento",     new Color(200, 170, 120), "iconos/descuento.png");

    {
        // Establece un tamano mas reducido para el boton de descuento
        btnDescuento.setPreferredSize(new Dimension(120, 36));
    }

    // Constructor que monta todos los componentes del panel y registra los eventos
    public PedidoPanel(VentanaMontealcohol ventana) {
        super(ventana);

        setLayout(new BorderLayout(6, 6));
        setOpaque(false);

        // Carga los datos de clientes y productos en los combos desplegables
        cargarCombos();

        // Construye el panel superior con la cabecera y las lineas del pedido
        JPanel norte = new JPanel(new BorderLayout(6, 6));
        norte.setBackground(MenuPrincipal.COLOR_FONDO);
        norte.add(crearPanelCabecera(), BorderLayout.NORTH);
        norte.add(crearPanelLineas(),   BorderLayout.CENTER);

        // Organiza los tres bloques principales en el layout del panel
        add(norte,              BorderLayout.NORTH);
        add(crearTablaPedidos(), BorderLayout.CENTER);
        add(crearBotones(),     BorderLayout.SOUTH);

        // Registra los listeners de todos los botones del panel
        configurarEventos();

        // Aplica el tamano estandar a todos los botones del panel
        estandarizarBotones(
            btnInsertar, btnBuscar, btnActualizar,
            btnEliminar, btnListar, btnLimpiar,
            btnAddLinea, btnDelLinea, btnDescuento
        );

        // Recalcula el precio de la linea cuando cambia el producto seleccionado
        cmbProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recalcularPrecioLinea();
            }
        });

        // Recalcula el precio de la linea cada vez que el usuario modifica la cantidad
        txtCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
        });

        // Carga todos los pedidos existentes al iniciar el panel
        try {
            cargarTodos();
        } catch (SQLException ex) {
            DialogosMontealcohol.error(
                this,
                "Error al cargar los pedidos: " + ex.getMessage());
        }
    }

    // Escala un icono de imagen al tamano indicado para usarlo en los botones
    private ImageIcon escalarIcono(String ruta, int ancho, int alto) {
        Image img = new ImageIcon(ruta).getImage();
        Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(nueva);
    }

    // Construye y devuelve el panel con los campos de la cabecera del pedido
    private JPanel crearPanelCabecera() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);

        // Aplica un borde con titulo y margenes al panel de la cabecera
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

        // Aplica el estilo visual a los campos de texto y al combo de clientes
        estilizar(txtNumero, txtFechaPedido, txtFechaEntrega, txtPrecioTotal);
        estilizarCombo(cmbCliente);

        // Bloquea la edicion del numero de pedido (es generado por la BD)
        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);

        // Bloquea la edicion del precio total (se calcula automaticamente)
        txtPrecioTotal.setEditable(false);
        estilizarNoEditable(txtPrecioTotal);

        // Fila 0: numero de pedido, fecha de pedido y fecha de entrega
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

    // Construye y devuelve el panel con el formulario y la tabla de lineas del pedido
    private JPanel crearPanelLineas() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);

        // Aplica un borde con titulo al panel de lineas
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

        // Construye el formulario horizontal para agregar o quitar una linea
        JPanel fLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fLinea.setBackground(MenuPrincipal.COLOR_BOTON_BG);
        fLinea.setPreferredSize(new Dimension(0, 90));

        // Aplica estilos a los campos de cantidad, precio de linea y combo de producto
        estilizar(txtCantidad, txtPrecioLinea);
        estilizarCombo(cmbProducto);

        // Bloquea la edicion del precio de linea ya que se calcula automaticamente
        txtPrecioLinea.setEditable(false);
        estilizarNoEditable(txtPrecioLinea);

        // Agrega las etiquetas, campos y botones al formulario de lineas
        fLinea.add(etiqueta("Producto:"));    fLinea.add(cmbProducto);
        fLinea.add(etiqueta("Cantidad:"));    fLinea.add(txtCantidad);
        fLinea.add(etiqueta("Precio linea:")); fLinea.add(txtPrecioLinea);
        fLinea.add(btnAddLinea);
        fLinea.add(btnDelLinea);
        fLinea.add(btnDescuento);

        // Aplica el estilo visual a la tabla de lineas del pedido
        tablaLineas.setBackground(new Color(40, 28, 15));
        tablaLineas.setForeground(MenuPrincipal.COLOR_TEXTO);
        tablaLineas.setGridColor(new Color(80, 60, 30));
        tablaLineas.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tablaLineas.setSelectionForeground(Color.BLACK);
        tablaLineas.setRowHeight(22);
        tablaLineas.getTableHeader().setBackground(MenuPrincipal.COLOR_BOTON_BG);
        tablaLineas.getTableHeader().setForeground(MenuPrincipal.COLOR_PRIMARIO);
        tablaLineas.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        // Envuelve la tabla de lineas en un scroll con altura limitada
        JScrollPane spLineas = new JScrollPane(tablaLineas);
        spLineas.setPreferredSize(new Dimension(0, 100));
        spLineas.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        spLineas.getViewport().setBackground(new Color(40, 28, 15));

        p.add(fLinea,   BorderLayout.NORTH);
        p.add(spLineas, BorderLayout.CENTER);
        return p;
    }

    // Construye y devuelve el scroll con la tabla principal de pedidos
    private JScrollPane crearTablaPedidos() {

        // Aplica el estilo visual a la tabla principal de pedidos
        tablaPedidos.setBackground(new Color(40, 28, 15));
        tablaPedidos.setForeground(MenuPrincipal.COLOR_TEXTO);
        tablaPedidos.setGridColor(new Color(80, 60, 30));
        tablaPedidos.setSelectionBackground(MenuPrincipal.COLOR_PRIMARIO);
        tablaPedidos.setSelectionForeground(Color.BLACK);
        tablaPedidos.setRowHeight(24);
        tablaPedidos.getTableHeader().setBackground(MenuPrincipal.COLOR_BOTON_BG);
        tablaPedidos.getTableHeader().setForeground(MenuPrincipal.COLOR_PRIMARIO);
        tablaPedidos.getTableHeader().setFont(MenuPrincipal.FUENTE_BTN);

        // Carga en el formulario los datos del pedido cuando el usuario selecciona una fila
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPedidos.getSelectedRow() >= 0)
                cargarPedidoEnFormulario(tablaPedidos.getSelectedRow());
        });

        JScrollPane sp = new JScrollPane(tablaPedidos);
        sp.setBorder(BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO));
        sp.getViewport().setBackground(new Color(40, 28, 15));
        return sp;
    }

    // Construye y devuelve el panel inferior con los botones principales del CRUD
    private JPanel crearBotones() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MenuPrincipal.COLOR_FONDO);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.gridy = 0;

        // Agrupa los botones CRUD en un array para colocarlos en fila
        JButton[] botones = {
            btnInsertar, btnBuscar, btnActualizar,
            btnEliminar, btnListar, btnLimpiar
        };

        // Posiciona cada boton en su columna correspondiente
        for (int i = 0; i < botones.length; i++) {
            g.gridx = i;
            p.add(botones[i], g);
        }

        return p;
    }

    // Registra los listeners de accion en todos los botones del panel
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

    // Agrega una nueva linea al pedido tras validar producto, cantidad y actualizar el stock
    private void addLinea() {
        String cod  = getNifProductoSeleccionado();
        String cant = txtCantidad.getText().trim();

        // Comprueba que se haya seleccionado un producto y se haya introducido una cantidad
        if (cod.isEmpty() || cant.isEmpty()) {
            error("Selecciona producto y cantidad.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cant);

            // Comprueba que la cantidad introducida sea mayor que cero
            if (cantidad <= 0) {
                error("La cantidad debe ser mayor que 0.");
                return;
            }

            // Descuenta del stock del producto la cantidad indicada en la linea
            daoProducto.restarStock(cod, cantidad);

            // Calcula el precio de la linea multiplicando cantidad por precio unitario
            float precioUnit = getPrecioProductoSeleccionado();
            float precioLinea = cantidad * precioUnit;

            // Crea la linea en memoria y la agrega a la lista activa del pedido
            LineaPedido lp = new LineaPedido(0, cod, cantidad, precioLinea);
            lineasActuales.add(lp);

            // Agrega la linea como fila a la tabla visual con el checkbox desmarcado
            modeloLineas.addRow(new Object[]{
                false,
                cod,
                cantidad,
                String.format("%.2f", precioLinea)
            });

            // Limpia los campos de entrada de la linea para permitir agregar otra
            txtCantidad.setText("");
            txtPrecioLinea.setText("");

            // Actualiza el precio total del pedido sumando la nueva linea
            recalcularPrecioTotalPedido();

        } catch (Exception ex) {
            error("Error al anadir linea o actualizar stock.");
        }
    }

    // Elimina la linea seleccionada de la tabla y devuelve el stock al producto
    private void delLinea() {
        int fila = tablaLineas.getSelectedRow();

        // Comprueba que haya una fila seleccionada antes de intentar eliminar
        if (fila < 0) { error("Selecciona una linea para eliminar."); return; }

        // Obtiene el codigo y la cantidad de la linea seleccionada en la tabla
        String cod = modeloLineas.getValueAt(fila, 1).toString();
        int cantidad = Integer.parseInt(modeloLineas.getValueAt(fila, 2).toString());

        try {
            // Devuelve al stock del producto las unidades que tenia esta linea
            daoProducto.sumarStock(cod, cantidad);
        } catch (Exception ex) {
            error("Error al devolver stock.");
            return;
        }

        // Elimina la linea tanto de la lista en memoria como de la tabla visual
        lineasActuales.remove(fila);
        modeloLineas.removeRow(fila);

        // Actualiza el precio total del pedido tras eliminar la linea
        recalcularPrecioTotalPedido();
    }

    // ── CRUD ──────────────────────────────────────────────────

    // Valida los datos, inserta el pedido en la BD y regenera el XML
    private void insertar() throws SQLException {
        Pedido p = getPedido();

        // Comprueba que el pedido sea valido antes de intentar insertarlo
        if (p == null) return;

        // Inserta el pedido en la base de datos y obtiene el numero generado
        int num = daoPedido.insertar(p);

        // Regenera el XML tras la insercion para mantenerlo actualizado
        try {
            xml.generarXML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Pedido insertado con numero: " + num);
        limpiar();
        cargarTodos();
    }

    // Busca un pedido por su numero y lo muestra en la tabla
    private void buscar() throws SQLException {
        String num = txtNumero.getText().trim();

        // Comprueba que se haya introducido un numero de pedido para buscar
        if (num.isEmpty()) { error("Introduce el numero de pedido."); return; }

        try {
            Pedido p = daoPedido.buscarPorNumero(Integer.parseInt(num));
            modeloPedidos.setRowCount(0);

            // Muestra el pedido si se encontro o informa al usuario si no existe
            if (p != null) { agregarFilaPedido(p); mostrarLineas(p); }
            else info("No se encontro pedido con numero: " + num);

        } catch (NumberFormatException ex) {
            error("El numero de pedido debe ser entero.");
        }
    }

    // Valida los datos y actualiza el pedido mediante el procedimiento almacenado
    private void actualizar() throws SQLException {
        Pedido p = getPedido();

        // Comprueba que el pedido sea valido antes de intentar actualizarlo
        if (p == null) return;

        // Comprueba que se haya seleccionado un pedido existente para actualizar
        if (p.getNum_Pedido() == 0) {
            error("Selecciona un pedido existente para actualizar.");
            return;
        }

        // Genera las listas de productos y cantidades separadas por comas
        String listaPro = daoPedido.generarListaProductos(p);
        String listaCan = daoPedido.generarListaCantidades(p);

        // Llama al procedimiento almacenado para realizar la modificacion en BD
        daoPedido.modificarPedidoProcedimiento(
                p.getNum_Pedido(),
                "MODIFICAR",
                listaPro,
                listaCan,
                p.getFecha_ped(),
                p.getFecha_ent()
        );

        // Regenera el XML tras la actualizacion para mantenerlo sincronizado
        try {
            xml.generarXML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Pedido modificado correctamente.");
        cargarTodos();
    }

    // Solicita confirmacion y elimina el pedido indicado por numero
    private void eliminar() throws SQLException {
        String num = txtNumero.getText().trim();

        // Comprueba que se haya introducido un numero de pedido para eliminar
        if (num.isEmpty()) {
            error("Introduce el numero de pedido a eliminar.");
            return;
        }

        // Muestra un dialogo de confirmacion antes de proceder con el borrado
        int op = DialogosMontealcohol.confirmar(
            this,
            "Eliminar el pedido no " + num + " y todas sus lineas?"
        );

        // Cancela la operacion si el usuario no confirmo el borrado
        if (op != JOptionPane.YES_OPTION) return;

        try {
            if (daoPedido.eliminar(Integer.parseInt(num))) {

                // Regenera el XML tras la eliminacion para reflejar los cambios
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

    // Aplica un descuento del 10% a las lineas marcadas con 6 o mas unidades
    private void aplicarDescuento() {
        for (int i = 0; i < modeloLineas.getRowCount(); i++) {

            // Comprueba si la linea tiene el checkbox marcado
            Boolean marcado = (Boolean) modeloLineas.getValueAt(i, 0);
            if (marcado == null || !marcado) continue;

            int cantidad = Integer.parseInt(modeloLineas.getValueAt(i, 2).toString());
            String precioStr = modeloLineas.getValueAt(i, 3).toString().replace(",", ".");
            float precioLinea = Float.parseFloat(precioStr);

            // Aplica el descuento solo si la cantidad de la linea es 6 o mayor
            if (cantidad >= 6) {
                float nuevoPrecio = precioLinea * 0.90f;

                // Actualiza el precio de la linea en la tabla visual con el descuento aplicado
                modeloLineas.setValueAt(String.format("%.2f", nuevoPrecio).replace(".", ","), i, 3);

                // Actualiza el precio de la linea en la lista en memoria
                lineasActuales.get(i).setPrecio_Total(nuevoPrecio);
            }
        }

        // Recalcula el precio total del pedido tras aplicar los descuentos
        recalcularPrecioTotalPedido();
        info("Descuento aplicado a las lineas marcadas.");
    }

    // Carga todos los pedidos de la BD y los muestra en la tabla principal
    private void cargarTodos() throws SQLException {
        List<Pedido> lista = daoPedido.listarTodos();
        modeloPedidos.setRowCount(0);

        // Agrega cada pedido de la lista como una fila en la tabla
        for (Pedido p : lista) agregarFilaPedido(p);
    }

    // Construye y valida un objeto Pedido a partir de los datos del formulario
    private Pedido getPedido() {
        String numStr  = txtNumero.getText().trim();
        String fp      = txtFechaPedido.getText().trim();
        String fe      = txtFechaEntrega.getText().trim();
        String precStr = txtPrecioTotal.getText().trim();

        // Comprueba que los campos obligatorios de fecha y precio no esten vacios
        if (fp.isEmpty() || fe.isEmpty() || precStr.isEmpty()) {
            error("Fecha pedido, fecha entrega y precio total son obligatorios.");
            return null;
        }

        LocalDate fechaPedido, fechaEntrega;
        try {
            // Convierte las cadenas de texto a fechas con el formato esperado
            fechaPedido  = LocalDate.parse(fp, FMT);
            fechaEntrega = LocalDate.parse(fe, FMT);
        } catch (DateTimeParseException ex) {
            error("Formato de fecha incorrecto. Usa dd/MM/yyyy");
            return null;
        }

        // Comprueba que la fecha de entrega sea posterior a la fecha del pedido
        if (!fechaEntrega.isAfter(fechaPedido)) {
            error("La fecha de entrega debe ser posterior a la fecha del pedido.");
            return null;
        }

        // Normaliza el precio eliminando el simbolo de euro y convirtiendo la coma en punto
        precStr = precStr.replace("EUR", "").replace(",", ".").trim();

        float precioTotal;
        try {
            precioTotal = Float.parseFloat(precStr);

            // Comprueba que el precio total no sea cero
            if (precioTotal == 0) {
                error("El precio total no puede ser 0.");
                return null;
            }
        } catch (NumberFormatException ex) {
            error("El precio total debe ser un numero.");
            return null;
        }

        // Obtiene el NIF del cliente seleccionado en el combo
        String nifCliente = getNifClienteSeleccionado();

        // Comprueba que haya un cliente seleccionado
        if (nifCliente.isEmpty()) {
            error("Selecciona un cliente.");
            return null;
        }

        // Comprueba que el pedido tenga al menos una linea de producto
        if (lineasActuales.isEmpty()) {
            error("El pedido debe tener al menos una linea de producto.");
            return null;
        }

        // Convierte el numero de pedido a entero o usa 0 si el campo esta vacio
        int numPedido = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);

        // Construye el objeto Pedido con los datos validados y las lineas actuales
        Pedido p = new Pedido(numPedido, fechaPedido, fechaEntrega, precioTotal, nifCliente);
        p.setLineas(new ArrayList<>(lineasActuales));

        return p;
    }

    // Carga los datos del pedido de la fila seleccionada en los campos del formulario
    private void cargarPedidoEnFormulario(int fila) {
        txtNumero.setText(valorOVacio(modeloPedidos, fila, 0));
        txtFechaPedido.setText(valorOVacio(modeloPedidos, fila, 1));
        txtFechaEntrega.setText(valorOVacio(modeloPedidos, fila, 2));
        txtPrecioTotal.setText(valorOVacio(modeloPedidos, fila, 3));

        // Selecciona en el combo el cliente correspondiente al pedido cargado
        seleccionarCliente(valorOVacio(modeloPedidos, fila, 4));

        try {
            String numStr = valorOVacio(modeloPedidos, fila, 0);

            // Carga las lineas del pedido si el numero de pedido no esta vacio
            if (!numStr.isEmpty()) {
                Pedido p = daoPedido.buscarPorNumero(Integer.parseInt(numStr));
                if (p != null) mostrarLineas(p);
            }
        } catch (SQLException ex) { error("Error al cargar lineas."); }

        // Bloquea la edicion del numero y la fecha de pedido al cargar un pedido existente
        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);

        txtFechaPedido.setEditable(false);
        estilizarNoEditable(txtFechaPedido);
    }

    // Devuelve el valor de una celda de la tabla como texto o cadena vacia si es nulo
    private String valorOVacio(DefaultTableModel m, int fila, int columna) {
        Object val = m.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }

    // Muestra las lineas del pedido indicado en la tabla de lineas
    private void mostrarLineas(Pedido p) {
        modeloLineas.setRowCount(0);
        lineasActuales.clear();

        // Recorre cada linea del pedido y la agrega a la tabla y a la lista en memoria
        for (LineaPedido l : p.getLineas()) {
            lineasActuales.add(l);

            modeloLineas.addRow(new Object[]{
                false,
                l.getCod_Pro(),
                l.getCantidad_Pro(),
                String.format("%.2f", l.getPrecio_Total())
            });
        }

        // Ajusta el ancho preferido de cada columna de la tabla de lineas
        tablaLineas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaLineas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaLineas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaLineas.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Actualiza el precio total del pedido en el campo correspondiente
        recalcularPrecioTotalPedido();
    }

    // Agrega una fila con los datos del pedido al modelo de la tabla principal
    private void agregarFilaPedido(Pedido p) {
        modeloPedidos.addRow(new Object[]{
            p.getNum_Pedido(),
            p.getFecha_ped()  != null ? p.getFecha_ped().format(FMT)  : "",
            p.getFecha_ent()  != null ? p.getFecha_ent().format(FMT)  : "",
            String.format("%.2f", p.getPrecio_Total_Ped()),
            p.getNif_Cli()    != null ? p.getNif_Cli()                : ""
        });
    }

    // Carga los combos de clientes y productos con los datos actuales de la BD
    private void cargarCombos() {
        try {
            List<Cliente> clientes = daoCliente.listarTodos();
            cmbCliente.removeAllItems();

            // Agrega cada cliente al combo con su NIF y nombre completo
            for (Cliente c : clientes)
                cmbCliente.addItem(c.getNif_Cli() + " - " + c.getNombre() + " " + c.getApellido());

            List<Producto> productos = daoProducto.listarTodos();
            cmbProducto.removeAllItems();

            // Agrega cada producto al combo con su codigo, tipo y precio
            for (Producto pr : productos)
                cmbProducto.addItem(pr.getCod_Pro() + " - " + pr.getTipo()
                    + " (" + String.format("%.2fEUR", pr.getPrecio_Pro()) + ")");

        } catch (SQLException ex) {
            error("Error al cargar combos: " + ex.getMessage());
        }
    }

    // Extrae y devuelve el precio unitario del producto seleccionado en el combo
    private float getPrecioProductoSeleccionado() {
        String item = (String) cmbProducto.getSelectedItem();
        if (item == null) return 0;

        try {
            // Localiza el precio entre el parentesis de apertura y el simbolo de euro
            int ini = item.indexOf('(');
            int fin = item.indexOf('E');
            if (ini == -1 || fin == -1) return 0;

            // Convierte el texto del precio a float normalizando el separador decimal
            String precioStr = item.substring(ini + 1, fin)
                                   .replace(",", ".")
                                   .trim();

            return Float.parseFloat(precioStr);
        } catch (Exception e) {
            return 0;
        }
    }

    // Calcula y muestra en pantalla el precio de la linea segun la cantidad introducida
    private void recalcularPrecioLinea() {
        String cantStr = txtCantidad.getText().trim();

        // Limpia el campo de precio si la cantidad esta vacia
        if (cantStr.isEmpty()) {
            txtPrecioLinea.setText("");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);

            // Limpia el campo de precio si la cantidad es cero o negativa
            if (cantidad <= 0) {
                txtPrecioLinea.setText("");
                return;
            }

            // Calcula el precio total de la linea multiplicando por el precio unitario
            float precioUnit = getPrecioProductoSeleccionado();
            float total = cantidad * precioUnit;

            // Muestra el precio calculado con dos decimales en el campo de precio de linea
            txtPrecioLinea.setText(String.format("%.2f", total));

        } catch (NumberFormatException ex) {
            txtPrecioLinea.setText("");
        }
    }

    // Suma los precios de todas las lineas activas y actualiza el campo de precio total
    private void recalcularPrecioTotalPedido() {
        float total = 0;

        // Acumula el precio de cada linea del pedido
        for (LineaPedido lp : lineasActuales) {
            total += lp.getPrecio_Total();
        }

        // Muestra el total calculado con dos decimales en el campo correspondiente
        txtPrecioTotal.setText(String.format("%.2f", total));
    }

    // Extrae y devuelve el NIF del cliente actualmente seleccionado en el combo
    private String getNifClienteSeleccionado() {
        String item = (String) cmbCliente.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    // Extrae y devuelve el codigo del producto actualmente seleccionado en el combo
    private String getNifProductoSeleccionado() {
        String item = (String) cmbProducto.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    // Selecciona en el combo el cliente cuyo NIF coincide con el valor indicado
    private void seleccionarCliente(String nif) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).startsWith(nif)) {
                cmbCliente.setSelectedIndex(i); break;
            }
        }
    }

    // Limpia todos los campos del formulario y restablece el estado inicial del panel
    private void limpiar() {
        txtNumero.setText("");
        txtFechaPedido.setText("");
        txtFechaEntrega.setText("");
        txtPrecioTotal.setText("");
        txtCantidad.setText("");
        txtPrecioLinea.setText("");

        // Restablece los combos al primer elemento si tienen datos disponibles
        if (cmbCliente.getItemCount() > 0)  cmbCliente.setSelectedIndex(0);
        if (cmbProducto.getItemCount() > 0) cmbProducto.setSelectedIndex(0);

        // Limpia el modelo de ambas tablas y la seleccion activa
        modeloLineas.setRowCount(0);
        modeloPedidos.setRowCount(0);
        lineasActuales.clear();
        tablaPedidos.clearSelection();

        // Desbloquea los campos de edicion que pudieran haber quedado bloqueados
        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);
        txtFechaPedido.setEditable(true);
        estilizar(txtFechaPedido);
        estilizar(txtPrecioTotal);
    }

    // Crea y devuelve una etiqueta con el estilo visual del panel
    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(MenuPrincipal.COLOR_PRIMARIO);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    // Aplica el estilo visual estandar a uno o varios campos de texto
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

    // Aplica el estilo visual estandar a un JComboBox del panel
    private void estilizarCombo(JComboBox<?> cmb) {
        cmb.setBackground(new Color(50, 35, 16));
        cmb.setForeground(MenuPrincipal.COLOR_TEXTO);
        cmb.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    // Crea y devuelve un boton con texto, color de fondo e icono escalado
    private JButton crearBtn(String texto, Color bg, String rutaIcono) {
        JButton b = new JButton(texto);

        // Carga y escala el icono si se ha proporcionado una ruta valida
        if (rutaIcono != null) {
            ImageIcon icono = escalarIcono(rutaIcono, 28, 28);
            b.setIcon(icono);
            b.setHorizontalTextPosition(SwingConstants.RIGHT);
            b.setIconTextGap(10);
        }

        // Aplica fuente, colores y bordes estandarizados al boton
        b.setFont(MenuPrincipal.FUENTE_BTN);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 140, 60), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        // Asigna el cursor de mano al pasar por encima del boton
        b.setCursor(getVentana().getManoCur());
        b.setMargin(new Insets(5, 10, 5, 10));

        return b;
    }

    // Muestra un dialogo de informacion con el mensaje indicado
    private void info(String m)  { DialogosMontealcohol.info(this, m); }

    // Muestra un dialogo de error con el mensaje indicado
    private void error(String m) { DialogosMontealcohol.error(this, m); }

    // Aplica el tamano estandar definido en TAM_BOTON a todos los botones indicados
    private void estandarizarBotones(JButton... botones) {
        for (JButton b : botones) {
            b.setPreferredSize(TAM_BOTON);
        }
    }

    // Gestiona los eventos de los botones y delega en el metodo correspondiente
    public void actionPerformed(ActionEvent e) {
        try {
            // Comprueba que boton origino el evento y ejecuta la accion correspondiente
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

    // Devuelve la instancia del generador XML del panel
    public XMLGenerator getXml() {
        return xml;
    }
}

/* Alvaro */
