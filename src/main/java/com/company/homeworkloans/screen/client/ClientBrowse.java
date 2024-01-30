package com.company.homeworkloans.screen.client;

import com.company.homeworkloans.screen.requestloan.RequestLoanScreen;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.screen.*;
import com.company.homeworkloans.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Client.browse")
@UiDescriptor("client-browse.xml")
@LookupComponent("clientsTable")
public class ClientBrowse extends StandardLookup<Client> {
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private GroupTable<Client> clientsTable;

    @Subscribe("clientsTable.requestLoan")
    public void onClientsTableRequestLoan(final Action.ActionPerformedEvent event) {
        Client selectedClient = clientsTable.getSingleSelected();

        RequestLoanScreen requestLoanScreen = screenBuilders.screen(this)
                .withScreenClass(RequestLoanScreen.class)
                .build();
        requestLoanScreen.setSelectedClient(selectedClient);
        requestLoanScreen.show();
    }
}