package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.*;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ViewListingSearchService;
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
import java.util.Date;

public class ViewListingNotificationSearchVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewListingSellerVM.class.getName());

	@Wire("#listingViewSellerWin")
	private Window win;
	@Wire("#panelChildren")
	private Panelchildren panelchildren;
	@Wire("#commentsListBoxRow")
	private Row commentsListBoxRow;
	@Wire("#commentsLabelRow")
	private Row commentsLabelRow;
	@Wire("#priceRow")
	private Row priceRow;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#inquiryButton")
	private Toolbarbutton inquiryButton;
	@Wire("#exchangeRow")
	private Row exchangeRow;
	@Wire("#sendHomeLabel")
	private Label sendHomeLabel;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;

	@WireVariable
	private ViewListingSearchService viewListingSearchService;

	private Listing listing;
	private ListModelList<ApplicationImage> images;
	private ApplicationImage selectedImage;
	private ListModelList<ListingAssessment> userTransactionsComments;
	private ListingInterest listingInterest;
	private ListModelList<TradeableGood> tradeableGoods;

	@AfterCompose
	@NotifyChange("listing")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		userTransactionsComments = new ListModelList<>();

		try {

			listing = viewListingSearchService.getListingById((Integer) ZKSession.getAttribute("listingId"));
			if (listing.getListingMainImage() != null) {
				Image imageComponent = new Image();
				imageComponent.setWidth("150px");
				imageComponent.setHeight("150px");
				imageComponent.setContent(listing.getListingMainImage().getImage());
				photoVBox.appendChild(imageComponent);
			} else {
				Image imageComponent = new Image();
				imageComponent.setWidth("150px");
				imageComponent.setHeight("150px");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}
			if (listing.isSendHome()) {
				sendHomeLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				sendHomeLabel.setValue(Labels.getLabel("common.no"));
			}

			if (loggedUser == null) {
				inquiryButton.setVisible(false);
			}

			if (loggedUser != null) {
				if (listing.getUser().getId() != loggedUser.getId()) {
					inquiryButton.setVisible(true);
				} else {
					inquiryButton.setVisible(false);
				}
			}

			if (listing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
				exchangeRow.setVisible(true);
				tradeableGoods = new ListModelList<>();
				try {
					tradeableGoods.addAll(viewListingSearchService.getListingTradeableGoods(listing));
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
					images.addAll(viewListingSearchService.getImages(listing));
					if (! images.isEmpty()) {
						Image image;
						Div div;
						for (ApplicationImage aimage : images) {
							div = new Div();
							div.setStyle("width: 100%; text-align:center;");
							image = new Image();
							if (aimage.getImage().getWidth() > 300) {
								image.setWidth("300px");
							}
							if (aimage.getImage().getHeight() > 300) {
								image.setHeight("300px");
							}

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

			Collection<ListingAssessment> comments = viewListingSearchService
					.findUserTransactionsComments(listing.getUser());
			userTransactionsComments.addAll(comments);

			if (userTransactionsComments.isEmpty()) {
				commentsListBoxRow.setVisible(false);
				commentsLabelRow.setVisible(true);
			}
			listingInterest = viewListingSearchService.getListingInterestByUser(listing, loggedUser);

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
		ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
	}

	@Command
	public void onInquiry() {
		if (listing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
			ZKSession.setAttribute("listingId", listing.getId());
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
							listingInterest.setListing(listing);
							try {
								viewListingSearchService.saveListingInterest(listingInterest);
								Messagebox.show(Labels.getLabel("listing.message.inquiryConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.SEARCH_LISTING);
									}
								});
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

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(viewListingSearchService.getImages(listing));
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

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}

	public ListModelList<TradeableGood> getTradeableGoods() {
		return tradeableGoods;
	}

	public void setTradeableGoods(ListModelList<TradeableGood> tradeableGoods) {
		this.tradeableGoods = tradeableGoods;
	}
}
