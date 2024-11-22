package org.example.demojavafx.datamodel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"from", "to"})
public class LinkDataModel {
    private String from;
    private String to;
    private String EventName;
}
