package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ViewListingInterestUserService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.Collection;

public class ViewListingInterestUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewListingInterestUserVM.class.getName());

	@Wire("#listingViewSellerWin")
	private Window win;
	@Wire("#panelChildren")
	private Panelchildren panelchildren;
	@Wire("#commentsListBoxRow")
	private Row commentsListBoxRow;
	@Wire("#averageRatingRow")
	private Row averageRatingRow;
	@Wire("#commentsLabelRow")
	private Row commentsLabelRow;
	@Wire("#exchangeRow")
	private Row exchangeRow;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;
	@Wire("#sendHomeLabel")
	private Label sendHomeLabel;

	@WireVariable
	private ViewListingInterestUserService viewListingInterestUserService;

	private Listing listing;
	private ListModelList<ApplicationImage> images;
	private ApplicationImage selectedImage;
	private ListModelList<ListingAssessment> userTransactionsComments;
	private ListModelList<TradeableGood> tradeableGoods;

	@AfterCompose
	@NotifyChange("listing")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		userTransactionsComments = new ListModelList<>();
		try {
			listing = viewListingInterestUserService.getListingById((Integer) ZKSession.getAttribute("listingId"));

			if (listing.getListingMainImage() != null) {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setContent(listing.getListingMainImage().getImage());
				imageComponent.addEventListener(Events.ON_MOUSE_OVER, new EventListener<Event>() {
					@Override
					public void onEvent(Event event)
							throws Exception {
						((Image) event.getTarget()).setStyle("cursor: pointer");
					}
				});
				imageComponent.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					@Override
					public void onEvent(Event event)
							throws Exception {

						ZKSession.setAttribute("image", event.getTarget());
						Window window = (Window) Executions.createComponents("/zul/trg/image_view.zul", null, null);
						window.doModal();
					}
				});
				photoVBox.appendChild(imageComponent);
			} else {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}
			if (listing.isSendHome()) {
				sendHomeLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				sendHomeLabel.setValue(Labels.getLabel("common.no"));
			}

			if (listing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
				exchangeRow.setVisible(true);
				tradeableGoods = new ListModelList<>();
				try {
					tradeableGoods.addAll(viewListingInterestUserService.getListingTradeableGoods(listing));
				} catch (ServiceException e) {
					log.error(e.getMessage());
					Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
					ZKSession.sendRedirect(PageURL.LISTING_LIST);
				}
			}

			if (images == null) {
				images = new ListModelList<>();
				try {
					images.addAll(viewListingInterestUserService.getImages(listing));
					if (! images.isEmpty()) {
						Image image;
						Div div;
						for (ApplicationImage aimage : images) {
							div = new Div();
							div.setStyle("width: 100%; text-align:center;");
							image = new Image();
								image.setWidth("80px");
								image.setHeight("80px");

							image.setContent(aimage.getImage());
							image.addEventListener(Events.ON_MOUSE_OVER, new EventListener<Event>() {
								@Override
								public void onEvent(Event event)
										throws Exception {
									((Image) event.getTarget()).setStyle("cursor: pointer");
								}
							});
							image.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
								@Override
								public void onEvent(Event event)
										throws Exception {

									ZKSession.setAttribute("image", event.getTarget());
									Window window = (Window) Executions
											.createComponents("/zul/trg/image_view.zul", null, null);
									window.doModal();
								}
							});
							div.appendChild(image);
							imageLibrary.appendChild(div);
						}
					}
				} catch (ServiceException e) {
					Messagebox.show(MessageBuilder
							.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
					log.error(e.getMessage());
				} catch (IOException e) {
					Messagebox.show(MessageBuilder
							.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
					log.error(e.getMessage());
				}

				if (listing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
					exchangeRow.setVisible(true);
				}
			}

			Collection<ListingAssessment> comments = viewListingInterestUserService
					.findUserTransactionsComments(listing.getUser());
			userTransactionsComments.addAll(comments);

			if (userTransactionsComments.isEmpty()) {
				commentsListBoxRow.setVisible(false);
				averageRatingRow.setVisible(false);
			}

			Clients.evalJavaScript("doLoad('" + listing.getUser().getAverageTransactionRating() + "')");
		} catch (ServiceException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.LISTING_INTERESTS_USER);
	}

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(viewListingInterestUserService.getImages(listing));
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
		return images;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public ApplicationImage getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(ApplicationImage selectedImage) {
		this.selectedImage = selectedImage;
	}

	public ListModelList<ListingAssessment> getUserTransactionsComments() {
		return userTransactionsComments;
	}

	public void setUserTransactionsComments(ListModelList<ListingAssessment> userTransactionsComments) {
		this.userTransactionsComments = userTransactionsComments;
	}

	public ListModelList<TradeableGood> getTradeableGoods() {
		return tradeableGoods;
	}

	public void setTradeableGoods(ListModelList<TradeableGood> tradeableGoods) {
		this.tradeableGoods = tradeableGoods;
	}
}
