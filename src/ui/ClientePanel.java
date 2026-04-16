
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
/**
 * Panel CRUD completo para la gestión de clientes dentro del sistema
 * Montealcohol. Permite insertar, buscar, actualizar, eliminar y listar
 * clientes, además de generar automáticamente el archivo XML tras cada
 * operación exitosa.
 *
 * Incluye formulario, tabla de resultados, botones de acción y estilos
 * visuales coherentes con la estética corporativa.
 *
 * Extiende {@link PanelMontealcohol} para mantener la integración con la
 * ventana principal y los cursores personalizados.
 *
 * @author Ines
 * @version 1.0
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

    /**
     * Construye el panel de gestión de clientes, inicializa el formulario,
     * la tabla, los botones y carga los datos existentes desde la base de datos.
     *
     * @param ventana Ventana principal desde la que se muestra este panel.
     */
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

        
    /**
     * Carga una imagen desde la ruta indicada y la escala al tamaño especificado.
     *
     * @param ruta  Ruta del archivo de imagen.
     * @param ancho Ancho deseado en píxeles.
     * @param alto  Alto deseado en píxeles.
     * @return      Icono escalado listo para usar en botones u otros componentes.
     */
    private ImageIcon escalarIcono(String ruta, int ancho, int alto) {

            Image img = new ImageIcon(ruta).getImage();
            Image nueva = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(nueva);
        

    }

    // ── Formulario ────────────────────────────────────────────
        /**
         * Construye el panel del formulario con etiquetas y campos organizados
         * mediante GridBagLayout, aplicando el estilo corporativo.
         *
         * @return Panel del formulario.
         */
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
        /**
         * Construye el panel que contiene la tabla de clientes, aplicando colores,
         * fuentes y comportamiento de selección.
         *
         * @return JScrollPane con la tabla configurada.
         */
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
        /**
         * Construye el panel que contiene los botones CRUD, organizados en una
         * cuadrícula horizontal.
         *
         * @return Panel con los botones de acción.
         */
        private JPanel crearPanelBotones() {

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

    // ── Eventos ───────────────────────────────────────────────
        /**
         * Asocia los botones del panel a sus respectivos eventos de acción.
         */
        private void configurarEventos() {

        btnInsertar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnActualizar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnListar.addActionListener(this);
        btnLimpiar.addActionListener(this);
    }

    
    
    /**
     * Inserta un nuevo cliente utilizando los datos del formulario. Si la operación
     * es exitosa, actualiza el XML, muestra un mensaje informativo y recarga la tabla.
     *
     * @throws SQLException Si ocurre un error al insertar en la base de datos.
     */
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

    

    /**
     * Busca un cliente por su NIF y muestra el resultado en la tabla y en el
     * formulario. Si no existe, muestra un mensaje informativo.
     *
     * @throws SQLException Si ocurre un error durante la consulta.
     */
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

    /**
     * Actualiza los datos de un cliente existente utilizando la información del
     * formulario. Si la operación tiene éxito, actualiza el XML y recarga la tabla.
     *
     * @throws SQLException Si ocurre un error al actualizar en la base de datos.
     */
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


    /**
     * Elimina un cliente por su NIF tras confirmar la acción con el usuario.
     * Si la eliminación es exitosa, actualiza el XML, limpia el formulario
     * y recarga la tabla.
     *
     * @throws SQLException Si ocurre un error al eliminar en la base de datos.
     */
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


    /**
     * Carga todos los clientes desde la base de datos y los muestra en la tabla.
     *
     * @throws SQLException Si ocurre un error al obtener los datos.
     */
    private void cargarTodos() throws SQLException {

       
            List<Cliente> lista = dao.listarTodos();
            modelo.setRowCount(0);
            for (Cliente c : lista) agregarFila(c);
        
    }

    // ── Helpers ───────────────────────────────────────────────
    /**
     * Obtiene los datos del formulario y construye un objeto {@link Cliente}.
     * Valida los campos obligatorios y el formato del número.
     *
     * @return Cliente construido o null si hay errores de validación.
     */
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
    
 
    /**
     * Añade una fila a la tabla con los datos del cliente indicado.
     *
     * @param c Cliente cuyos datos se insertarán en la tabla.
     */
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

    



    /**
     * Rellena el formulario con los datos de la fila seleccionada en la tabla.
     * El campo NIF se bloquea y se aplica estilo de campo no editable.
     *
     * @param fila Índice de la fila seleccionada.
     */
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

     // ⭐ BLOQUEAR NIF + ESTILO NO EDITABLE
        txtNif.setEditable(false);
        estilizarNoEditable(txtNif);

    }


    // Método auxiliar para evitar NullPointerException
    /**
     * Devuelve el valor de una celda de la tabla o una cadena vacía si es null.
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
     * Rellena el formulario con los datos del cliente proporcionado.
     * El NIF se bloquea y se estiliza como campo no editable.
     *
     * @param c Cliente cuyos datos se mostrarán en el formulario.
     */
    private void rellenarCampos(Cliente c) {

        txtNif.setText(c.getNif_Cli());
        txtNif.setEditable(false);
        estilizarNoEditable(txtNif);

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


    /**
     * Limpia todos los campos del formulario, desbloquea el NIF y restablece
     * los estilos estándar. También limpia la tabla y la selección.
     */
    private void limpiarFormulario() {

        for (JTextField tf : new JTextField[]{txtNif, txtNombre, txtApellido, txtCalle,
                txtNumero, txtPiso, txtLocalidad, txtProvincia, txtTelefono, txtEmail}) {
            tf.setText("");
        }

        // ⭐ DESBLOQUEAR NIF
        txtNif.setEditable(true);
        estilizarCampos(txtNif);

        modelo.setRowCount(0);
        tabla.clearSelection();
    }


    // ── Estilos ───────────────────────────────────────────────
    /**
     * Crea una etiqueta con el estilo corporativo Montealcohol para el formulario.
     *
     * @param texto Texto que mostrará la etiqueta.
     * @return      JLabel configurado con fuente y color corporativos.
     */
    private JLabel etiqueta(String texto) {

        JLabel l = new JLabel(texto);
        l.setForeground(MenuPrincipal.COLOR_PRIMARIO);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    /**
     * Aplica el estilo Montealcohol a los campos de texto del formulario.
     *
     * @param campos Campos a estilizar.
     */
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
     * @param msg Mensaje a mostrar.
     */
    private void mostrarInfo(String msg) {
        DialogosMontealcohol.info(this, msg);
    }

    /**
     * Muestra un diálogo de error con estilo Montealcohol.
     *
     * @param msg Mensaje a mostrar.
     */
    private void mostrarError(String msg) {
        DialogosMontealcohol.error(this, msg);
    }

        /**
         * Aplica un tamaño estándar a todos los botones recibidos para mantener
         * una apariencia uniforme en la interfaz.
         *
         * @param botones Botones a los que se les aplicará el tamaño estándar.
         */
        private void estandarizarBotones(JButton... botones) {

        for (JButton b : botones) {
            b.setPreferredSize(TAM_BOTON);
        }
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

    /**
     * Devuelve la instancia del generador XML asociada a este panel.
     *
     * @return Implementación de {@link XMLGenerator} utilizada por el panel.
     */
    public XMLGenerator getXml() {

		return xml;
	}
}
