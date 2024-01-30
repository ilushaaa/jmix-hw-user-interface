package com.company.homeworkloans.screen.loan;

import com.company.homeworkloans.entity.LoanStatus;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import com.company.homeworkloans.entity.Loan;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Loan.approval")
@UiDescriptor("loan-approval.xml")
@LookupComponent("loansTable")
public class LoanApproval extends StandardLookup<Loan> {
    @Autowired
    private CollectionLoader<Loan> prevLoansDl;
    @Autowired
    private GroupTable<Loan> loansTable;
    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CollectionLoader<Loan> loansDl;

    @Subscribe(id = "loansDc", target = Target.DATA_CONTAINER)
    public void onLoansDcItemChange(final InstanceContainer.ItemChangeEvent<Loan> event) {
        Loan selectedLoan = event.getItem();
        if (selectedLoan == null) {
            return;
        }

        prevLoansDl.setQuery("select l from Loan l where l.client.id = :clientId and l.id <> :selectedLoanId");
        prevLoansDl.setParameter("clientId", selectedLoan.getClient().getId());
        prevLoansDl.setParameter("selectedLoanId", selectedLoan.getId());
        prevLoansDl.load();
    }

    @Subscribe("approve")
    public void onApproveClick(final Button.ClickEvent event) {
        applyLoanStatus(LoanStatus.APPROVED);
        updateLoaders();
    }

    @Subscribe("reject")
    public void onRejectClick(final Button.ClickEvent event) {
        applyLoanStatus(LoanStatus.REJECTED);
        updateLoaders();
    }

    private void updateLoaders() {
        loansDl.load();

        prevLoansDl.setQuery("select l from Loan l where l.client is null");
        prevLoansDl.removeParameter("clientId");
        prevLoansDl.removeParameter("selectedLoanId");
        prevLoansDl.load();
    }

    private void applyLoanStatus(LoanStatus loanStatus) {
        Loan selected = loansTable.getSingleSelected();
        if (selected == null) {
            notifications.create()
                    .withType(Notifications.NotificationType.TRAY)
                    .withCaption("Please, select a loan")
                    .show();
            return;
        }
        selected.setStatus(loanStatus);
        dataManager.save(selected);
    }
}