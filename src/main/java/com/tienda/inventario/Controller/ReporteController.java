package com.tienda.inventario.Controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;          
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.Document;          
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tienda.inventario.Model.Venta;
import com.tienda.inventario.Service.VentaService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/ventasDiarias")
    public ResponseEntity<byte[]> generarReporteVentasDiarias() throws IOException {
        List<Venta> ventasDiarias = ventaService.obtenerVentasDiarias();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

        Sheet sheet = workbook.createSheet("Ventas Diarias");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Fecha");
        headerRow.createCell(1).setCellValue("Cliente");
        headerRow.createCell(2).setCellValue("Total");

        int rowNum = 1;
        for (Venta venta : ventasDiarias) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(venta.getFecha().toString());
            row.createCell(1).setCellValue(venta.getCliente().getNombre());
            row.createCell(2).setCellValue(venta.getTotal());
        }

        workbook.write(outputStream);
        byte[] excelBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "ventas_diarias.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ventas/pdf")
    public ResponseEntity<byte[]> descargarReportePdf() {
        List<Venta> ventas = ventaService.obtenerVentasDiarias();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(com.lowagie.text.PageSize.LETTER);
            PdfWriter.getInstance(document, out);

            document.open();

            com.lowagie.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            com.lowagie.text.Phrase titulo = new com.lowagie.text.Phrase("Reporte de Ventas Diarias", fontTitulo);
            document.add(titulo);
            document.add(new com.lowagie.text.Phrase("\n\n"));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            com.lowagie.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            String[] headers = {"Fecha", "Cliente", "Total"};

             for (String columnHeader : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(columnHeader, fontHeader));
                cell.setBackgroundColor(Color.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

                for (Venta venta : ventas) {
                    table.addCell(venta.getFecha().toString());
                    table.addCell(venta.getCliente().getNombre());
                    table.addCell("$" + venta.getTotal());
                }

                document.add(table);
                document.close();

                byte[] responseBytes = out.toByteArray();

                HttpHeaders responHeaders = new HttpHeaders();
                responHeaders.setContentType(MediaType.APPLICATION_PDF);
                responHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas.pdf");

                return new ResponseEntity<>(responseBytes, responHeaders, HttpStatus.OK);
            }catch (DocumentException | IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            }
        }
    }