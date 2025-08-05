package com.geotechpy.facturavirtual.controller;

import com.geotechpy.facturavirtual.model.Contribuyente;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class ContribuyenteController {

    private final Map<String, Contribuyente> data = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public Contribuyente register(@RequestBody Contribuyente contribuyente) {
        data.put(contribuyente.getRuc(), contribuyente);
        return contribuyente;
    }

    @GetMapping("/register/{ruc}")
    public Contribuyente get(@PathVariable String ruc) {
        return data.get(ruc);
    }
}
