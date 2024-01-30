package com.company.homeworkloans.screen.requestloan;

import com.company.homeworkloans.entity.Client;
import com.company.homeworkloans.entity.Loan;
import com.company.homeworkloans.entity.LoanStatus;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import liquibase.pro.packaged.S;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

@UiController("RequestLoanScreen")
@UiDescriptor("request-loan-screen.xml")
@DialogMode(forceDialog = true, width = "AUTO", height = "AUTO")
public class RequestLoanScreen extends Screen {

    @Autowired
    private TextField loanAmount;
    @Autowired
    private EntityComboBox<Client> clients;

    private Client selectedClient;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private ScreenValidation screenValidation;
    @Autowired
    private Form loanForm;

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        clients.setValue(selectedClient);
        loanAmount.setValue(new BigDecimal(1000));
    }

    @Subscribe("requestBtn")
    public void onRequestBtnClick(final Button.ClickEvent event) {
        ValidationErrors errors = screenValidation.validateUiComponents(loanForm);
        if (!errors.getAll().isEmpty()) {
            StringBuilder errorsNotification = new StringBuilder();
            for (ValidationErrors.Item validationItem : errors.getAll()) {
                errorsNotification.append(validationItem.description).append("\n");
            }
            notifications.create()
                    .withType(Notifications.NotificationType.TRAY)
                    .withCaption("Please, enter correct values")
                    .withDescription(errorsNotification.toString())
                    .show();
            return;
        }
        Loan loan = dataManager.create(Loan.class);

        loan.setAmount((BigDecimal) loanAmount.getValue());
        loan.setClient(clients.getValue());
        loan.setRequestDate(LocalDate.now());
        loan.setStatus(LoanStatus.REQUESTED);

        dataManager.save(loan);
        close(StandardOutcome.COMMIT);
    }

    @Subscribe("cancelBtn")
    public void onCancelBtnClick(final Button.ClickEvent event) {
        close(StandardOutcome.CLOSE);
    }
}