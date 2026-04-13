
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

public class XMLGeneratorImpl implements XMLGenerator {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public void generarXML() throws Exception {

        // Crear carpeta xml si no existe
        File carpeta = new File("xml");
        if (!carpeta.exists()) carpeta.mkdirs();

        File archivo = new File("xml/MonteAlcohol.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Raíz
        Element root = doc.createElement("montealcohol");
        doc.appendChild(root);

        // ============================
        // CLIENTES
        // ============================
        Element clientesEl = doc.createElement("clientes");
        root.appendChild(clientesEl);

        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente c : clientes) {
            Element cliEl = doc.createElement("cliente");
            cliEl.setAttribute("nif", c.getNif_Cli());

            clientesEl.appendChild(cliEl);

            crearElemento(doc, cliEl, "nombre", c.getNombre());
            crearElemento(doc, cliEl, "apellido", c.getApellido());
            crearElemento(doc, cliEl, "calle", c.getCalle());
            crearElemento(doc, cliEl, "numero", String.valueOf(c.getNumero()));

            if (c.getPiso() != null)
                crearElemento(doc, cliEl, "piso", c.getPiso());

            crearElemento(doc, cliEl, "localidad", c.getLocalidad());
            crearElemento(doc, cliEl, "provincia", c.getProvincia());

            if (c.getTelefono() != null)
                crearElemento(doc, cliEl, "telefono", c.getTelefono());

            if (c.getEmail() != null)
                crearElemento(doc, cliEl, "email", c.getEmail());
        }

        // ============================
        // PROVEEDORES
        // ============================
        Element proveedoresEl = doc.createElement("proveedores");
        root.appendChild(proveedoresEl);

        List<Proveedor> proveedores = proveedorDAO.listarTodos();
        for (Proveedor p : proveedores) {
            Element provEl = doc.createElement("proveedor");
            provEl.setAttribute("nif", p.getNif_Prove());
            proveedoresEl.appendChild(provEl);

            crearElemento(doc, provEl, "nombre", p.getNombre());
            crearElemento(doc, provEl, "localidad", p.getLocalidad());

            if (p.getTelefono() != null)
                crearElemento(doc, provEl, "telefono", p.getTelefono());

            if (p.getEmail() != null)
                crearElemento(doc, provEl, "email", p.getEmail());
        }

        // ============================
        // PRODUCTOS
        // ============================
        Element productosEl = doc.createElement("productos");
        root.appendChild(productosEl);

        List<Producto> productos = productoDAO.listarTodos();
        for (Producto p : productos) {
            Element prodEl = doc.createElement("producto");
            prodEl.setAttribute("codigo", p.getCod_Pro());
            productosEl.appendChild(prodEl);

            crearElemento(doc, prodEl, "nombre", p.getNom_Pro());

            // precio con atributo moneda="EUR"
            Element precioEl = doc.createElement("precio");
            precioEl.setAttribute("moneda", "EUR");
            precioEl.setTextContent(String.valueOf(p.getPrecio_Pro()));
            prodEl.appendChild(precioEl);

            crearElemento(doc, prodEl, "stock", String.valueOf(p.getStock()));
            crearElemento(doc, prodEl, "tipo", p.getTipo().name());

            // referencia al proveedor
            Element provRef = doc.createElement("proveedor");
            provRef.setAttribute("ref", p.getNif_Prove());
            prodEl.appendChild(provRef);

            // imagen
            Element imgEl = doc.createElement("imagen");
            imgEl.setAttribute("ruta", "img/" + p.getCod_Pro() + ".png");
            prodEl.appendChild(imgEl);
        }

        // ============================
        // PEDIDOS
        // ============================
        Element pedidosEl = doc.createElement("pedidos");
        root.appendChild(pedidosEl);

        List<Pedido> pedidos = pedidoDAO.listarTodos();
        for (Pedido ped : pedidos) {
            Element pedEl = doc.createElement("pedido");
            pedEl.setAttribute("numero", String.valueOf(ped.getNum_Pedido()));
            pedidosEl.appendChild(pedEl);

            crearElemento(doc, pedEl, "fecha_ped", ped.getFecha_ped().toString());
            crearElemento(doc, pedEl, "fecha_ent", ped.getFecha_ent().toString());
            crearElemento(doc, pedEl, "precio_total", String.valueOf(ped.getPrecio_Total_Ped()));

            // referencia al cliente
            Element cliRef = doc.createElement("cliente");
            cliRef.setAttribute("ref", ped.getNif_Cli());
            pedEl.appendChild(cliRef);

            // líneas
            Element lineasEl = doc.createElement("lineas");
            pedEl.appendChild(lineasEl);

            for (LineaPedido lp : ped.getLineas()) {
                Element lineaEl = doc.createElement("linea");
                lineaEl.setAttribute("unidad", "uds"); // atributo fixed
                lineasEl.appendChild(lineaEl);

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
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(archivo));

        transformer.transform(source, result);
    }

    private void crearElemento(Document doc, Element padre, String nombre, String valor) {
        Element el = doc.createElement(nombre);
        el.setTextContent(valor);
        padre.appendChild(el);
    }
}
