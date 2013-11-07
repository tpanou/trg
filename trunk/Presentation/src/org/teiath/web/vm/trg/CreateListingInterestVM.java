package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ListingMainImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.CreateListingInterestService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vbox;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class CreateListingInterestVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateListingInterestVM.class.getName());

	@Wire("#photoVBox")
	private Vbox photoVBox;

	@WireVariable
	private CreateListingInterestService createListingInterestService;

	private ListingInterest listingInterest;
	private Listing listing;
	private ListModelList<TradeableGood> tradeableGoods;
	private ListModelList<ProductStatus> productStatuses;
	private TradeableGood tradeableGood;
	private TradeableGood selectedTradeableGood;
	private AImage mainImage;

	@AfterCompose
	@NotifyChange("listingInterest")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);

		try {
			listing = createListingInterestService.getListingById((Integer) ZKSession.getAttribute("listingId"));
			tradeableGoods = new ListModelList<>();
			tradeableGoods.addAll(createListingInterestService.getListingTradeableGoods(listing));
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
		listingInterest = new ListingInterest();
		listingInterest.setUser(loggedUser);
		listingInterest.setListing(listing);
		tradeableGood = new TradeableGood();
	}

	@Command
	public void onSave() {
		if (selectedTradeableGood != null) {
			listingInterest = new ListingInterest();
			listingInterest.setInterestDate(new Date());
			listingInterest.setUser(loggedUser);
			listingInterest.setStatus(ListingInterest.STATUS_PENDING);
			listingInterest.setListing(listing);
			tradeableGood.setName(selectedTradeableGood.getName());
			listingInterest.setTradeableGood(tradeableGood);
			ListingMainImage listingMainImage = new ListingMainImage();
			if (mainImage != null) {
				listingMainImage.setImageBytes(mainImage.getByteData());
				tradeableGood.setListingMainImage(listingMainImage);
			} else {
				tradeableGood.setListingMainImage(null);
			}
			try {
				createListingInterestService.saveListingInterest(listingInterest);
				Messagebox.show(Labels.getLabel("listing.message.inquiryConfirmation"),
						Labels.getLabel("common.messages.confirm"), Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							public void onEvent(Event evt) {
								ZKSession.sendRedirect(PageURL.SEARCH_LISTING);
							}
						});
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listingInterest")),
						Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
				ZKSession.sendRedirect(PageURL.SEARCH_LISTING);
			}
		} else {
			Messagebox.show(MessageBuilder.buildErrorMessage(Labels.getLabel("lisintgInterest.missingTradeableGood"),
					Labels.getLabel("reservation.create")), Labels.getLabel("common.messages.missingElements"),
					Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void uploadMainImage(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		Media media = upEvent.getMedia();
		mainImage = (AImage) media;
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setContent((AImage) media);
		photoVBox.appendChild(imageComponent);
	}

	@Command
	public void deleteMainImage() {
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.SEARCH_LISTING);
	}

	public ListModelList<ProductStatus> getProductStatuses() {
		if (productStatuses == null) {
			productStatuses = new ListModelList<>();
			try {
				productStatuses.addAll(createListingInterestService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productStatuses;
	}

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public ListModelList<TradeableGood> getTradeableGoods() {
		return tradeableGoods;
	}

	public void setTradeableGoods(ListModelList<TradeableGood> tradeableGoods) {
		this.tradeableGoods = tradeableGoods;
	}

	public TradeableGood getSelectedTradeableGood() {
		return selectedTradeableGood;
	}

	public void setSelectedTradeableGood(TradeableGood selectedTradeableGood) {
		this.selectedTradeableGood = selectedTradeableGood;
	}

	public TradeableGood getTradeableGood() {
		return tradeableGood;
	}

	public void setTradeableGood(TradeableGood tradeableGood) {
		this.tradeableGood = tradeableGood;
	}
}
