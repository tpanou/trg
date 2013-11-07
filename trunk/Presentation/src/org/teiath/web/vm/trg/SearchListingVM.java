package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.*;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.SearchListingsService;
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
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class SearchListingVM
		extends BaseVM {

	static Logger log = Logger.getLogger(SearchListingVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#listingsListbox")
	private Listbox listingsListbox;
	@Wire("#priceDiv")
	private Div priceDiv;
	@Wire("#toolbar")
	private Div toolbar;
	@Wire("#contributionAmountIntbox")
	private Intbox contributionAmountIntbox;
	@Wire("#inquiryButton")
	private Toolbarbutton inquiryButton;
	@Wire("#categoriesDiv")
	private Div categoriesDiv;
	@Wire("#parentCategoryCombo")
	private Combobox parentCategoryCombo;
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
	@Wire("#orderBy")
	private Listbox orderBy;

	@WireVariable
	private SearchListingsService searchListingsService;

	private ListingSearchCriteria listingSearchCriteria;
	private ListModelList<Listing> listingsList;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ListModelList<ProductStatus> productStatuses;
	private Listing selectedListing;
	private ProductCategory selectedCategory;
	private TransactionType selectedTransactionType;
	private ProductStatus selectedProductStatus;
	private ListingInterest listingInterest;
	private Integer parentCategoryId;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		listingsList = new ListModelList<>();
		//Initial search criteria
		if (ZKSession.getAttribute("listingSearchCriteria") != null) {
			listingSearchCriteria = (ListingSearchCriteria) ZKSession.getAttribute("listingSearchCriteria");

			if ((listingSearchCriteria.getProductCategory() != null) && listingSearchCriteria.getProductCategory().getParentCategoryId() != null) {
				populateParentCategories(listingSearchCriteria.getProductCategory().getParentCategoryId());
				categoriesDiv.setVisible(true);
				getCategories();
				categories = new ListModelList<>();
				categories.addAll(getCategoriesByParentCategoryId(listingSearchCriteria.getProductCategory().getParentCategoryId()));
				selectedCategory = listingSearchCriteria.getProductCategory();
			}

			getTransactionTypes();
			getProductStatuses();
			selectedTransactionType = listingSearchCriteria.getTransactionType();
			selectedProductStatus = listingSearchCriteria.getProductStatus();

			selectedListing = null;
			listingsList.clear();
			listingSearchCriteria.setPageSize(paging.getPageSize());
			listingSearchCriteria.setActive(true);
			listingSearchCriteria.setEnabled(true);

			if (loggedUser == null) {
				inquiryButton.setVisible(false);
			}

			try {
				SearchResults<Listing> results = searchListingsService.searchListings(listingSearchCriteria);
				Collection<Listing> listings = results.getData();
				listingsList.addAll(listings);

				if (results.getTotalRecords() != 0) {
					listingsListbox.setVisible(true);
					paging.setVisible(true);
					toolbar.setVisible(true);
				} else {
					listingsListbox.setVisible(false);
					paging.setVisible(false);
					toolbar.setVisible(false);
					Messagebox.show(MessageBuilder
							.buildErrorMessage(Labels.getLabel("listing.notFound"), Labels.getLabel("listing")),
							Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
				}

				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
			ZKSession.removeAttribute("listingSearchCriteria");
		} else {
			listingSearchCriteria = new ListingSearchCriteria();
			paging.setPageSize(10);
			listingSearchCriteria.setPageSize(paging.getPageSize());
			listingSearchCriteria.setPageNumber(0);
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
	@NotifyChange("listingSearchCriteria")
	public void onSearch() {

			selectedListing = null;
			listingsList.clear();
			listingSearchCriteria.setOrderField("listingCreationDate");
			listingSearchCriteria.setOrderDirection("descending");
			listingSearchCriteria.setPageNumber(0);
			listingSearchCriteria.setPageSize(paging.getPageSize());
			listingSearchCriteria.setActive(true);
			listingSearchCriteria.setProductCategory(selectedCategory);
			listingSearchCriteria.setProductStatus(selectedProductStatus);
			listingSearchCriteria.setTransactionType(selectedTransactionType);
			listingSearchCriteria.setEnabled(true);
			listingSearchCriteria.setSortOrder(Integer.parseInt(orderBy.getSelectedItem().getValue().toString()));

			if (loggedUser == null) {
				inquiryButton.setVisible(false);
			}

			try {
				SearchResults<Listing> results = searchListingsService.searchListings(listingSearchCriteria);
				Collection<Listing> listings = results.getData();
				listingsList.addAll(listings);

				if (results.getTotalRecords() != 0) {
					listingsListbox.setVisible(true);
					paging.setVisible(true);
					toolbar.setVisible(true);
				} else {
					listingsListbox.setVisible(false);
					paging.setVisible(false);
					toolbar.setVisible(false);
					Messagebox.show(MessageBuilder
							.buildErrorMessage(Labels.getLabel("listing.notFound"), Labels.getLabel("listing")),
							Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
				}

				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}

	}

	@Command
	@NotifyChange({"selectedCategory", "selectedTransactionType", "selectedProductStatus", "selectedListing",
			"listingSearchCriteria"})
	public void onResetSearch() {
		if (categories != null) {
			selectedCategory = categories.get(0);
		}
		selectedTransactionType = transactionTypes.get(0);
		selectedProductStatus = productStatuses.get(0);
		listingSearchCriteria.setListingKeyword(null);
		contributionAmountIntbox.setRawValue(null);
		listingSearchCriteria.setMaxAmount(null);
		listingSearchCriteria.setDateFrom(null);
		listingSearchCriteria.setDateTo(null);
		listingSearchCriteria.setProductCategory(null);
		listingSearchCriteria.setProductStatus(null);
		listingSearchCriteria.setTransactionType(null);
		listingSearchCriteria.setCode(null);
		parentCategoryCombo.setSelectedItem(null);
		subcategoryLabel.setValue("");
		listingSearchCriteria.setParentCategoryId(0);
	}

	@Command
	@NotifyChange("selectedListing")
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		listingSearchCriteria.setOrderField(listheader.getId());
		listingSearchCriteria.setOrderDirection(listheader.getSortDirection());
		listingSearchCriteria.setPageNumber(0);
		selectedListing = null;
		listingsList.clear();

		try {
			SearchResults<Listing> results = searchListingsService.searchListings(listingSearchCriteria);
			Collection<Listing> listings = results.getData();
			listingsList.addAll(listings);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange("selectedListing")
	public void onPaging() {
		if (listingsList != null) {
			listingSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Listing> results = searchListingsService.searchListings(listingSearchCriteria);
				selectedListing = null;
				listingsList.clear();
				listingsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
	}

	@Command
	public void togglePrice() {
		if (selectedTransactionType.getCode().equals("100")) {
			priceDiv.setVisible(true);
		} else {
			priceDiv.setVisible(false);
		}
	}

	@Command
	@NotifyChange("selectedListing")
	public void onSelectListing() {
		if (loggedUser != null) {

			if (selectedListing.getUser().getId() != loggedUser.getId()) {
				if (selectedListing.getListingInterests() != null) {
					for (ListingInterest listingInterest : selectedListing.getListingInterests()) {
						if (listingInterest.getUser().getId() == loggedUser.getId())
							inquiryButton.setVisible(false);
						else
							inquiryButton.setVisible(true);

					}
				}
			} else {
				inquiryButton.setVisible(false);
			}
		}
	}

	@Command
	public void onView() {
		if (selectedListing != null) {
			ZKSession.setAttribute("listingId", selectedListing.getId());
			listingSearchCriteria.setProductCategory(selectedCategory);
			listingSearchCriteria.setProductStatus(selectedProductStatus);
			listingSearchCriteria.setTransactionType(selectedTransactionType);
			ZKSession.setAttribute("listingSearchCriteria", listingSearchCriteria);
			ZKSession.sendRedirect(PageURL.SEARCH_LISTING_VIEW);
		}
	}

	@Command
	public void onInquiry() {

		if (selectedListing != null) {
			if (selectedListing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
				ZKSession.setAttribute("listingId", selectedListing.getId());
				ZKSession.sendRedirect(PageURL.LISTING_INQUIRY);
			} else {
				Messagebox.show(Labels.getLabel("listing.message.inquiryQuestion"), Labels.getLabel("listing"),
						Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event evt) {
						switch ((Integer) evt.getData()) {
							case Messagebox.YES:
								listingInterest = new ListingInterest();
								listingInterest.setInterestDate(new Date());
								listingInterest.setUser(loggedUser);
								listingInterest.setStatus(ListingInterest.STATUS_PENDING);
								listingInterest.setListing(selectedListing);
								try {
									searchListingsService.saveListingInterest(listingInterest);
									ZKSession.setAttribute("listingId", selectedListing.getId());
									Messagebox.show(Labels.getLabel("listing.message.inquiryConfirmation"),
											Labels.getLabel("common.messages.confirm"), Messagebox.OK,
											Messagebox.INFORMATION);
									break;
								} catch (ServiceException e) {
									log.error(e.getMessage());
									Messagebox.show(MessageBuilder
											.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
											Labels.getLabel("common.messages.inquiry_title"), Messagebox.OK,
											Messagebox.ERROR);
									ZKSession.sendRedirect(PageURL.SEARCH_LISTING);
								}
							case Messagebox.NO:
								break;
						}
					}
				});
			}
		}
	}

	@Command
	public void onThumbnail() {
		try {
			ZKSession.setAttribute("aImage", selectedListing.getListingMainImage().getImage());
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Window window = (Window) Executions.createComponents("/zul/trg/image_view.zul", null, null);
		window.doModal();
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Listing> results = null;
		listingSearchCriteria.setPageNumber(0);
		listingSearchCriteria.setPageSize(0);
		try {
			results = searchListingsService.searchListings(listingSearchCriteria);
			for (Listing listing : results.getData()) {
				if (listing.getListingMainImage() != null) {
					listing.setReportImage(new ImageIcon(listing.getListingMainImage().getImageBytes()));
				} else {
					listing.setReportImage(new ImageIcon());
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<Listing> listings = results.getData();

		Report report = ReportToolkit.requestSearchListingsReport(listings, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	@Command
	@NotifyChange("categories")
	public void onSelectParentCategory() {
		if (parentCategoryCombo.getValue() != "") {
			categoriesDiv.setVisible(true);
			parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex();
			getCategoriesByParentCategoryId(parentCategoryId);
			listingSearchCriteria.setParentCategoryId(parentCategoryId);
		}
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		selectedCategory = new ProductCategory();
		selectedCategory.setId(- 1);
		selectedCategory.setName("");
		categories.add(selectedCategory);
		try {
			categories.addAll(searchListingsService.getProductCategoriesByParentCategoryId(parentCategoryId));
		} catch (ServiceException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}

		return categories;
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
				transactionTypes.addAll(searchListingsService.getTransactionTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.transactionType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return transactionTypes;
	}

	public ListModelList<ProductStatus> getProductStatuses() {
		if (productStatuses == null) {
			productStatuses = new ListModelList<>();
			selectedProductStatus = new ProductStatus();
			selectedProductStatus.setId(- 1);
			selectedProductStatus.setName("");
			productStatuses.add(selectedProductStatus);
			try {
				productStatuses.addAll(searchListingsService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productStatus")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return productStatuses;
	}

	public ProductStatus getSelectedProductStatus() {
		return selectedProductStatus;
	}

	public void setSelectedProductStatus(ProductStatus selectedProductStatus) {
		this.selectedProductStatus = selectedProductStatus;
	}

	public ListModelList<Listing> getListingsList() {
		return listingsList;
	}

	public void setListingsList(ListModelList<Listing> listingsList) {
		this.listingsList = listingsList;
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

	public Listing getSelectedListing() {
		return selectedListing;
	}

	public void setSelectedListing(Listing selectedListing) {
		this.selectedListing = selectedListing;
	}

	public ListingSearchCriteria getListingSearchCriteria() {
		return listingSearchCriteria;
	}

	public void setListingSearchCriteria(ListingSearchCriteria listingSearchCriteria) {
		this.listingSearchCriteria = listingSearchCriteria;
	}

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public ListModelList<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(ListModelList<ProductCategory> categories) {
		this.categories = categories;
	}

	public Integer getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
}
