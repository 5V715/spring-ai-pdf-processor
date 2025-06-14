package dev.silas.ai.mistral.pdf.controller;

import dev.silas.ai.mistral.pdf.api.InvoiceInformation;
import dev.silas.ai.mistral.pdf.service.DocumentIngest;
import dev.silas.ai.mistral.pdf.service.Generation;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@Controller
public class PdfController {

    private final DocumentIngest ingest;
    private final Generation generation;

    PdfController(
            DocumentIngest documentIngest,
            Generation generation
    ) {
        this.ingest = documentIngest;
        this.generation = generation;
    }

    @GetMapping(value = "/")
    public Mono<Rendering> uploadForm() {
        return Mono.just(Rendering.view("uploadForm").build());
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleFileUpload(@RequestPart("file") FilePart file) throws IOException {
        return ingest.consumeFile(file)
                .map("redirect:/result?id=%s"::formatted);
    }

    @GetMapping(value = "/result", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<InvoiceInformation> getResult(@RequestParam UUID id) {
        return generation.forRequestId(id);
    }
}
