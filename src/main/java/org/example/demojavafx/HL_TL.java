package org.example.demojavafx;

import io.swagger.v3.core.util.Json31;
import io.swagger.v3.oas.models.media.Schema;
import org.example.graph.*;

import java.util.HashMap;
import java.util.Map;

public class HL_TL {
    /**
     * topics:
     * 1. completed
     * 2. order_created
     * 3. arrived
     * 4. good_is_booked
     * 5. warehouse_is_full
     * 6. order_is_canceled
     * 7. car_is_ready
     * 8. open_the_door
     * 9. start_trip
     * 10. close_the_door
     * Service:
     * 1. Deaf_Service
     * Deaf_Service: consume 2,5,9
     */
    static String completedJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "Product",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;
    static String orderCreatedJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "order_created",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "orderId": {
                  "description": "The unique identifier for a order",
                  "type": "integer"
                },
                "userName": {
                  "description": "Name of the user, who created the order",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the order",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "x-incoming": {
                "topics": [ "order_created" ]
              },
              "required": [ "orderId", "userName", "price" ]
            }
            
            """;
    static String arrivedJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "arrived",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;

    static String goodIsBookedJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "good_is_booked",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;

    static String warehouseIsFullJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "warehouse_is_full",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "warehouseId": {
                  "description": "The id of warehouse",
                  "type": "integer"
                },
                "address": {
                  "description": "address of warehouse",
                  "type": "string"
                }
              },
              "required": [ "warehouseId", "address"],
              "x-incoming": {
                "topics": [ "warehouse_is_full" ]
              }
            }
            
            """;

    static String goodIsDeliveredJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "good_is_delivered",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;
    static String orderIsCanceledJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "order_is_canceled",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;

    static String carIsReadyJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "car_is_ready",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;

    static String openTheDoorJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "open_the_door",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;

    static String closeTheDoorJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "close_the_door",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {
                "productId": {
                  "description": "The unique identifier for a product",
                  "type": "integer"
                },
                "productName": {
                  "description": "Name of the product",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the product",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "required": [ "productId", "productName", "price" ]
            }
            
            """;

    static String startTripJsonSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema",
              "$id": "https://example.com/product.schema.json",
              "title": "start_trip",
              "description": "A product from Acme's catalog",
              "type": "object",
              "properties": {                
                "addressTo": {
                  "description": "address to",
                  "type": "string"
                },
                "addressFrom": {
                  "description": "address from",
                  "type": "string"
                },
                "price": {
                  "description": "The price of the trip",
                  "type": "number",
                  "exclusiveMinimum": 0
                }
              },
              "x-incoming": {
                                    "topics": [
                                      "start_trip"
                                    ]
                                  },
              "required": [ "addressTo", "addressFrom", "price" ]
            }
            
            """;


    static Map<String, Schema> getSchemas() {
        Map<String, Schema> schemas = new HashMap<>();
        Schema schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(orderCreatedJsonSchema));
        schemas.put("order_created", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(warehouseIsFullJsonSchema));
        schemas.put("warehouse_is_full", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(startTripJsonSchema));
        schemas.put("start_trip", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(completedJsonSchema));
        schemas.put("completed", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(arrivedJsonSchema));
        schemas.put("arrived", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(goodIsBookedJsonSchema));
        schemas.put("good_is_booked", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(openTheDoorJsonSchema));
        schemas.put("open_the_door", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(closeTheDoorJsonSchema));
        schemas.put("close_the_door", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(orderIsCanceledJsonSchema));
        schemas.put("order_is_canceled", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(goodIsDeliveredJsonSchema));
        schemas.put("good_is_delivered", schema);

        schema = new Schema();
        schema.setJsonSchema(Json31.jsonSchemaAsMap(carIsReadyJsonSchema));
        schemas.put("trip_is_canceled", schema);

        return schemas;
    }



    public static EventGraph getGraph() {
        EventGraph GRAPH = new EventGraph();

        GRAPH.setName("HL_TL");

        Node deafService = new Node("Deaf_Service", NodeType.SERVICE, "Deaf_Service", null);
        GRAPH.addNode(deafService);

        Node orderCreated = new Node("order_created", NodeType.TOPIC, "Deaf_Service", BrokerType.KAFKA);
        Node warehouseFull = new Node("warehouse_is_full", NodeType.TOPIC, "Deaf_Service", BrokerType.KAFKA);
        Node startTrip = new Node("start_trip", NodeType.TOPIC, "Deaf_Service", BrokerType.KAFKA);
        GRAPH.addNode(new Node("completed", NodeType.TOPIC, null, BrokerType.KAFKA));
        GRAPH.addNode(orderCreated);
        GRAPH.addNode(new Node("arrived", NodeType.TOPIC, null, BrokerType.KAFKA));
        GRAPH.addNode(new Node("good_is_booked", NodeType.TOPIC, null, BrokerType.KAFKA));
        GRAPH.addNode(warehouseFull);
        GRAPH.addNode(new Node("order_is_canceled", NodeType.TOPIC, null, BrokerType.KAFKA));
        GRAPH.addNode(new Node("car_is_ready", NodeType.TOPIC, null, BrokerType.KAFKA));
        GRAPH.addNode(new Node("open_the_door", NodeType.TOPIC, null, BrokerType.KAFKA));
        GRAPH.addNode(startTrip);
        GRAPH.addNode(new Node("close_the_door", NodeType.TOPIC, null, BrokerType.KAFKA));

        //add events to graph.
        Map<String, Schema> schemas = getSchemas();
        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            Event event = new Event(entry.getValue(), GRAPH.getEventColor(), entry.getKey());
            GRAPH.addEvent(event);
        }

        //add links to graph. order_created -> deaf_service, warehouse_is_full -> deaf_service ,start_trip -> deaf_service
        Link orderCreatedLink = Link.builder()
                .group("Deaf_Service")
                .from(orderCreated)
                .to(deafService)
                .event(GRAPH.getEvent("order_created"))
                .build();

        Link warehouseFullLink = Link.builder()
                .group("Deaf_Service")
                .from(warehouseFull)
                .to(deafService)
                .event(GRAPH.getEvent("warehouse_is_full"))
                .build();

        Link startTripLink = Link.builder()
                .group("Deaf_Service")
                .from(startTrip)
                .to(deafService)
                .event(GRAPH.getEvent("start_trip"))
                .build();
        GRAPH.addLink(orderCreatedLink);
        GRAPH.addLink(warehouseFullLink);
        GRAPH.addLink(startTripLink);
        return GRAPH;
    }
}
