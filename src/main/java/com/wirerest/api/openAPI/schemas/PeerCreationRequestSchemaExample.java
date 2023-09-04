package com.wirerest.api.openAPI.schemas;

import com.wirerest.api.openAPI.examples.samples.PeerCreationRequestSample;
import com.wirerest.api.openAPI.schemas.samples.PeerCreationRequestSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class PeerCreationRequestSchemaExample extends Schema<PeerCreationRequestSchema> {

    public PeerCreationRequestSchemaExample() {
        super();
        setExample(new PeerCreationRequestSample());
        setType("object");
    }

}
