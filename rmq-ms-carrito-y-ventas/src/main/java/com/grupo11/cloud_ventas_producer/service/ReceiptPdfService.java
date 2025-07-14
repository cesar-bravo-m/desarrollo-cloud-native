package com.grupo11.cloud_ventas_producer.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.model.Carrito;
import com.grupo11.cloud_ventas_producer.model.CarritoItem;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class ReceiptPdfService {

    public byte[] generateReceiptPdf(Carrito carrito, List<CarritoItem> carritoItems) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Set font
            PdfFont font = PdfFontFactory.createFont();

            // Add header
            Paragraph title = new Paragraph("BOLETA DE VENTA")
                    .setFont(font)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Company information
            Paragraph companyInfo = new Paragraph("TIENDA GRUPO 11\n" +
                    "RUT: 12.345.678-9\n" +
                    "Dirección: Av. Principal 123, Santiago\n" +
                    "Teléfono: +56 2 1234 5678")
                    .setFont(font)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(companyInfo);

            // Receipt details
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fecha = carrito.getCreadoEn().format(formatter);
            
            Paragraph receiptDetails = new Paragraph()
                    .setFont(font)
                    .setFontSize(10)
                    .add("Boleta N°: " + carrito.getCarritoId() + "\n")
                    .add("Fecha: " + fecha + "\n")
                    .add("Cliente: " + carrito.getUsuarioId() + "\n")
                    .setMarginBottom(15);
            document.add(receiptDetails);

            // Items table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio Unit.").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            BigDecimal total = BigDecimal.ZERO;

            // Add items to table
            for (CarritoItem item : carritoItems) {
                BigDecimal subtotal = item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad()));
                total = total.add(subtotal);

                String productoNombre = item.getProducto() != null ? 
                    item.getProducto().getNombre() : "Producto ID: " + item.getProductoId();

                table.addCell(new Cell().add(new Paragraph(productoNombre).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(item.getCantidad().toString()).setFontSize(9))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph("$" + formatPrice(item.getPrecioUnitario())).setFontSize(9))
                        .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph("$" + formatPrice(subtotal)).setFontSize(9))
                        .setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(table);

            // Total
            Paragraph totalParagraph = new Paragraph("TOTAL: $" + formatPrice(total))
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(15);
            document.add(totalParagraph);

            // Footer
            Paragraph footer = new Paragraph("¡Gracias por su compra!\n" +
                    "Este documento es su comprobante de venta.")
                    .setFont(font)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);

            document.close();
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF receipt", e);
        }
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,.0f", price);
    }
} 