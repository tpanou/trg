package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListListingInboxInterestsService;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ListListingInboxInterestsVM
		extends BaseVM

{

	static Logger log = Logger.getLogger(ListListingsVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;

	@WireVariable
	private ListListingInboxInterestsService listListingInboxInterestsService;

	private ListingInterestSearchCriteria listingInterestSearchCriteria;
	private ListModelList<ListingInterest> listingInterestsList;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ProductCategory selectedCategory;
	private TransactionType selectedTransactionType;
	private ListingInterest selectedListingInterest;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Initial search criteria
		listingInterestSearchCriteria = new ListingInterestSearchCriteria();
		paging.setPageSize(10);
		listingInterestSearchCriteria.setPageSize(paging.getPageSize());
		listingInterestSearchCriteria.setPageNumber(0);
		listingInterestSearchCriteria.setUser(loggedUser);
		listingInterestSearchCriteria.setStatus(ListingInterest.STATUS_PENDING);
		listingInterestSearchCriteria.setOrderField("interestDate");
		listingInterestSearchCriteria.setOrderDirection("descending");

		listingInterestsList = new ListModelList<>();

		try {
			SearchResults<ListingInterest> results = listListingInboxInterestsService
					.searchListingInterestsByCriteria(listingInterestSearchCriteria);
			Collection<ListingInterest> listingInterests = results.getData();
			listingInterestsList.addAll(listingInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
			if (listingInterestsList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("route")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {

		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		String sortField = listheader.getId();
		switch (sortField) {
			case "interestDate":
				listingInterestSearchCriteria.setOrderField("interestDate");
				break;
			case "rating":
				listingInterestSearchCriteria.setOrderField("usr.averageTransactionRating");
				break;
		}

		listingInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		listingInterestSearchCriteria.setPageNumber(0);
		listingInterestsList.clear();

		try {
			SearchResults<ListingInterest> results = listListingInboxInterestsService
					.searchListingInterestsByCriteria(listingInterestSearchCriteria);
			Collection<ListingInterest> listingInterests = results.getData();
			listingInterestsList.addAll(listingInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
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
				SearchResults<ListingInterest> results = listListingInboxInterestsService
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
	public void onView() {
		if (selectedListingInterest != null) {
			ZKSession.setAttribute("listingInterestId", selectedListingInterest.getId());
			ZKSession.setAttribute("fromSubmenu", true);
			ZKSession.sendRedirect(PageURL.LISTING_INTEREST_VIEW_SELLER);
		}
	}

	@Command
	public void onAccept() {

		if (selectedListingInterest != null) {
			Messagebox.show(Labels.getLabel("listingInterest.message.approveQuestion"),
					Labels.getLabel("commmon.messages.approve_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								//den ginetai na mpoun stin idia methodo service dioti mplekoun ta references sto session
								//tou hibernate
								listListingInboxInterestsService
										.rejectListingInterests(selectedListingInterest.getListing());
								listListingInboxInterestsService.approveListingInterest(selectedListingInterest);
								Messagebox.show(Labels.getLabel("listingInterest.message.approveConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.INCOMING_LISTING_INTERESTS);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.INCOMING_LISTING_INTERESTS);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onReject() {
		if (selectedListingInterest != null) {
			Messagebox.show(Labels.getLabel("listingInterest.message.rejectQuestion"),
					Labels.getLabel("commmon.messages.reject_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listListingInboxInterestsService.rejectListingInterest(selectedListingInterest);
								Messagebox.show(Labels.getLabel("listingInterest.message.rejectConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.INCOMING_LISTING_INTERESTS);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.INCOMING_LISTING_INTERESTS);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<ListingInterest> results = null;
		listingInterestSearchCriteria.setPageSize(0);
		listingInterestSearchCriteria.setPageNumber(0);
		try {
			results = listListingInboxInterestsService.searchListingInterestsByCriteria(listingInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<ListingInterest> listingInterests = results.getData();

		Report report = ReportToolkit.requestIncomingListingInterestsReport(listingInterests, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public static Logger getLog() {
		return log;
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
}
