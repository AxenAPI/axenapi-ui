package org.example.demojavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.example.codegen.info.BrokerServer;
import org.example.codegen.info.BrokerServers;
import org.example.demojavafx.markers.BrokerServerRequire;

public class ChooseBrokerElement {
    private final BrokerServers brokerServers = BrokerServers.BROKER_SERVERS;
    private BrokerServerRequire require;
    @FXML
    private ComboBox<BrokerServer> brokerComboBox;

    private BrokerServer brokerServer;

    public void initialize() {
        brokerServers.getBrokerServers().forEach(broker -> brokerComboBox.getItems().add(broker));
    }

    public void handleBrokerSelection(ActionEvent event) {
        brokerServer = brokerComboBox.getSelectionModel().getSelectedItem();
        if(require != null) {
            require.setBrokerServer(brokerServer);
        }
    }

    public void setRequire(BrokerServerRequire createLink) {
        this.require = createLink;
    }
}
