package org.example.demojavafx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.util.EventGraphService;

public class CreateEvent {

    public TextArea eventTextArea;
    public TextField eventNameField;
    public MainWindow controller;

    public void setParent (MainWindow controller){
        this.controller = controller;
    }

    public void addEvent(ActionEvent actionEvent) throws JsonProcessingException {
        // get text from text field
        String name = eventNameField.getText();
        if(!name.isEmpty()){
            // 0 to upperCase
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // get text from text area
        String text = eventTextArea.getText();
        // text into JsonNode
        JsonNode node = Json.mapper().readTree(text);
        Schema schema = deserializeObjectSchema(node);

        schema.setName(name);
        EventGraphService.EVENT_GRAPH_SERVICE.getEventGraph().addEdge(name, schema);
        controller.drawGraph();
    }
    // copy from  @io.swagger.v3.core.util.ModelDeserializer
    private Schema deserializeObjectSchema(JsonNode node) {
        JsonNode additionalProperties = node.get("additionalProperties");
        Schema schema = null;
        if (additionalProperties != null) {
            if (additionalProperties.isBoolean()) {
                Boolean additionalPropsBoolean = (Boolean) Json.mapper().convertValue(additionalProperties, Boolean.class);
                ((ObjectNode)node).remove("additionalProperties");
                if (additionalPropsBoolean) {
                    schema = (Schema)Json.mapper().convertValue(node, MapSchema.class);
                } else {
                    schema = (Schema)Json.mapper().convertValue(node, ObjectSchema.class);
                }

                ((Schema)schema).setAdditionalProperties(additionalPropsBoolean);
            } else {
                Schema innerSchema = (Schema)Json.mapper().convertValue(additionalProperties, Schema.class);
                ((ObjectNode)node).remove("additionalProperties");
                MapSchema ms = (MapSchema)Json.mapper().convertValue(node, MapSchema.class);
                ms.setAdditionalProperties(innerSchema);
                schema = ms;
            }
        } else {
            schema = (Schema)Json.mapper().convertValue(node, ObjectSchema.class);
        }

        if (schema != null) {
            ((Schema)schema).jsonSchema(Json31.jsonSchemaAsMap(node));
        }
        return (Schema)schema;
    }
}
