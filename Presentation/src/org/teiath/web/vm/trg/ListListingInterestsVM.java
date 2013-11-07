package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListListingInterestsService;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class ListListingInterestsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingInterestsVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;

	@WireVariable
	private ListListingInterestsService listListingInterestsService;

	private ListingInterestSearchCriteria listingInterestSearchCriteria;
	private ListModelList<ListingInterest> listingInterestsList;
	private ListingInterest selectedListingInterest;
	private Listing listing;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Initial search criteria
		listingInterestSearchCriteria = new ListingInterestSearchCriteria();
		listingInterestSearchCriteria.setPageSize(paging.getPageSize());
		listingInterestSearchCriteria.setPageNumber(0);
		listingInterestSearchCriteria.setListing((Listing) ZKSession.getAttribute("selectedListing"));
		listingInterestSearchCriteria.setOrderField("interestDate");
		listingInterestSearchCriteria.setOrderDirection("ascending");

		listingInterestsList = new ListModelList<>();

		try {
			SearchResults<ListingInterest> results = listListingInterestsService
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
			SearchResults<ListingInterest> results = listListingInterestsService
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
				SearchResults<ListingInterest> results = listListingInterestsService
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
	public void onBack() {
		ZKSession.sendRedirect(PageURL.LISTING_LIST);
	}

	@Command
	public void onView() {

		if (selectedListingInterest != null) {
			ZKSession.setAttribute("listingInterestId", selectedListingInterest.getId());
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
								listListingInterestsService
										.rejectListingInterests(selectedListingInterest.getListing());
								selectedListingInterest.getListing().getTransaction().setCompleted(true);
								selectedListingInterest.getListing().getTransaction().setTransactionDate(new Date());
								selectedListingInterest.getListing().getTransaction()
										.setListingInterest(selectedListingInterest);
								listListingInterestsService.approveListingInterest(selectedListingInterest);
								Messagebox.show(Labels.getLabel("listingInterest.message.approveConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.LISTING_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
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
	public void onReject() {
		if (selectedListingInterest != null) {
			Messagebox.show(Labels.getLabel("listingInterest.message.rejectQuestion"),
					Labels.getLabel("commmon.messages.reject_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listListingInterestsService.rejectListingInterest(selectedListingInterest);
								Messagebox.show(Labels.getLabel("listingInterest.message.rejectConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.LISTING_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
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
	public void onPrintPDF() {
		SearchResults<ListingInterest> results = null;
		listingInterestSearchCriteria.setPageSize(0);
		listingInterestSearchCriteria.setPageNumber(0);
		try {
			results = listListingInterestsService.searchListingInterestsByCriteria(listingInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<ListingInterest> listingInterests = results.getData();

		Report report = ReportToolkit.requestSellerListingInterestsReport(listingInterests, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		ListListingInterestsVM.log = log;
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

	public ListingInterest getSelectedListingInterest() {
		return selectedListingInterest;
	}

	public void setSelectedListingInterest(ListingInterest selectedListingInterest) {
		this.selectedListingInterest = selectedListingInterest;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}
}
