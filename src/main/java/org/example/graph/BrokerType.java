package org.example.graph;

public enum BrokerType {
    KAFKA ("kafka"), JMS ("jms"), RABBITMQ("rabbit");

    private final String value;

    BrokerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BrokerType fromValue(String value) {
        for (BrokerType type : BrokerType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
