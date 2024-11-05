package org.example.util;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Server
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    private String specificationPath;
    private String port;
    private String name;
    private String brokerAddress;
}
