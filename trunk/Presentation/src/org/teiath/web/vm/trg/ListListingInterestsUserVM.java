package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListListingInterestsUserService;
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
public class ListListingInterestsUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingsVM.class.getName());

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


	@WireVariable
	private ListListingInterestsUserService listListingInterestsUserService;

	private ListingInterestSearchCriteria listingInterestSearchCriteria;
	private ListModelList<ListingInterest> listingInterestsList;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ProductCategory selectedCategory;
	private TransactionType selectedTransactionType;
	private ListingInterest selectedListingInterest;
	private Integer parentCategoryId;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if (ZKSession.getAttribute("listingInterestSearchCriteria") != null) {
			listingInterestSearchCriteria = (ListingInterestSearchCriteria) ZKSession.getAttribute("listingInterestSearchCriteria");
			ZKSession.removeAttribute("listingInterestSearchCriteria");

			if ((listingInterestSearchCriteria.getProductCategory() != null) && listingInterestSearchCriteria.getProductCategory().getParentCategoryId() != null) {
				populateParentCategories(listingInterestSearchCriteria.getProductCategory().getParentCategoryId());
				categoriesDiv.setVisible(true);
				getCategories();
				categories = new ListModelList<>();
				categories.addAll(getCategoriesByParentCategoryId(listingInterestSearchCriteria.getProductCategory().getParentCategoryId()));
				selectedCategory = listingInterestSearchCriteria.getProductCategory();
			}

			getTransactionTypes();
			selectedTransactionType = listingInterestSearchCriteria.getTransactionType();
			selectedListingInterest = null;

			listingInterestsList = new ListModelList<>();

			try {
				SearchResults<ListingInterest> results = listListingInterestsUserService
						.searchListingInterestsByCriteria(listingInterestSearchCriteria);
				Collection<ListingInterest> listingInterests = results.getData();
				listingInterestsList.addAll(listingInterests);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
				if (listingInterestsList.isEmpty()) {
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
			listingInterestSearchCriteria = new ListingInterestSearchCriteria();
			paging.setPageSize(10);
			listingInterestSearchCriteria.setPageSize(paging.getPageSize());
			listingInterestSearchCriteria.setPageNumber(0);
			listingInterestSearchCriteria.setBuyer(loggedUser);
			listingInterestSearchCriteria.setStatus(ListingInterest.STATUS_PENDING);
			listingInterestSearchCriteria.setOrderField("interestDate");
			listingInterestSearchCriteria.setOrderDirection("descending");

			listingInterestsList = new ListModelList<>();

			try {
				SearchResults<ListingInterest> results = listListingInterestsUserService
						.searchListingInterestsByCriteria(listingInterestSearchCriteria);
				Collection<ListingInterest> listingInterests = results.getData();
				listingInterestsList.addAll(listingInterests);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
				if (listingInterestsList.isEmpty()) {
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
	@NotifyChange("selectedListingInterest")
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		listingInterestSearchCriteria.setOrderField("list." + listheader.getId());
		listingInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		listingInterestSearchCriteria.setPageNumber(0);
		selectedListingInterest = null;
		listingInterestsList.clear();

		try {
			SearchResults<ListingInterest> results = listListingInterestsUserService
					.searchListingInterestsByCriteria(listingInterestSearchCriteria);
			Collection<ListingInterest> listingInterests = results.getData();
			listingInterestsList.addAll(listingInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingIntrest")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange("selectedListingInterest")
	public void onPaging() {
		if (listingInterestsList != null) {
			listingInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<ListingInterest> results = listListingInterestsUserService
						.searchListingInterestsByCriteria(listingInterestSearchCriteria);
				selectedListingInterest = null;
				listingInterestsList.clear();
				listingInterestsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
	}

	@Command
	@NotifyChange("selectedListing")
	public void onSearch() {

			selectedListingInterest = null;
			listingInterestsList.clear();
			listingInterestSearchCriteria.setPageNumber(0);
			listingInterestSearchCriteria.setPageSize(paging.getPageSize());
			if (selectedCategory != null) {
				listingInterestSearchCriteria
						.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
			}
			listingInterestSearchCriteria
					.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);

			try {
				SearchResults<ListingInterest> results = listListingInterestsUserService
						.searchListingInterestsByCriteria(listingInterestSearchCriteria);
				Collection<ListingInterest> listingInterests = results.getData();
				listingInterestsList.addAll(listingInterests);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
				if (listingInterests.isEmpty()) {
					Messagebox.show(MessageBuilder.buildErrorMessage(Labels.getLabel("listingInterest.noInterest"),
							Labels.getLabel("listingInterest")), Labels.getLabel("common.messages.search"),
							Messagebox.OK, Messagebox.INFORMATION);
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
	}

	@Command
	@NotifyChange(
			{"selectedCategory", "selectedTransactionType", "selectedListingInterest", "listingInterestSearchCriteria"})
	public void onResetSearch() {
		if (categories != null) {
			selectedCategory = categories.get(0);
		}
		selectedTransactionType = transactionTypes.get(0);
		listingInterestSearchCriteria.setListingKeyword(null);
		listingInterestSearchCriteria.setListingCode(null);
		selectedListingInterest = null;
		listingInterestSearchCriteria.setPageNumber(0);
		listingInterestSearchCriteria.setPageSize(paging.getPageSize());
		parentCategoryCombo.setSelectedItem(null);
		subcategoryLabel.setValue("");
		listingInterestSearchCriteria.setParentCategoryId(0);
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("listingId", selectedListingInterest.getListing().getId());
		listingInterestSearchCriteria.setProductCategory(selectedCategory);
		listingInterestSearchCriteria.setTransactionType(selectedTransactionType);
		ZKSession.setAttribute("listingInterestSearchCriteria", listingInterestSearchCriteria);
		ZKSession.sendRedirect(PageURL.LISTING_INTEREST_VIEW);
	}

	@Command
	@NotifyChange("selectedListingInterest")
	public void onWithdraw() {
		if (selectedListingInterest != null) {
			Messagebox.show(Labels.getLabel("listingInterest.message.withdrawQuestion"),
					Labels.getLabel("commmon.messages.withdraw_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listListingInterestsUserService.deleteListingInterest(selectedListingInterest);
								Messagebox.show(Labels.getLabel("listingInterest.message.withdrawConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.LISTING_INTERESTS_USER);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.LISTING_INTERESTS_USER);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onThumbnail() {
		try {
			ZKSession.setAttribute("aImage", selectedListingInterest.getListing().getListingMainImage().getImage());
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Window window = (Window) Executions.createComponents("/zul/trg/image_view.zul", null, null);
		window.doModal();
	}

	@Command
	public void onPrintPDF() {
		SearchResults<ListingInterest> results = null;
		listingInterestSearchCriteria.setPageSize(0);
		listingInterestSearchCriteria.setPageNumber(0);
		try {
			results = listListingInterestsUserService.searchListingInterestsByCriteria(listingInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<ListingInterest> listingInterests = results.getData();
		for (ListingInterest listingInterest : listingInterests) {
			if (listingInterest.getListing().getListingMainImage() != null) {
				listingInterest.getListing().setReportImage(
						new ImageIcon(listingInterest.getListing().getListingMainImage().getImageBytes()));
			} else {
				listingInterest.getListing().setReportImage(new ImageIcon());
			}
		}

		Report report = ReportToolkit.requestPersonalListingInterestsReport(listingInterests, ReportType.PDF);
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
			listingInterestSearchCriteria.setParentCategoryId(parentCategoryId);
		}
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		selectedCategory = new ProductCategory();
		selectedCategory.setId(- 1);
		selectedCategory.setName("");
		categories.add(selectedCategory);
		try {
			categories.addAll(listListingInterestsUserService.getProductCategoriesByParentCategoryId(parentCategoryId));
		} catch (ServiceException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}

		return categories;
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
				transactionTypes.addAll(listListingInterestsUserService.getTransactionTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.transactionType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return transactionTypes;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public ListingInterestSearchCriteria getListingInterestSearchCriteria() {
		return listingInterestSearchCriteria;
	}

	public void setListingInterestSearchCriteria(ListingInterestSearchCriteria listingInterestSearchCriteria) {
		this.listingInterestSearchCriteria = listingInterestSearchCriteria;
	}

	public ListModelList<ListingInterest> getListingInterestsList() {
		return listingInterestsList;
	}

	public void setListingInterestsList(ListModelList<ListingInterest> listingInterestsList) {
		this.listingInterestsList = listingInterestsList;
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

	public ListingInterest getSelectedListingInterest() {
		return selectedListingInterest;
	}

	public void setSelectedListingInterest(ListingInterest selectedListingInterest) {
		this.selectedListingInterest = selectedListingInterest;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public Integer getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
}
