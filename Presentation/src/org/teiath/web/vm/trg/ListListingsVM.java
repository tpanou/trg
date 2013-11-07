package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListListingsService;
import org.teiath.web.facebook.trg.FacebookToolKitListings;
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

@SuppressWarnings("UnusedDeclaration")
public class ListListingsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingsVM.class.getName());

	@Wire("#listingListWin")
	private Window listingListWin;
	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
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
	private ListListingsService listListingsService;

	private ListingSearchCriteria listingSearchCriteria;
	private ListModelList<Listing> listingsList;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ProductCategory selectedCategory;
	private TransactionType selectedTransactionType;
	private Listing selectedListing;
	private Integer parentCategoryId;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		if (ZKSession.getAttribute("myListingsSearchCriteria") != null) {
			listingSearchCriteria = (ListingSearchCriteria) ZKSession.getAttribute("myListingsSearchCriteria");
			ZKSession.removeAttribute("myListingsSearchCriteria");

			if ((listingSearchCriteria.getProductCategory() != null) && listingSearchCriteria.getProductCategory().getParentCategoryId() != null) {
				populateParentCategories(listingSearchCriteria.getProductCategory().getParentCategoryId());
				categoriesDiv.setVisible(true);
				getCategories();
				categories = new ListModelList<>();
				categories.addAll(getCategoriesByParentCategoryId(listingSearchCriteria.getProductCategory().getParentCategoryId()));
				selectedCategory = listingSearchCriteria.getProductCategory();
			}

			getTransactionTypes();
			selectedTransactionType = listingSearchCriteria.getTransactionType();
			selectedListing = null;


			listingsList = new ListModelList<>();
			SearchResults<Listing> results = null;
			try {
				results = listListingsService.searchListingsByCriteria(listingSearchCriteria);
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
			Collection<Listing> listings = results.getData();
			listingsList.addAll(listings);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingSearchCriteria.getPageNumber());
			if (listingsList.isEmpty()) {
				empty.setVisible(true);
			}
		}
		else {

			//Initial search criteria
			listingSearchCriteria = new ListingSearchCriteria();
			paging.setPageSize(10);
			listingSearchCriteria.setPageSize(paging.getPageSize());
			listingSearchCriteria.setPageNumber(0);
			listingSearchCriteria.setActive(true);
			listingSearchCriteria.setUser(loggedUser);
			listingSearchCriteria.setOrderField("listingCreationDate");
			listingSearchCriteria.setOrderDirection("descending");
			listingsList = new ListModelList<>();

			try {
				SearchResults<Listing> results = listListingsService.searchListingsByCriteria(listingSearchCriteria);
				Collection<Listing> listings = results.getData();
				listingsList.addAll(listings);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingSearchCriteria.getPageNumber());
				if (listingsList.isEmpty()) {
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

		listingSearchCriteria.setOrderField(listheader.getId());
		listingSearchCriteria.setOrderDirection(listheader.getSortDirection());
		listingSearchCriteria.setPageNumber(0);
		selectedListing = null;
		listingsList.clear();

		try {
			SearchResults<Listing> results = listListingsService.searchListingsByCriteria(listingSearchCriteria);
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
				SearchResults<Listing> results = listListingsService.searchListingsByCriteria(listingSearchCriteria);
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
	@NotifyChange("selectedListing")
	public void onSearch() {

			subcategoryLabel.setValue("");
			selectedListing = null;
			listingsList.clear();
			listingSearchCriteria.setPageNumber(0);
			listingSearchCriteria.setPageSize(paging.getPageSize());
			if (selectedCategory != null) {
				listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
			}
			listingSearchCriteria
					.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
			listingSearchCriteria.setSortOrder(Integer.parseInt(orderBy.getSelectedItem().getValue().toString()));

			try {
				SearchResults<Listing> results = listListingsService.searchListingsByCriteria(listingSearchCriteria);
				Collection<Listing> listings = results.getData();
				listingsList.addAll(listings);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingSearchCriteria.getPageNumber());
				if (listings.isEmpty()) {
					Messagebox.show(MessageBuilder
							.buildErrorMessage(Labels.getLabel("listing.noListings"), Labels.getLabel("listing")),
							Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
	}

	@Command
	@NotifyChange({"selectedCategory", "selectedTransactionType", "selectedListing", "listingSearchCriteria"})
	public void onResetSearch() {
		if (categories != null) {
			selectedCategory = categories.get(0);
		}
		selectedTransactionType = transactionTypes.get(0);
		listingSearchCriteria.setListingKeyword(null);
		listingSearchCriteria.setCode(null);
		parentCategoryCombo.setSelectedItem(null);
		subcategoryLabel.setValue("");
		listingSearchCriteria.setParentCategoryId(0);
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("listingId", selectedListing.getId());
		listingSearchCriteria.setProductCategory(selectedCategory);
		listingSearchCriteria.setTransactionType(selectedTransactionType);
		ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
		ZKSession.sendRedirect(PageURL.LISTING_VIEW);
	}

	@Command
	public void onEdit() {
		ZKSession.setAttribute("listingId", selectedListing.getId());
		if (selectedCategory != null)
			listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
		if (selectedTransactionType != null)
			listingSearchCriteria.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
		ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
		ZKSession.sendRedirect(PageURL.LISTING_EDIT);
	}

	@Command
	public void onCreate() {
		if (selectedCategory != null)
			listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
		if (selectedTransactionType != null)
			listingSearchCriteria.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
		ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
		ZKSession.sendRedirect(PageURL.LISTING_CREATE);
	}

	@Command
	public void onDelete() {
		if (selectedListing != null) {
			Messagebox.show(Labels.getLabel("listing.message.deleteQuestion"), Labels.getLabel("listing"),
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listListingsService.deleteListing(selectedListing);
								Messagebox.show(Labels.getLabel("listing.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										if (selectedCategory != null)
											listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
										if (selectedTransactionType != null)
											listingSearchCriteria.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
										ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
										ZKSession.sendRedirect(PageURL.LISTING_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								if (selectedCategory != null)
									listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
								if (selectedTransactionType != null)
									listingSearchCriteria.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
								ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
								ZKSession.sendRedirect(PageURL.LISTING_LIST);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onInterests() {

		if ((selectedListing != null) && (selectedListing.getActive())) {
			ZKSession.setAttribute("selectedListing", selectedListing);
			if (selectedCategory != null)
				listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
			if (selectedTransactionType != null)
				listingSearchCriteria.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
			ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
			ZKSession.sendRedirect(PageURL.LISTING_INTERESTS);
		}
	}

	@Command
	public void facebookShare() {
		if (selectedListing != null) {
			//stelnoume to listing id ws state wste na mas stalei pisw apo to facebook
//			ZKSession.sendPureRedirect(
//					FacebookToolKitListings.getLoginRedirectURL() + "&state=" + selectedListing.getId());
			ZKSession.fireNewWindow(FacebookToolKitListings.getLoginRedirectURL() + "&state=" + selectedListing.getId());

		}
	}

	@Command
	public void onCopy() {
		ZKSession.setAttribute("listingId", selectedListing.getId());
		if (selectedCategory != null)
			listingSearchCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
		if (selectedTransactionType != null)
			listingSearchCriteria.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
		ZKSession.setAttribute("myListingsSearchCriteria", listingSearchCriteria);
		ZKSession.sendRedirect(PageURL.LISTING_COPY);
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Listing> results = null;
		listingSearchCriteria.setPageNumber(0);
		listingSearchCriteria.setPageSize(0);
		try {
			results = listListingsService.searchListingsByCriteria(listingSearchCriteria);
			for (Listing listing : results.getData()) {
				if (listing.getListingMainImage() != null) {
					listing.setReportImage(new ImageIcon(listing.getListingMainImage().getImageBytes()));
				} else {
					listing.setReportImage(new ImageIcon());
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<Listing> listings = results.getData();

		Report report = ReportToolkit.requestSellerListingsReport(listings, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	@Command
	public void onThumbnail() {
		try {
			ZKSession.setAttribute("aImage", selectedListing.getListingMainImage().getImage());
			Executions.createComponents("/zul/trg/image_view.zul", listingListWin, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public ListingSearchCriteria getListingSearchCriteria() {
		return listingSearchCriteria;
	}

	public void setListingSearchCriteria(ListingSearchCriteria listingSearchCriteria) {
		this.listingSearchCriteria = listingSearchCriteria;
	}

	public ListModelList<Listing> getListingsList() {
		return listingsList;
	}

	public void setListingsList(ListModelList<Listing> listingsList) {
		this.listingsList = listingsList;
	}

	public Listing getSelectedListing() {
		return selectedListing;
	}

	public void setSelectedListing(Listing selectedListing) {
		this.selectedListing = selectedListing;
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		selectedCategory = new ProductCategory();
		selectedCategory.setId(- 1);
		selectedCategory.setName("");
		categories.add(selectedCategory);
		try {
			categories.addAll(listListingsService.getProductCategoriesByParentCategoryId(parentCategoryId));
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
			transactionTypes.add(selectedTransactionType);
			try {
				transactionTypes.addAll(listListingsService.getTransactionTypes());
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
