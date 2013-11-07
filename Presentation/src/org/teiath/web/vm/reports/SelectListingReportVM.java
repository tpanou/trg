package org.teiath.web.vm.reports;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.SelectListingReportService;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class SelectListingReportVM
		extends BaseVM {

	static Logger log = Logger.getLogger(SelectListingReportVM.class.getName());

	@Wire("#typesCombo")
	Combobox typesCombo;
	@Wire("#dateFromRow")
	Row dateFromRow;
	@Wire("#dateToRow")
	Row dateToRow;
	@Wire("#transactionTypeRow")
	Row transactionTypeRow;
	@Wire("#productCategoryRow")
	Row productCategoryRow;
	@Wire("#parentCategoryCombo")
	Combobox parentCategoryCombo;


	@WireVariable
	private SelectListingReportService selectListingReportService;

	private Date dateFrom;
	private Date dateTo;
	private TransactionType selectedTransactionType;
	private ProductCategory selectedCategory;
	private ListModelList<TransactionType> transactionTypes;
	private ListModelList<ProductCategory> categories;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onSelectReportType() {

		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			dateFromRow.setVisible(true);
			dateToRow.setVisible(true);
			transactionTypeRow.setVisible(false);
			productCategoryRow.setVisible(false);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			dateFromRow.setVisible(false);
			dateToRow.setVisible(false);
			transactionTypeRow.setVisible(false);
			productCategoryRow.setVisible(true);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("2")) {
			dateFromRow.setVisible(false);
			dateToRow.setVisible(false);
			transactionTypeRow.setVisible(true);
			productCategoryRow.setVisible(false);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("3")) {
			dateFromRow.setVisible(true);
			dateToRow.setVisible(true);
			transactionTypeRow.setVisible(false);
			productCategoryRow.setVisible(false);
		}
	}

	@Command
	public void onPrintPDF() {

		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestCreatedListingsReport(dateFrom, dateTo, ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			if (selectedCategory != null) {
				Report report = ReportToolkit
						.requestListingsProductCategoryReport(selectedCategory.getId(), ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη κατηγορία προϊόντος", Labels.getLabel("common.messages.read_title"),
						Messagebox.OK, Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("2")) {
			if (selectedTransactionType != null) {
				Report report = ReportToolkit
						.requestListingsTransactionTypeReport(selectedTransactionType.getId(), ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρος τύπος συναλλαγής", Labels.getLabel("common.messages.read_title"),
						Messagebox.OK, Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("3")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestTransactionsReport(dateFrom, dateTo, ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
			;
		}
	}

	@Command
	public void onPrintXLS() {
		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestCreatedListingsReport(dateFrom, dateTo, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.CREATED_LISTINGS);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			if (selectedCategory != null) {
				Report report = ReportToolkit
						.requestListingsProductCategoryReportXLS(selectedCategory, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.LISTINGS_BY_PRODUCT_CATEGORY);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("2")) {
			if (selectedTransactionType != null) {
				Report report = ReportToolkit
						.requestListingsTransactionTypeReportXLS(selectedTransactionType, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.LISTINGS_BY_TRANSACTION_TYPE);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("3")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestTransactionsReport(dateFrom, dateTo, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.TRANSACTIONS);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("categories")
	public void onSelectParentCategory() {
		Integer parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex() + 1;
		getCategoriesByParentCategoryId(parentCategoryId);
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		try {
			categories.addAll(selectListingReportService.getProductCategoriesByParentCategoryId(parentCategoryId));
		} catch (ServiceException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}

		return categories;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public TransactionType getSelectedTransactionType() {
		return selectedTransactionType;
	}

	public void setSelectedTransactionType(TransactionType selectedTransactionType) {
		this.selectedTransactionType = selectedTransactionType;
	}

	public ProductCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ProductCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
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
				transactionTypes.addAll(selectListingReportService.getTransactionTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.transactionType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return transactionTypes;
	}

	public ListModelList<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(ListModelList<ProductCategory> categories) {
		this.categories = categories;
	}
}
