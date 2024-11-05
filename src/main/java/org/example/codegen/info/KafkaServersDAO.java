package org.example.codegen.info;

import java.io.IOException;
import java.util.Set;

public interface KafkaServersDAO {
    Set<BrokerServer> kafkaAddresses();
    void addKafkaServer(BrokerServer brokerServer) throws IOException;
    void delete(String address) throws IOException;
    void delete(BrokerServer brokerServer) throws IOException;
    Set<BrokerServer> getBrokerServers();

    void saveOrUpdate(BrokerServer rowData) throws IOException;

    void clear() throws IOException;
}
