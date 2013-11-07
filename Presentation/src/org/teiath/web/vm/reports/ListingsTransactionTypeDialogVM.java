package org.teiath.web.vm.reports;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListingsTransactionTypeDialogService;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class ListingsTransactionTypeDialogVM {

	static Logger log = Logger.getLogger(ListingsTransactionTypeDialogVM.class.getName());

	@WireVariable
	private ListingsTransactionTypeDialogService listingsTransactionTypeDialogService;

	private ListModelList<TransactionType> transactionTypes;
	private TransactionType selectedTransactionType;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onPrintPDF() {
		if (selectedTransactionType != null) {
			Report report = ReportToolkit
					.requestListingsTransactionTypeReport(selectedTransactionType.getId(), ReportType.PDF);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} else {
			Messagebox.show("Μη έγκυρος τύπος συναλλαγής", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onPrintXLS() {
		if (selectedTransactionType != null) {
			Report report = ReportToolkit
					.requestListingsTransactionTypeReportXLS(selectedTransactionType, ReportType.EXCEL);
			report.setExcelReport(ExcelToolkit.LISTINGS_BY_TRANSACTION_TYPE);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} else {
			Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		//		ZKSession.sendRedirect(PageURL.ROUTE_LIST);
	}

	public ListModelList<TransactionType> getTransactionTypes() {
		if (transactionTypes == null) {
			transactionTypes = new ListModelList<>();
			selectedTransactionType = new TransactionType();
			selectedTransactionType.setId(- 1);
			selectedTransactionType.setName("");
			selectedTransactionType.setCode("-1");
			transactionTypes.add(selectedTransactionType);
			try {
				transactionTypes.addAll(listingsTransactionTypeDialogService.getTransactionTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.transactionType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return transactionTypes;
	}

	public TransactionType getSelectedTransactionType() {
		return selectedTransactionType;
	}

	public void setSelectedTransactionType(TransactionType selectedTransactionType) {
		this.selectedTransactionType = selectedTransactionType;
	}
}
