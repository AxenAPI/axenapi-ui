package org.example.codegen.info;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class BrokerServer {
    private int id;
    private String name;
    private String address;
    private String type;

    public BrokerServer(String name, String address, String type, int id) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BrokerServer that = (BrokerServer) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }


    @Override
    public String toString() {
        return name + ":" + address;
    }

}
