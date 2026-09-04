package com.loldomizer.controller;

import com.loldomizer.model.JogadorResponse;
import com.loldomizer.model.SorteioRequest;
import com.loldomizer.service.SorteioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sorteio")
@CrossOrigin(origins = "*") // Permite chamadas do front-end web local
public class SorteioController {

    private final SorteioService sorteioService;

    public SorteioController(SorteioService sorteioService) {
        this.sorteioService = sorteioService;
    }

    @PostMapping
    public ResponseEntity<List<JogadorResponse>> sortear(@RequestBody SorteioRequest request) {
        return ResponseEntity.ok(sorteioService.realizarSorteio(request));
    }
}