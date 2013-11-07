package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.data.search.TransactionSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListListingHistoryService;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ListListingHistoryVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingHistoryVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#listingStatusCombo")
	private Combobox listingStatusCombo;
	@Wire("#categoriesDiv")
	private Div categoriesDiv;
	@Wire("#parentCategoryCombo")
	private Combobox parentCategoryCombo;
	@Wire("#typesCombo")
	private Combobox typesCombo;
	@Wire("#subcategoryLabel")
	private Label subcategoryLabel;
	@Wire("#appliances")
	private Comboitem appliances;
	@Wire("#electronics")
	private Comboitem electronics;
	@Wire("#furniture")
	private Comboitem furniture;
	@Wire("#books")
	private Comboitem books;
	@Wire("#various")
	private Comboitem various;
	@Wire("#all")
	private Comboitem all;
	@Wire("#completed")
	private Comboitem completed;
	@Wire("#enabled")
	private Comboitem enabled;
	@Wire("#disabled")
	private Comboitem disabled;

	@WireVariable
	private ListListingHistoryService listListingHistoryService;

	private TransactionSearchCriteria transactionSearchCriteria;
	private ListModelList<Transaction> transactionsList;
	private Transaction selectedTransaction;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ProductCategory selectedCategory;
	private TransactionType selectedTransactionType;
	private Integer selectedListingStatus;
	private Integer parentCategoryId;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if (ZKSession.getAttribute("transactionSearchCriteria") != null) {
			transactionSearchCriteria = (TransactionSearchCriteria) ZKSession.getAttribute("transactionSearchCriteria");
			ZKSession.removeAttribute("transactionSearchCriteria");

			if ((transactionSearchCriteria.getProductCategory() != null) && transactionSearchCriteria.getProductCategory().getParentCategoryId() != null) {
				populateParentCategories(transactionSearchCriteria.getProductCategory().getParentCategoryId());
				categoriesDiv.setVisible(true);
				getCategories();
				categories = new ListModelList<>();
				categories.addAll(getCategoriesByParentCategoryId(transactionSearchCriteria.getProductCategory().getParentCategoryId()));
				selectedCategory = transactionSearchCriteria.getProductCategory();
			}

			getTransactionTypes();
			selectedTransactionType = transactionSearchCriteria.getTransactionType();
			selectedTransaction = null;

			transactionsList = new ListModelList<>();

			try {
				SearchResults<Transaction> results = listListingHistoryService
						.searchTransactionsByCriteria(transactionSearchCriteria);
				Collection<Transaction> transactions = results.getData();
				transactionsList.addAll(transactions);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(transactionSearchCriteria.getPageNumber());
				if (transactionsList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
		else {
			//Initial search criteria
			transactionSearchCriteria = new TransactionSearchCriteria();
			paging.setPageSize(10);
			transactionSearchCriteria.setPageSize(paging.getPageSize());
			transactionSearchCriteria.setPageNumber(0);
			transactionSearchCriteria.setUser(loggedUser);
			transactionSearchCriteria.setOrderField("transactionDate");
			transactionSearchCriteria.setOrderDirection("descending");

			transactionsList = new ListModelList<>();

			try {
				SearchResults<Transaction> results = listListingHistoryService
						.searchTransactionsByCriteria(transactionSearchCriteria);
				Collection<Transaction> transactions = results.getData();
				transactionsList.addAll(transactions);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(transactionSearchCriteria.getPageNumber());
				if (transactionsList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
	}

	public void populateParentCategories(int categoryId) {

		switch (categoryId) {
			case 1:
				parentCategoryCombo.setSelectedItem(appliances);
				break;
			case 2:
				parentCategoryCombo.setSelectedItem(electronics);
				break;
			case 3:
				parentCategoryCombo.setSelectedItem(furniture);
				break;
			case 4:
				parentCategoryCombo.setSelectedItem(books);
				break;
			case 5:
				parentCategoryCombo.setSelectedItem(various);
				break;
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {

		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		String sortField = listheader.getId();
		switch (sortField) {
			case "transactionDate":
				transactionSearchCriteria.setOrderField(sortField);
				break;
			case "owner":
				transactionSearchCriteria.setOrderField("list.user");
				break;
			case "productCategory":
				transactionSearchCriteria.setOrderField("list.productCategory");
				break;
			case "productName":
				transactionSearchCriteria.setOrderField("list.productName");
				break;
			case "transactionType":
				transactionSearchCriteria.setOrderField("list.transactionType");
				break;
		}

		transactionSearchCriteria.setOrderDirection(listheader.getSortDirection());
		transactionSearchCriteria.setPageNumber(0);
		selectedTransaction = null;
		transactionsList.clear();

		try {
			SearchResults<Transaction> results = listListingHistoryService
					.searchTransactionsByCriteria(transactionSearchCriteria);
			Collection<Transaction> transactions = results.getData();
			transactionsList.addAll(transactions);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(transactionSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange("selectedTransaction")
	public void onPaging() {

		if (transactionsList != null) {
			transactionSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Transaction> results = listListingHistoryService
						.searchTransactionsByCriteria(transactionSearchCriteria);
				selectedTransaction = null;
				transactionsList.clear();
				transactionsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(transactionSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
	}

	@Command
	@NotifyChange("selectedTransaction")
	public void onSearch() {

			selectedTransaction = null;
			transactionsList.clear();
			transactionSearchCriteria.setPageNumber(0);
			transactionSearchCriteria.setPageSize(paging.getPageSize());
			if (selectedCategory != null) {
				transactionSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
			}
			transactionSearchCriteria
					.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);

			try {
				SearchResults<Transaction> results = listListingHistoryService
						.searchTransactionsByCriteria(transactionSearchCriteria);
				Collection<Transaction> transactions = results.getData();
				transactionsList.addAll(transactions);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(transactionSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
	}

	@Command
	@NotifyChange({"selectedCategory", "selectedTransactionType", "selectedTransaction", "transactionSearchCriteria"})
	public void onResetSearch() {
		if (categories != null) {
			selectedCategory = categories.get(0);
		}
		selectedTransactionType = transactionTypes.get(0);
		transactionSearchCriteria.setListingKeyword(null);
		transactionSearchCriteria.setDateFrom(null);
		transactionSearchCriteria.setDateTo(null);
		transactionSearchCriteria.setCompleted(true);
		transactionSearchCriteria.setListingEnabled(null);
		transactionSearchCriteria.setListingCode(null);
		listingStatusCombo.setValue(null);
		selectedTransaction = null;
		parentCategoryCombo.setSelectedItem(null);
		subcategoryLabel.setValue("");
		transactionSearchCriteria.setParentCategoryId(0);
	}

	@Command
	public void onView() {
		if (selectedTransaction != null) {
			if (selectedTransaction.getCompleted()) {
				ZKSession.setAttribute("transactionId", selectedTransaction.getId());
				transactionSearchCriteria.setProductCategory(selectedCategory);
				transactionSearchCriteria.setTransactionType(selectedTransactionType);
				ZKSession.setAttribute("transactionSearchCriteria", transactionSearchCriteria);
				ZKSession.sendRedirect(PageURL.LISTING_HISTORY_VIEW);
			} else {
				ZKSession.setAttribute("listingId", selectedTransaction.getListing().getId());
				ZKSession.setAttribute("fromHistory", true);
				transactionSearchCriteria.setProductCategory(selectedCategory);
				transactionSearchCriteria.setTransactionType(selectedTransactionType);
				ZKSession.setAttribute("transactionSearchCriteria", transactionSearchCriteria);
				ZKSession.sendRedirect(PageURL.LISTING_VIEW);
			}
		}
	}

	@Command
	public void onRate() {

		if (selectedTransaction != null) {
			if (selectedTransaction.getListingInterest() != null) {
				ZKSession.setAttribute("transaction", selectedTransaction);
				ZKSession.sendRedirect(PageURL.LISTING_RATE);
			}
			else {
				Messagebox.show(MessageBuilder.buildErrorMessage(Labels.getLabel("transaction.notCompletedError"), Labels.getLabel("transaction")),
						Labels.getLabel("transaction.rating"), Messagebox.OK, Messagebox.EXCLAMATION);
			}
		}
	}

	@Command
	public void onThumbnail() {
		try {
			ZKSession.setAttribute("aImage", selectedTransaction.getListing().getListingMainImage().getImage());
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Window window = (Window) Executions.createComponents("/zul/trg/image_view.zul", null, null);
		window.doModal();
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Transaction> results = null;
		transactionSearchCriteria.setPageSize(0);
		transactionSearchCriteria.setPageSize(0);
		try {
			results = listListingHistoryService.searchTransactionsByCriteria(transactionSearchCriteria);
			for (Transaction transaction : results.getData()) {
				if (transaction.getListing().getListingMainImage() != null) {
					transaction.getListing().setReportImage(
							new ImageIcon(transaction.getListing().getListingMainImage().getImageBytes()));
				} else {
					transaction.getListing().setReportImage(new ImageIcon());
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<Transaction> transactions = results.getData();

		Report report = ReportToolkit.requestTransactionsReport(transactions, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	@Command
	public void onSelectListingStatus() {
		switch (listingStatusCombo.getValue()) {
			case "":
				transactionSearchCriteria.setCompleted(true);
				transactionSearchCriteria.setListingEnabled(null);
				break;
			case "Όλες":
				transactionSearchCriteria.setCompleted(null);
				transactionSearchCriteria.setListingEnabled(null);
				break;
			case "Ολοκληρωμένες":
				transactionSearchCriteria.setCompleted(true);
				transactionSearchCriteria.setListingEnabled(null);
				break;
			case "Ανοικτές/Ενεργές":
				transactionSearchCriteria.setCompleted(null);
				transactionSearchCriteria.setListingEnabled(true);
				break;
			case "Ανοικτές/Ανενεργές":
				transactionSearchCriteria.setCompleted(false);
				transactionSearchCriteria.setListingEnabled(false);
				break;
		}
	}

	@Command
	@NotifyChange("categories")
	public void onSelectParentCategory() {
		if (parentCategoryCombo.getValue() != "") {
			categoriesDiv.setVisible(true);
			parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex();
			transactionSearchCriteria.setParentCategoryId(parentCategoryId);
			getCategoriesByParentCategoryId(parentCategoryId);
		}
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		selectedCategory = new ProductCategory();
		selectedCategory.setId(- 1);
		selectedCategory.setName("");
		categories.add(selectedCategory);
		try {
			categories.addAll(listListingHistoryService.getProductCategoriesByParentCategoryId(parentCategoryId));
		} catch (ServiceException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}

		return categories;
	}

	public ListModelList<Transaction> getTransactionsList() {
		return transactionsList;
	}

	public void setTransactionsList(ListModelList<Transaction> transactionsList) {
		this.transactionsList = transactionsList;
	}

	public Transaction getSelectedTransaction() {
		return selectedTransaction;
	}

	public void setSelectedTransaction(Transaction selectedTransaction) {
		this.selectedTransaction = selectedTransaction;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public ListModelList<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(ListModelList<ProductCategory> categories) {
		this.categories = categories;
	}

	public ListModelList<TransactionType> getTransactionTypes() {
		if (transactionTypes == null) {
			transactionTypes = new ListModelList<>();
			selectedTransactionType = new TransactionType();
			selectedTransactionType.setId(- 1);
			selectedTransactionType.setName("");
			transactionTypes.add(selectedTransactionType);
			try {
				transactionTypes.addAll(listListingHistoryService.getTransactionTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.transactionType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return transactionTypes;
	}

	public ProductCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ProductCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public TransactionType getSelectedTransactionType() {
		return selectedTransactionType;
	}

	public void setSelectedTransactionType(TransactionType selectedTransactionType) {
		this.selectedTransactionType = selectedTransactionType;
	}

	public TransactionSearchCriteria getTransactionSearchCriteria() {
		return transactionSearchCriteria;
	}

	public void setTransactionSearchCriteria(TransactionSearchCriteria transactionSearchCriteria) {
		this.transactionSearchCriteria = transactionSearchCriteria;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public Integer getSelectedListingStatus() {
		return selectedListingStatus;
	}

	public void setSelectedListingStatus(Integer selectedListingStatus) {
		this.selectedListingStatus = selectedListingStatus;
	}

	public Integer getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
}
