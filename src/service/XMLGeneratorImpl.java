/* Alvaro */

package service;

import dao.ClienteDAO;
import dao.ProveedorDAO;
import dao.ProductoDAO;
import dao.PedidoDAO;

import model.Cliente;
import model.Proveedor;
import model.Producto;
import model.Pedido;
import model.LineaPedido;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Clase que implementa la generacion del archivo XML con todos los datos del sistema
public class XMLGeneratorImpl implements XMLGenerator {

    // Instancias de los DAO necesarios para obtener los datos de cada entidad
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public void generarXML() throws Exception {

        // Comprueba si existe la carpeta "xml" y la crea si no existe
        File carpeta = new File("xml");
        if (!carpeta.exists()) carpeta.mkdirs();

        // Define el archivo de destino donde se guardara el XML generado
        File archivo = new File("xml/MonteAlcohol.xml");

        // Inicializa el constructor de documentos XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Crea el elemento raiz del documento XML con sus atributos de esquema
        Element root = doc.createElement("montealcohol");
        root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:noNamespaceSchemaLocation","C://Users//1dam//Downloads/montealcohol.xsd");
        doc.appendChild(root);

        // ============================
        // CLIENTES
        // ============================

        // Crea el elemento contenedor de todos los clientes
        Element clientesEl = doc.createElement("clientes");
        root.appendChild(clientesEl);

        // Obtiene todos los clientes de la base de datos
        List<Cliente> clientes = clienteDAO.listarTodos();

        // Recorre cada cliente y crea su elemento XML con todos sus datos
        for (Cliente c : clientes) {
            Element cliEl = doc.createElement("cliente");

            // Asigna el NIF como atributo del elemento cliente
            cliEl.setAttribute("nif", c.getNif_Cli());
            clientesEl.appendChild(cliEl);

            // Agrega los datos obligatorios del cliente como elementos hijo
            crearElemento(doc, cliEl, "nombre", c.getNombre());
            crearElemento(doc, cliEl, "apellido", c.getApellido());
            crearElemento(doc, cliEl, "calle", c.getCalle());
            crearElemento(doc, cliEl, "numero", String.valueOf(c.getNumero()));

            // Agrega el piso solo si el cliente tiene ese dato informado
            if (c.getPiso() != null)
                crearElemento(doc, cliEl, "piso", c.getPiso());

            crearElemento(doc, cliEl, "localidad", c.getLocalidad());
            crearElemento(doc, cliEl, "provincia", c.getProvincia());

            // Agrega el telefono solo si el cliente lo tiene registrado
            if (c.getTelefono() != null)
                crearElemento(doc, cliEl, "telefono", c.getTelefono());

            // Agrega el email solo si el cliente lo tiene registrado
            if (c.getEmail() != null)
                crearElemento(doc, cliEl, "email", c.getEmail());
        }

        // ============================
        // PROVEEDORES
        // ============================

        // Crea el elemento contenedor de todos los proveedores
        Element proveedoresEl = doc.createElement("proveedores");
        root.appendChild(proveedoresEl);

        // Obtiene todos los proveedores de la base de datos
        List<Proveedor> proveedores = proveedorDAO.listarTodos();

        // Recorre cada proveedor y crea su elemento XML con sus datos
        for (Proveedor p : proveedores) {
            Element provEl = doc.createElement("proveedor");

            // Asigna el NIF como atributo del elemento proveedor
            provEl.setAttribute("nif", p.getNif_Prove());
            proveedoresEl.appendChild(provEl);

            crearElemento(doc, provEl, "nombre", p.getNombre());
            crearElemento(doc, provEl, "localidad", p.getLocalidad());

            // Agrega el telefono solo si el proveedor lo tiene registrado
            if (p.getTelefono() != null)
                crearElemento(doc, provEl, "telefono", p.getTelefono());

            // Agrega el email solo si el proveedor lo tiene registrado
            if (p.getEmail() != null)
                crearElemento(doc, provEl, "email", p.getEmail());
        }

        // ============================
        // PRODUCTOS
        // ============================

        // Crea el elemento contenedor de todos los productos
        Element productosEl = doc.createElement("productos");
        root.appendChild(productosEl);

        // Obtiene todos los productos de la base de datos
        List<Producto> productos = productoDAO.listarTodos();

        // Recorre cada producto y crea su elemento XML con sus datos
        for (Producto p : productos) {
            Element prodEl = doc.createElement("producto");

            // Asigna el codigo del producto como atributo del elemento
            prodEl.setAttribute("codigo", p.getCod_Pro());
            productosEl.appendChild(prodEl);

            crearElemento(doc, prodEl, "nombre", p.getNom_Pro());

            // Crea el elemento precio con el atributo de moneda en euros
            Element precioEl = doc.createElement("precio");
            precioEl.setAttribute("moneda", "EUR");
            precioEl.setTextContent(String.valueOf(p.getPrecio_Pro()));
            prodEl.appendChild(precioEl);

            crearElemento(doc, prodEl, "stock", String.valueOf(p.getStock()));
            crearElemento(doc, prodEl, "tipo", p.getTipo().name());

            // Crea la referencia al proveedor usando su NIF como atributo ref
            Element provRef = doc.createElement("proveedor");
            provRef.setAttribute("ref", p.getNif_Prove());
            prodEl.appendChild(provRef);

            // Crea el elemento imagen con la ruta construida a partir del codigo del producto
            Element imgEl = doc.createElement("imagen");
            imgEl.setAttribute("ruta", "img/" + p.getCod_Pro() + ".png");
            prodEl.appendChild(imgEl);
        }

        // ============================
        // PEDIDOS
        // ============================

        // Crea el elemento contenedor de todos los pedidos
        Element pedidosEl = doc.createElement("pedidos");
        root.appendChild(pedidosEl);

        // Obtiene todos los pedidos de la base de datos
        List<Pedido> pedidos = pedidoDAO.listarTodos();

        // Recorre cada pedido y crea su elemento XML con todos sus datos
        for (Pedido ped : pedidos) {
            Element pedEl = doc.createElement("pedido");

            // Asigna el numero de pedido como atributo del elemento
            pedEl.setAttribute("numero", String.valueOf(ped.getNum_Pedido()));
            pedidosEl.appendChild(pedEl);

            crearElemento(doc, pedEl, "fecha_ped", ped.getFecha_ped().toString());
            crearElemento(doc, pedEl, "fecha_ent", ped.getFecha_ent().toString());
            crearElemento(doc, pedEl, "precio_total", String.valueOf(ped.getPrecio_Total_Ped()));

            // Crea la referencia al cliente usando su NIF como atributo ref
            Element cliRef = doc.createElement("cliente");
            cliRef.setAttribute("ref", ped.getNif_Cli());
            pedEl.appendChild(cliRef);

            // Crea el elemento contenedor de las lineas del pedido
            Element lineasEl = doc.createElement("lineas");
            pedEl.appendChild(lineasEl);

            // Recorre cada linea del pedido y la agrega como elemento XML
            for (LineaPedido lp : ped.getLineas()) {
                Element lineaEl = doc.createElement("linea");

                // Asigna el atributo de unidad fijo a "uds" en cada linea
                lineaEl.setAttribute("unidad", "uds");
                lineasEl.appendChild(lineaEl);

                // Crea la referencia al producto de la linea usando su codigo
                Element prodRef = doc.createElement("producto");
                prodRef.setAttribute("ref", lp.getCod_Pro());
                lineaEl.appendChild(prodRef);

                crearElemento(doc, lineaEl, "cantidad", String.valueOf(lp.getCantidad_Pro()));
                crearElemento(doc, lineaEl, "precio_linea", String.valueOf(lp.getPrecio_Total()));
            }
        }

        // ============================
        // GUARDAR XML
        // ============================

        // Configura el transformador para escribir el documento XML con sangria
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        // Activa la sangria en el XML resultante para mejorar la legibilidad
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // Prepara el origen del documento y el destino del archivo de salida
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(archivo));

        // Escribe el contenido del documento XML en el archivo de destino
        transformer.transform(source, result);
    }

    // Crea un elemento hijo con nombre y texto y lo agrega al elemento padre indicado
    private void crearElemento(Document doc, Element padre, String nombre, String valor) {
        Element el = doc.createElement(nombre);
        el.setTextContent(valor);
        padre.appendChild(el);
    }
}

/* Alvaro */
