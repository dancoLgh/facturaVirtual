package com.geotechpy.facturavirtual.controller;

import com.geotechpy.facturavirtual.model.Invoice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @PostMapping("/invoice")
    public Invoice create(@RequestBody Invoice invoice) {
        return invoice;
    }
}
