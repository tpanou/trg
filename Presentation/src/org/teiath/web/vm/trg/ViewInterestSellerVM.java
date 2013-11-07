package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ViewListingInterestSellerService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ViewInterestSellerVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewInterestSellerVM.class.getName());

	@WireVariable
	private ViewListingInterestSellerService viewListingInterestSellerService;

	@Wire("#labelRow")
	private Row labelRow;
	@Wire("#pagingRow")
	private Row pagingRow;
	@Wire("#paging")
	private Paging paging;
	@Wire("#listBoxRow")
	private Row listBoxRow;
	@Wire("#commentsListBoxRow")
	private Row commentsListBoxRow;
	@Wire("#commentsLabelRow")
	private Row commentsLabelRow;
	@Wire("#userPhoto")
	private Image userPhoto;
	@Wire("#statusLabel")
	private Label statusLabel;
	@Wire("#ameaLabel")
	private Label ameaLabel;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#tradeableGoodTab")
	private Tab tradeableGoodTab;
	@Wire("#tradeableGoodTabPanel")
	private Tabpanel tradeableGoodTabPanel;

	private ListingInterest listingInterest;
	private ListModelList<Listing> commonListings;
	private ListModelList<ListingAssessment> userTransactionsComments;
	private Listing listing;
	private ListingInterestSearchCriteria listingInterestSearchCriteria = new ListingInterestSearchCriteria();

	@AfterCompose
	@NotifyChange("listingInterest")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		commonListings = new ListModelList<>();
		userTransactionsComments = new ListModelList<>();
		listingInterestSearchCriteria.setPageSize(paging.getPageSize());
		listingInterestSearchCriteria.setPageNumber(0);

		try {
			listingInterest = viewListingInterestSellerService
					.getListingInterestById((Integer) ZKSession.getAttribute("listingInterestId"));
			listing = listingInterest.getListing();
			if (listingInterest.getUser().getAverageTransactionRating() == null)
				listingInterest.getUser().setAverageTransactionRating(0.0);
			SearchResults<Listing> results = viewListingInterestSellerService
					.findCommonListings(listing, listingInterest, listingInterestSearchCriteria);
			Collection<Listing> listings = results.getData();
			commonListings.addAll(listings);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingInterestSearchCriteria.getPageNumber());

			if (listingInterest.getTradeableGood() != null) {
				if (listingInterest.getTradeableGood().getListingMainImage() != null) {
					Image imageComponent = new Image();
					imageComponent.setWidth("150px");
					imageComponent.setHeight("150px");
					imageComponent.setContent(listingInterest.getTradeableGood().getListingMainImage().getImage());
					photoVBox.appendChild(imageComponent);
				}
			} else {
				Image imageComponent = new Image();
				imageComponent.setWidth("150px");
				imageComponent.setHeight("150px");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}

			if (listingInterest.getUser().getApplicationImage() == null) {
				userPhoto.setSrc("/img/default-avatar.png");
			} else {
				AImage aImage = new AImage("", listingInterest.getUser().getApplicationImage().getImageBytes());
				userPhoto.setContent(aImage);
			}

			if (commonListings.isEmpty()) {
				pagingRow.setVisible(false);
				listBoxRow.setVisible(false);
				labelRow.setVisible(true);
			}

			Collection<ListingAssessment> comments = viewListingInterestSellerService
					.findUserTransactionsComments(listingInterest.getUser());
			userTransactionsComments.addAll(comments);
			if (userTransactionsComments.isEmpty()) {
				commentsListBoxRow.setVisible(false);
				commentsLabelRow.setVisible(true);
			}

			if (listingInterest.getUser().isUnemployed()) {
				statusLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				statusLabel.setValue(Labels.getLabel("common.no"));
			}

			if (listingInterest.getUser().isAmea()) {
				ameaLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				ameaLabel.setValue(Labels.getLabel("common.no"));
			}

			if (listing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
				tradeableGoodTab.setVisible(true);
				tradeableGoodTabPanel.setVisible(true);
			}

			Clients.evalJavaScript("doLoad('" + listingInterest.getUser().getAverageTransactionRating() + "')");
		} catch (ServiceException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		} catch (IOException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.ROUTE_INTERESTS);
		}
	}

	@Command
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();
		listingInterestSearchCriteria.setOrderField(listheader.getId());
		listingInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		listingInterestSearchCriteria.setPageNumber(0);
		commonListings.clear();

		try {
			SearchResults<Listing> results = viewListingInterestSellerService
					.findCommonListings(listing, listingInterest, listingInterestSearchCriteria);
			Collection<Listing> listings = results.getData();
			commonListings.addAll(listings);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onPaging() {
		if (commonListings != null) {
			listingInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Listing> results = viewListingInterestSellerService
						.findCommonListings(listing, listingInterest, listingInterestSearchCriteria);
				Collection<Listing> listings = results.getData();
				commonListings.addAll(listings);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(listingInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("fromSubmenu") != null) {
			ZKSession.removeAttribute("fromSubmenu");
			ZKSession.sendRedirect(PageURL.INCOMING_LISTING_INTERESTS);
		} else {
			ZKSession.sendRedirect(PageURL.LISTING_INTERESTS);
		}
	}

	public ListModelList<Listing> getCommonListings() {
		return commonListings;
	}

	public void setCommonListings(ListModelList<Listing> commonListings) {
		this.commonListings = commonListings;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public ListingInterestSearchCriteria getListingInterestSearchCriteria() {
		return listingInterestSearchCriteria;
	}

	public void setListingInterestSearchCriteria(ListingInterestSearchCriteria listingInterestSearchCriteria) {
		this.listingInterestSearchCriteria = listingInterestSearchCriteria;
	}

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}

	public ListModelList<ListingAssessment> getUserTransactionsComments() {
		return userTransactionsComments;
	}

	public void setUserTransactionsComments(ListModelList<ListingAssessment> userTransactionsComments) {
		this.userTransactionsComments = userTransactionsComments;
	}
}
