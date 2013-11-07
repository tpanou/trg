package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ViewListingSellerService;
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
import org.zkoss.zul.*;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class ViewListingSellerVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewListingSellerVM.class.getName());

	@Wire("#listingViewSellerWin")
	private Window win;
	@Wire("#priceRow")
	private Row priceRow;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#exchangeRow")
	private Row exchangeRow;
	@Wire("#sendHomeLabel")
	private Label sendHomeLabel;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;

	@WireVariable
	private ViewListingSellerService viewListingSellerService;

	private Listing listing;
	private ListModelList<ApplicationImage> images;
	private ListModelList<TradeableGood> tradeableGoods;

	@AfterCompose
	@NotifyChange("listing")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			if (Executions.getCurrent().getParameter("code") != null) {
				listing = viewListingSellerService.getListingByCode(Executions.getCurrent().getParameter("code"));
			} else if (ZKSession.getAttribute("code") != null)
				listing = viewListingSellerService.getListingByCode((String) ZKSession.getAttribute("code"));
			else {
				listing = viewListingSellerService.getListingById((Integer) ZKSession.getAttribute("listingId"));
			}

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
					tradeableGoods.addAll(viewListingSellerService.getListingTradeableGoods(listing));
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
					images.addAll(viewListingSellerService.getImages(listing));
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
		} catch (ServiceException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("fromNotification") != null) {
			ZKSession.removeAttribute("fromNotification");
			ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
		} else if (ZKSession.getAttribute("fromHistory") != null) {
			ZKSession.removeAttribute("fromHistory");
			ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
		}   else if (ZKSession.getAttribute("fromUserAction") != null) {
			ZKSession.removeAttribute("fromUserAction");
			ZKSession.sendRedirect(PageURL.USER_ACTION_LIST);
		} else {
			ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
		}
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public ListModelList<ApplicationImage> getImages() {
		return images;
	}

	public void setImages(ListModelList<ApplicationImage> images) {
		this.images = images;
	}

	public ListModelList<TradeableGood> getTradeableGoods() {
		return tradeableGoods;
	}

	public void setTradeableGoods(ListModelList<TradeableGood> tradeableGoods) {
		this.tradeableGoods = tradeableGoods;
	}
}
