package org.example.codegen.info;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public enum BrokerServers implements KafkaServersDAO {
    BROKER_SERVERS;
    private final File kafkaServersFile;
    private final Set<BrokerServer> brokerServers;

    BrokerServers() {
        //read file from resources
        kafkaServersFile = new File("kafka-servers.txt");
        brokerServers = new HashSet<>();
        //set up kafkaServers. Read from file if it exists.
        if (kafkaServersFile.exists()) {
            //read from file (line by line). format of line: name, address
            Scanner scanner = null;
            try {
                scanner = new Scanner(kafkaServersFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            int i = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(", ");
                if (parts.length == 3) {
                    String name = parts[0];
                    String address = parts[1];
                    String type = parts[2];
                    System.out.println("Name: " + name);
                    System.out.println("Address: " + address);
                    System.out.println("Type: " + type);
                    brokerServers.add(new BrokerServer(name, address, type, i));
                    i++;
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
            scanner.close();
        } else {
            //create empty file
            try {
                kafkaServersFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    @Override
    public Set<BrokerServer> kafkaAddresses() {
        return brokerServers;
    }

    @Override
    public void addKafkaServer(BrokerServer brokerServer) throws IOException {
        boolean add = brokerServers.add(brokerServer);
        if(add) {
            syncFile();
        }
    }

    @Override
    public void delete(String address) throws IOException {
        boolean b = brokerServers.removeIf(brokerServer -> brokerServer.getAddress().equals(address));
        if(b) {
            syncFile();
        }
    }

    @Override
    public void delete(BrokerServer brokerServer) throws IOException {
        boolean remove = brokerServers.remove(brokerServer);
        if(remove) {
            syncFile();
        }
    }

    private void syncFile() throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("kafka-servers", ".tmp");
        // write the kafkaServers to the temporary file
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            for (BrokerServer brokerServer : brokerServers) {
                writer.write(brokerServer.getName() + ", " + brokerServer.getAddress());
                if(brokerServer.getType() != null) {
                    writer.write(", " + brokerServer.getType());
                }
                writer.newLine();
            }
        }

        // delete the original file
        if (kafkaServersFile.exists()) {
            kafkaServersFile.delete();
        }
        // replace the original file with the temporary file
        Files.move(tempFile, kafkaServersFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    public Set<BrokerServer> getBrokerServers() {
        return brokerServers;
    }

    public void saveOrUpdate(BrokerServer brokerServer) throws IOException {
        boolean newBroker = true;
        for (BrokerServer b : brokerServers) {
            if (b.getId() == brokerServer.getId()) {
                newBroker = false;
                break;
            }
        }
        if(newBroker) {
            this.addKafkaServer(brokerServer);
        } else {
            this.update(brokerServer);
        }
    }

    private void update(BrokerServer brokerServer) throws IOException {
        for (BrokerServer b : brokerServers) {
            if (b.getId() == brokerServer.getId()) {
                b.setName(brokerServer.getName());
                b.setAddress(brokerServer.getAddress());
                b.setType(brokerServer.getType());
                b.setId(brokerServer.getId());
                break;
            }
        }
        syncFile();
    }

    @Override
    public void clear() throws IOException {
        brokerServers.clear();
        syncFile();
    }
}
