package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ViewListingHistoryService;
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

@SuppressWarnings("UnusedDeclaration")
public class ViewListingHistoryVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewListingHistoryVM.class.getName());

	@Wire("#listingViewSellerWin")
	private Window win;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#exchangeRow")
	private Row exchangeRow;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;

	@WireVariable
	private ViewListingHistoryService viewListingHistoryService;

	private Transaction transaction;
	private ListModelList<ApplicationImage> images;

	@AfterCompose
	@NotifyChange("transaction")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			transaction = viewListingHistoryService
					.getTransactionById((Integer) ZKSession.getAttribute("transactionId"));

			if (transaction.getListing().getUser().getAverageTransactionRating() == null)
				transaction.getListing().getUser().setAverageTransactionRating(0.0);

			if (transaction.getListingInterest().getUser().getAverageTransactionRating() == null)
				transaction.getListingInterest().getUser().setAverageTransactionRating(0.0);

			Clients.evalJavaScript("doLoad('" + transaction.getListingInterest().getListing().getUser()
					.getAverageTransactionRating() + "'," +
					" '" + transaction.getListingInterest().getUser().getAverageTransactionRating() + "')");

			if (transaction.getListingInterest().getListing().getListingMainImage() != null) {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent
						.setContent(transaction.getListingInterest().getListing().getListingMainImage().getImage());
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
				imageComponent.setHeight("300x");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}

			if (transaction.getListingInterest().getListing().getTransactionType()
					.getId() == TransactionType.TYPE_EXCHANGE) {
				exchangeRow.setVisible(true);
			}

			if (images == null) {
				images = new ListModelList<>();
				try {
					images.addAll(viewListingHistoryService.getImages(transaction.getListingInterest().getListing()));
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
		ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
}
