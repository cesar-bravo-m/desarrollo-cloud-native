package com.grupo11.cloud_ventas_producer.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo11.cloud_ventas_producer.model.Carrito;
import com.grupo11.cloud_ventas_producer.model.CarritoItem;
import com.grupo11.cloud_ventas_producer.model.Venta;
import com.grupo11.cloud_ventas_producer.service.CarritoItemService;
import com.grupo11.cloud_ventas_producer.service.CarritoService;
import com.grupo11.cloud_ventas_producer.service.ReceiptPdfService;
import com.grupo11.cloud_ventas_producer.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoItemService carritoItemService;

    @Autowired
    private ReceiptPdfService receiptPdfService;

    @GetMapping
    public ResponseEntity<List<Venta>> getAllVentas() {
        List<Venta> ventas = ventaService.getAllVentas();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        Optional<Venta> venta = ventaService.getVentaById(id);
        return venta.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> getVentasByUsuario(@PathVariable String usuarioId) {
        List<Venta> ventas = ventaService.getVentasByUsuarioId(usuarioId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/carrito/{carritoId}/status")
    public ResponseEntity<java.util.HashMap<String, String>> getSaleStatusByCarritoId(@PathVariable Long carritoId) {
        String status = ventaService.getSaleStatusByCarritoId(carritoId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        var response = new java.util.HashMap<String, String>();
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<java.util.HashMap<String, String>> processCheckout(
            @RequestParam Long carritoId,
            @RequestParam String usuarioId) {
        
        try {
            ventaService.processCheckout(carritoId, usuarioId);
            var response = new java.util.HashMap<String, String>();
            response.put("message", "Checkout processed successfully");
            response.put("carritoId", carritoId.toString());
            response.put("usuarioId", usuarioId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/boleta")
    public ResponseEntity<byte[]> generateReceipt(@RequestParam Long carritoId) {
        System.out.println("### Generating receipt for carritoId: " + carritoId);
        try {
            Optional<Carrito> carritoOpt = carritoService.getCarritoById(carritoId);
            if (carritoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Carrito carrito = carritoOpt.get();
            
            List<CarritoItem> carritoItems = carritoItemService.getItemsByCarritoId(carritoId);
            if (carritoItems.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            byte[] pdfBytes = receiptPdfService.generateReceiptPdf(carrito, carritoItems);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "boleta_" + carritoId + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 