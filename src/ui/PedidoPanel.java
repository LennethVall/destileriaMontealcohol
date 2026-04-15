
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
 * Panel CRUD completo para la gestión de Pedidos.
 * Incluye cabecera del pedido + tabla de líneas de productos.
 * Valida: fecha_entrega > fecha_pedido, precio_total != 0.
 */
public class PedidoPanel extends PanelMontealcohol implements ActionListener {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Dimension TAM_BOTON = new Dimension(180, 42);

	
	// ── GeneradorXML──────────────────────────────────────────
	private final XMLGenerator xml = new XMLGeneratorImpl();

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PedidoDAO   daoPedido   = new PedidoDAO();
    private final ClienteDAO  daoCliente  = new ClienteDAO();
    private final ProductoDAO daoProducto = new ProductoDAO();

    // ── Tabla de pedidos ──────────────────────────────────────
    private final String[] COL_PEDIDOS = {"Nº Pedido", "F. Pedido", "F. Entrega",
                                           "Precio Total (€)", "NIF Cliente"};
    private final DefaultTableModel modeloPedidos = new DefaultTableModel(COL_PEDIDOS, 0) {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tablaPedidos = new JTable(modeloPedidos);

    // ── Tabla de líneas ───────────────────────────────────────
    private final String[] COL_LINEAS = {"✔", "Código Producto", "Cantidad", "Precio Línea (€)"};

    private final DefaultTableModel modeloLineas = new DefaultTableModel(COL_LINEAS, 0) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : Object.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0; // solo el check es editable
        }
    };

    private final JTable tablaLineas = new JTable(modeloLineas);

    // ── Campos cabecera ───────────────────────────────────────
    private final JTextField  txtNumero      = new JTextField(6);
    private final JTextField  txtFechaPedido = new JTextField(10);  // dd/MM/yyyy
    private final JTextField  txtFechaEntrega= new JTextField(10);
    private final JTextField  txtPrecioTotal = new JTextField(10);
    private final JComboBox<String> cmbCliente = new JComboBox<>();

    // ── Campos línea ──────────────────────────────────────────
    private final JComboBox<String> cmbProducto = new JComboBox<>();
    private final JTextField txtCantidad    = new JTextField(5);
    private final JTextField txtPrecioLinea = new JTextField(8);
    private final List<LineaPedido> lineasActuales = new ArrayList<>();

    // ── Botones pedido ────────────────────────────────────────
    private final JButton btnInsertar  = crearBtn(" Insertar",  new Color(200, 170, 120), "iconos/añadir.png");
    private final JButton btnBuscar    = crearBtn(" Buscar",    new Color(200, 170, 120),"iconos/buscar.png");
    private final JButton btnActualizar= crearBtn(" Actualizar", new Color(200, 170, 120), "iconos/actualizar.png");
    private final JButton btnEliminar  = crearBtn(" Eliminar",  new Color(200, 170, 120), "iconos/borrar.png");
    private final JButton btnListar    = crearBtn(" Listar", new Color(200, 170, 120),"iconos/cargarDatos.png");
    private final JButton btnLimpiar   = crearBtn(" Limpiar",   new Color(200, 170, 120), "iconos/limpiar.png");

    // ── Botones línea ─────────────────────────────────────────
 // ── Botones línea ─────────────────────────────────────────
    private final JButton btnAddLinea = crearBtn("Añadir línea",   new Color(200, 170, 120), "iconos/ok.png");
    private final JButton btnDelLinea = crearBtn("Quitar línea",   new Color(200, 170, 120), "iconos/error.png");
    private final JButton btnDescuento = crearBtn("Descuento", new Color(200,170,120), "iconos/descuento.png");

   
    {
        btnDescuento.setPreferredSize(new Dimension(120, 36));
    }

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
            btnInsertar,
            btnBuscar,
            btnActualizar,
            btnEliminar,
            btnListar,
            btnLimpiar,
            btnAddLinea,
            btnDelLinea,
            btnDescuento
        );
    
        

        cmbProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recalcularPrecioLinea();
            }
        });
     // ⭐ Recalcular precio de línea cuando cambia la cantidad
        txtCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalcularPrecioLinea(); }
        });


        // cargarTodos SÍ lanza SQLException → mantener try/catch
        try {
            cargarTodos();
        } catch (SQLException ex) {
            DialogosMontealcohol.error(
                this,
                "Error al cargar los pedidos: " + ex.getMessage());
        }
    }
        
        
    private ImageIcon escalarIcono(String ruta, int ancho, int alto) {
        Image img = new ImageIcon(ruta).getImage();
        Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(nueva);
    }




    // ── Panel cabecera del pedido ─────────────────────────────
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

       

        txtPrecioTotal.setEditable(false);
        estilizarNoEditable(txtPrecioTotal);

       

        // Fila 0
        g.gridx=0; g.gridy=0; g.weightx=0; p.add(etiqueta("Nº Pedido"),         g);
        g.gridx=1; g.weightx=0.3;           p.add(txtNumero,                     g);
        g.gridx=2; g.weightx=0;             p.add(etiqueta("F. Pedido *"),        g);
        g.gridx=3; g.weightx=0.5;           p.add(txtFechaPedido,                g);
        g.gridx=4; g.weightx=0;             p.add(etiqueta("F. Entrega *"),       g);
        g.gridx=5; g.weightx=0.5;           p.add(txtFechaEntrega,               g);

        // Fila 1
        g.gridx=0; g.gridy=1; g.weightx=0; p.add(etiqueta("Precio Total (€) *"),g);
        g.gridx=1; g.weightx=0.4;           p.add(txtPrecioTotal,                g);
        g.gridx=2; g.weightx=0;             p.add(etiqueta("Cliente *"),          g);
        g.gridx=3; g.weightx=1; g.gridwidth=3; p.add(cmbCliente,                 g);

        return p;
    }

    // ── Panel de líneas del pedido ────────────────────────────
    private JPanel crearPanelLineas() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(MenuPrincipal.COLOR_BOTON_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MenuPrincipal.COLOR_PRIMARIO),
                " Líneas del Pedido ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                MenuPrincipal.COLOR_PRIMARIO
            ),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // Formulario de nueva línea
     // Formulario de nueva línea
        JPanel fLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fLinea.setBackground(MenuPrincipal.COLOR_BOTON_BG);
        fLinea.setPreferredSize(new Dimension(0, 90));

        estilizar(txtCantidad, txtPrecioLinea);
        estilizarCombo(cmbProducto);

        txtPrecioLinea.setEditable(false);
        estilizarNoEditable(txtPrecioLinea);   // ⭐ AÑADIR ESTA LÍNEA



        fLinea.add(etiqueta("Producto:"));   fLinea.add(cmbProducto);
        fLinea.add(etiqueta("Cantidad:"));   fLinea.add(txtCantidad);
        fLinea.add(etiqueta("Precio línea:")); fLinea.add(txtPrecioLinea);
        fLinea.add(btnAddLinea);
        fLinea.add(btnDelLinea);
        fLinea.add(btnDescuento);


        // Mini-tabla de líneas
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

    // ── Tabla principal de pedidos ────────────────────────────
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

        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPedidos.getSelectedRow() >= 0)
                cargarPedidoEnFormulario(tablaPedidos.getSelectedRow());
        });

        JScrollPane sp = new JScrollPane(tablaPedidos);
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


    // ── Eventos ───────────────────────────────────────────────
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
    

    private void addLinea() {
        String cod = getNifProductoSeleccionado();
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

            // ⭐ RESTAR STOCK EN BD
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
            error("Error al añadir línea o actualizar stock.");
        }
    }




    private void delLinea() {
        int fila = tablaLineas.getSelectedRow();
        if (fila < 0) { error("Selecciona una línea para eliminar."); return; }

        String cod = modeloLineas.getValueAt(fila, 1).toString();
        int cantidad = Integer.parseInt(modeloLineas.getValueAt(fila, 2).toString());

        try {
            // ⭐ SUMAR STOCK EN BD
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
    private void insertar() throws SQLException {
        Pedido p = getPedido();
        if (p == null) return;

        int num = daoPedido.insertar(p);

        try {
            xml.generarXML();   // ← AQUÍ, justo después de insertar en BD
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Pedido insertado con número: " + num);
        limpiar();
        cargarTodos();
    }


    private void buscar() throws SQLException{
        String num = txtNumero.getText().trim();
        if (num.isEmpty()) { error("Introduce el número de pedido."); return; }
        try {
            Pedido p = daoPedido.buscarPorNumero(Integer.parseInt(num));
            modeloPedidos.setRowCount(0);
            if (p != null) { agregarFilaPedido(p); mostrarLineas(p); }
            else info("No se encontró pedido con número: " + num);
        } catch (NumberFormatException ex) { error("El número de pedido debe ser entero."); }
          
    }

    private void actualizar() throws SQLException {
        Pedido p = getPedido();
        if (p == null) return;
        if (p.getNum_Pedido() == 0) {
            error("Selecciona un pedido existente para actualizar.");
            return;
        }

     // Generar listas separadas por comas
        String listaPro = daoPedido.generarListaProductos(p);
        String listaCan = daoPedido.generarListaCantidades(p);

        // Llamar al procedimiento almacenado
        daoPedido.modificarPedidoProcedimiento(
                p.getNum_Pedido(),
                "MODIFICAR",
                listaPro,
                listaCan,
                p.getFecha_ped(),
                p.getFecha_ent()

        );

        // Si llega aquí, no hubo excepción
        try {
            xml.generarXML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        info("Pedido modificado correctamente.");
        cargarTodos();

    }


    private void eliminar() throws SQLException {
        String num = txtNumero.getText().trim();
        if (num.isEmpty()) {
            error("Introduce el número de pedido a eliminar.");
            return;
        }

        int op = DialogosMontealcohol.confirmar(
        	    this,
        	    "¿Eliminar el pedido nº " + num + " y todas sus líneas?"
        	);

        	if (op != JOptionPane.YES_OPTION) return;

        try {
            if (daoPedido.eliminar(Integer.parseInt(num))) {

                try {
                    xml.generarXML();   // ← AQUÍ, solo si se eliminó correctamente
                } catch (Exception e) {
                    e.printStackTrace();
                }

                info("Pedido eliminado.");
                limpiar();
                cargarTodos();

            } else {
                info("No existe pedido con ese número.");
            }
        } catch (NumberFormatException ex) {
            error("Número de pedido inválido.");
        }
    }

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
        info("Descuento aplicado a las líneas marcadas.");
    }

 
    private void cargarTodos()throws SQLException{
        
            List<Pedido> lista = daoPedido.listarTodos();
            modeloPedidos.setRowCount(0);
            for (Pedido p : lista) agregarFilaPedido(p);
        
    }

    // ── Helpers ───────────────────────────────────────────────
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

        // ⭐ NORMALIZAR PRECIO TOTAL
        precStr = precStr.replace("€", "").replace(",", ".").trim();

        float precioTotal;
        try {
            precioTotal = Float.parseFloat(precStr);
            if (precioTotal == 0) {
                error("El precio total no puede ser 0.");
                return null;
            }
        } catch (NumberFormatException ex) {
            error("El precio total debe ser un número.");
            return null;
        }

        String nifCliente = getNifClienteSeleccionado();
        if (nifCliente.isEmpty()) {
            error("Selecciona un cliente.");
            return null;
        }

        if (lineasActuales.isEmpty()) {
            error("El pedido debe tener al menos una línea de producto.");
            return null;
        }

        int numPedido = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);

        Pedido p = new Pedido(numPedido, fechaPedido, fechaEntrega, precioTotal, nifCliente);
        p.setLineas(new ArrayList<>(lineasActuales));

        return p;
    }

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
        } catch (SQLException ex) { error("Error al cargar líneas."); }

     // ⭐ BLOQUEAR CAMPOS + ESTILO NO EDITABLE
        txtNumero.setEditable(false);
        estilizarNoEditable(txtNumero);

        txtFechaPedido.setEditable(false);
        estilizarNoEditable(txtFechaPedido);

    }


    private String valorOVacio(DefaultTableModel m, int fila, int columna) {
        Object val = m.getValueAt(fila, columna);
        return val != null ? val.toString() : "";
    }
    
    private void mostrarLineas(Pedido p) {
    modeloLineas.setRowCount(0);
    lineasActuales.clear();

    for (LineaPedido l : p.getLineas()) {
        lineasActuales.add(l);

        modeloLineas.addRow(new Object[]{
            false,
            l.getCod_Pro(),
            l.getCantidad_Pro(),
            String.format("%.2f", l.getPrecio_Total())  // String en la tabla
        });
    }

    tablaLineas.getColumnModel().getColumn(0).setPreferredWidth(40);
    tablaLineas.getColumnModel().getColumn(1).setPreferredWidth(150);
    tablaLineas.getColumnModel().getColumn(2).setPreferredWidth(80);
    tablaLineas.getColumnModel().getColumn(3).setPreferredWidth(120);

    recalcularPrecioTotalPedido();
}


    private void agregarFilaPedido(Pedido p) {
        modeloPedidos.addRow(new Object[]{
            p.getNum_Pedido(),
            p.getFecha_ped()  != null ? p.getFecha_ped().format(FMT)  : "",
            p.getFecha_ent()  != null ? p.getFecha_ent().format(FMT)  : "",
            String.format("%.2f", p.getPrecio_Total_Ped()),
            p.getNif_Cli()    != null ? p.getNif_Cli()                : ""
        });
    }

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
                    + " (" + String.format("%.2f€", pr.getPrecio_Pro()) + ")");
        } catch (SQLException ex) {
            error("Error al cargar combos: " + ex.getMessage());
        }
    }
    private float getPrecioProductoSeleccionado() {
        String item = (String) cmbProducto.getSelectedItem();
        if (item == null) return 0;

        try {
            int ini = item.indexOf('(');
            int fin = item.indexOf('€');
            if (ini == -1 || fin == -1) return 0;

            String precioStr = item.substring(ini + 1, fin)
                                   .replace(",", ".")
                                   .trim();

            return Float.parseFloat(precioStr);
        } catch (Exception e) {
            return 0;
        }
    }

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


    private void recalcularPrecioTotalPedido() {
        float total = 0;

        for (LineaPedido lp : lineasActuales) {
            total += lp.getPrecio_Total();
        }

        txtPrecioTotal.setText(String.format("%.2f", total));
    }

    
    private String getNifClienteSeleccionado() {
        String item = (String) cmbCliente.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    private String getNifProductoSeleccionado() {
        String item = (String) cmbProducto.getSelectedItem();
        return item != null ? item.split(" - ")[0] : "";
    }

    private void seleccionarCliente(String nif) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).startsWith(nif)) {
                cmbCliente.setSelectedIndex(i); break;
            }
        }
    }

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

        // ⭐ DESBLOQUEAR CAMPOS
        txtNumero.setEditable(true);
        txtFechaPedido.setEditable(true);
        estilizar(txtNumero);
        estilizar(txtFechaPedido);
        estilizar(txtPrecioTotal);

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
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 140, 60), 1),   // borde dorado
                BorderFactory.createEmptyBorder(8, 16, 8, 16)                 // padding interno
        ));

        b.setCursor(getVentana().getManoCur());

        
        b.setMargin(new Insets(5, 10, 5, 10));



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
		else if (e.getSource() == btnAddLinea)
			addLinea();
		else if (e.getSource() == btnDelLinea)
			delLinea();
		else if (e.getSource() == btnDescuento)
		    aplicarDescuento();

	 } catch (SQLException ex) {
		 error ("proceso fallido");
}
}



public XMLGenerator getXml() {
	return xml;
}
}
