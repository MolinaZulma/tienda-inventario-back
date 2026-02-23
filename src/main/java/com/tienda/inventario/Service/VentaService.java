package com.tienda.inventario.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;  
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Year;                  
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;            
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;
import com.tienda.inventario.Model.Cliente;
import com.tienda.inventario.Model.DetalleVenta;
import com.tienda.inventario.Model.MovimientoInventario;
import com.tienda.inventario.Model.Producto;
import com.tienda.inventario.Model.Venta;
import com.tienda.inventario.Repository.ClienteRepository;
import com.tienda.inventario.Repository.MovimientoRepository;
import com.tienda.inventario.Repository.ProductoRepository;
import com.tienda.inventario.Repository.DetalleVentaRepository;
import com.tienda.inventario.Repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;

    // Constructor unificado para evitar errores de inyección
    public VentaService(VentaRepository ventaRepository, 
                        ProductoRepository productoRepository,
                        MovimientoRepository movimientoRepository, 
                        DetalleVentaRepository detalleVentaRepository,
                        ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Value("${tienda.direccion}")
    private String tiendaDireccion;

    @Value("${tienda.telefono}")
    private String tiendaTelefono;

    @Value("${tienda.email}")
    private String tiendaEmail;

    @Transactional
    public Venta registrarVenta(Venta venta) {
        Double totalVenta = 0.0;
    
    Cliente clienteRegistrado = null; 
    if (venta.getCliente() != null && venta.getCliente().getId() != null) {
        clienteRegistrado = clienteRepository.findById(venta.getCliente().getId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        venta.setCliente(clienteRegistrado); // Lo asignamos a la venta
    }

    for (DetalleVenta detalle : venta.getDetalles()) {
        Producto producto = productoRepository.findById(detalle.getProducto().getId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < detalle.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
        }

        // Actualizar stock
        producto.setStock(producto.getStock() - detalle.getCantidad());
        productoRepository.save(producto);

        // 2. REGISTRO DE MOVIMIENTO CORREGIDO
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(detalle.getCantidad());
        movimiento.setTipo("SALIDA");
        
        // Usamos el clienteRegistrado que buscamos arriba para obtener el nombre real
        String nombreParaMotivo = (clienteRegistrado != null) ? clienteRegistrado.getNombre() : "VENTA MOSTRADOR";
        movimiento.setMotivo("VENTA A: " + nombreParaMotivo);
        
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        totalVenta += detalle.getPrecioUnitario() * detalle.getCantidad();
    }

    venta.setTotal(totalVenta);

    if (venta.getNumeroFactura() == null) {
        venta.setNumeroFactura(generarNumeroFactura());
    }

    if (venta.getFecha() == null) {
        venta.setFecha(LocalDateTime.now());
    }

    return ventaRepository.save(venta);
}

    private String generarNumeroFactura() {
        List<Venta> ventasDelAnio = ventaRepository.findByFechaYear(Year.now().getValue());
        int secuencia = ventasDelAnio.size() + 1;
        return String.format("FAC-%d-%03d", Year.now().getValue(), secuencia);
    }

    // ESTE ES EL MÉTODO QUE SOLUCIONA EL ERROR DE TU CONSOLA
    public List<Venta> obtenerVentasDiarias() {
        return ventaRepository.findAll();
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public byte[] generarFactura(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            try {
                ClassPathResource resource = new ClassPathResource("static/images/tienda-online.png");
                InputStream inputStream = resource.getInputStream();
                byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
                Image logo = Image.getInstance(imageBytes);
                logo.scaleToFit(50, 50);
                logo.setAlignment(Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Error al cargar el logo: " + e.getMessage());
            }

            PdfPTable infoTiendaTable = new PdfPTable(2);
            infoTiendaTable.setWidthPercentage(60);
            infoTiendaTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            infoTiendaTable.addCell(crearCeldaSimple("Dirección:"));
            infoTiendaTable.addCell(crearCeldaSimple(tiendaDireccion));
            infoTiendaTable.addCell(crearCeldaSimple("Teléfono:"));
            infoTiendaTable.addCell(crearCeldaSimple(tiendaTelefono));
            infoTiendaTable.addCell(crearCeldaSimple("Email:"));
            infoTiendaTable.addCell(crearCeldaSimple(tiendaEmail));
            document.add(infoTiendaTable);

            Paragraph titulo = new Paragraph("Factura de Venta", new Font(Font.HELVETICA, 18, Font.BOLD));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Cliente: " + venta.getCliente().getNombre()));
            document.add(new Paragraph("Fecha: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Número de Factura: " + venta.getNumeroFactura()));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell(crearCeldaHeader("Producto"));
            table.addCell(crearCeldaHeader("Cant."));
            table.addCell(crearCeldaHeader("P. Unitario"));
            table.addCell(crearCeldaHeader("Subtotal"));

            for (DetalleVenta detalle : venta.getDetalles()) {
                table.addCell(detalle.getProducto().getNombre());
                table.addCell(String.valueOf(detalle.getCantidad()));
                table.addCell(String.format("$%.2f", detalle.getPrecioUnitario()));
                table.addCell(String.format("$%.2f", detalle.getPrecioUnitario() * detalle.getCantidad()));
            }
            document.add(table);

            document.add(new Paragraph("\n"));
            Paragraph totalPara = new Paragraph("SUBTOTAL: $" + String.format("%.2f", venta.getTotal()), new Font(Font.HELVETICA, 12, Font.BOLD));
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalPara);

            Paragraph ivaPara = new Paragraph("IVA (19%): $" + String.format("%.2f", venta.getTotal() * 0.19), new Font(Font.HELVETICA, 11));
            ivaPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaPara);

            Paragraph granTotal = new Paragraph("TOTAL CON IVA: $" + String.format("%.2f", (venta.getTotal() * 1.19)), new Font(Font.HELVETICA, 14, Font.BOLD));
            granTotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(granTotal);

            document.close(); 
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar la factura PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }
    
    private PdfPCell crearCeldaHeader(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, new Font(Font.HELVETICA, 10, Font.BOLD)));
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
      
    private PdfPCell crearCeldaSimple(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, new Font(Font.HELVETICA, 9)));
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
}