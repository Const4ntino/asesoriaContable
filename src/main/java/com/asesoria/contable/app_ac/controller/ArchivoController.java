package com.asesoria.contable.app_ac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/comprobantes";
    private final String uploadDir2 = System.getProperty("user.dir") + "/uploads/documentos";

    @PostMapping("/subir-comprobante")
    public ResponseEntity<String> subirArchivo(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        File directorio = new File(uploadDir);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = Paths.get(uploadDir, nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        String urlRelativa = "/uploads/comprobantes/" + nombreArchivo;
        System.out.println("Guardado en: " + rutaArchivo.toAbsolutePath());
        return ResponseEntity.ok(urlRelativa);
    }

    @PostMapping("/subir-documentos")
    public ResponseEntity<String> subirArchivo2(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        File directorio = new File(uploadDir2);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = Paths.get(uploadDir2, nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        String urlRelativa = "/uploads/documentos/" + nombreArchivo;
        System.out.println("Guardado en: " + rutaArchivo.toAbsolutePath());
        return ResponseEntity.ok(urlRelativa);
    }
}