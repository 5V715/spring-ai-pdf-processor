package dev.silas.ai.mistral.pdf.service;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;

@Service
public class DocumentIngest {

    private final PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();

    private final VectorStore vectorStore;

    public DocumentIngest(
            VectorStore vectorStore
    ) {
        this.vectorStore = vectorStore;
    }

    public Mono<UUID> consumeFile(FilePart filePart) throws IOException {
        var tempFile = Files.createTempFile(Instant.now().toString(), "tmp");
        return filePart.transferTo(tempFile)
                .then(Mono.create(sink -> {
                            var id = UUID.randomUUID();
                            var resource = new FileSystemResource(tempFile);
                            var reader = new PagePdfDocumentReader(resource, config);
                            var splitter = new TokenTextSplitter();
                            var result =
                                    splitter
                                            .apply(reader.get())
                                            .stream()
                                            .peek(it ->
                                                    it.getMetadata().put("requestid", id)
                                            ).toList();
                            vectorStore.accept(result);
                            sink.success(id);
                        })
                );
    }
}
