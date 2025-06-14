package dev.silas.ai.mistral.pdf.service;

import dev.silas.ai.mistral.pdf.api.InvoiceInformation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
public class Generation {

    private static final BeanOutputConverter<InvoiceInformation> FORMAT = new BeanOutputConverter<>(InvoiceInformation.class);
    private static final Prompt PROMPT =
            PromptTemplate
                    .builder()
                    .template(
                            """
                                    extract the needed information from the documents
                                    any values that are not present should be null
                                    currency should always be a ISO 4217 currency code
                                    dates should always be in YYYY-MM-DD format
                                    {format}
                                    """
                    ).variables(Map.of("format", FORMAT.getFormat()))
                    .build()
                    .create();

    private final VectorStore vectorStore;
    private final ChatClient client;

    public Generation(
            ChatClient.Builder builder,
            VectorStore vectorStore
    ) {
        this.client = builder.build();
        this.vectorStore = vectorStore;
    }

    public Mono<InvoiceInformation> forRequestId(UUID id) {
        return client
                .prompt(PROMPT)
                .system("you are helping to find relevant information from invoices")
                .advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                        SearchRequest
                                                .builder().
                                                filterExpression("requestid == '%s'".formatted(id))
                                                .similarityThreshold(0.8d)
                                                .topK(6)
                                                .build()
                                ).build()
                ).stream()
                .content()
                .collectList()
                .map(it -> FORMAT.convert(String.join("", it)
                ));
    }

}
