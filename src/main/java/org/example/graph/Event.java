package org.example.graph;

import io.swagger.v3.oas.models.media.Schema;
import lombok.*;
import org.example.demojavafx.datamodel.Color;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class Event {
    private Schema schema;
    private Color color;
    private String name;
}
