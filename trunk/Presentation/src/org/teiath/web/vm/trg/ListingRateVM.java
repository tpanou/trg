package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.CreateListingAssessmentService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.validator.ListingAssessmentValidator;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class ListingRateVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingHistoryVM.class.getName());

	@WireVariable
	private CreateListingAssessmentService createListingAssessmentService;

	@Wire
	private Intbox rating;

	private Transaction transaction;
	private ListingAssessment listingAssessment;
	private Validator listingAssessmentValidator;

	@AfterCompose
	@NotifyChange("transaction")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		listingAssessmentValidator = new ListingAssessmentValidator();
		transaction = (Transaction) ZKSession.getAttribute("transaction");
		listingAssessment = new ListingAssessment();

		if (loggedUser.getId() == transaction.getListingInterest().getUser().getId()) {
			try {
				listingAssessment = createListingAssessmentService
						.getListingAssessmentBuyer(transaction.getListingInterest().getListing().getId(),
								loggedUser.getId());

				if (listingAssessment != null) {
					rating.setValue(listingAssessment.getRating());
					Clients.evalJavaScript("doEdit('" + listingAssessment.getRating() + "')");
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
				ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
			}
		} else if ((loggedUser.getId() == transaction.getListingInterest().getListing().getUser().getId())) {
			try {
				listingAssessment = createListingAssessmentService
						.getListingAssessmentSeller(transaction.getListingInterest().getListing().getId(),
								loggedUser.getId());

				if (listingAssessment != null) {
					rating.setValue(listingAssessment.getRating());
					Clients.evalJavaScript("doEdit('" + listingAssessment.getRating() + "')");
				}
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
				ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
			}
		}

		if (listingAssessment == null) {
			listingAssessment = new ListingAssessment();
			rating.setValue(0);
			Clients.evalJavaScript("doLoad()");
		}
	}

	@Command
	public void onSave() {

		listingAssessment.setAssessmentDate(new Date());
		listingAssessment.setRating(rating.getValue());
		listingAssessment.setUser(loggedUser);
		if (loggedUser.getId() == transaction.getListingInterest().getUser().getId()) {
			listingAssessment.setAssessedUser(transaction.getListingInterest().getListing().getUser());
		} else if (loggedUser.getId() == transaction.getListingInterest().getListing().getUser().getId()) {
			listingAssessment.setAssessedUser(transaction.getListingInterest().getUser());
		}

		listingAssessment.setAssessedTransaction(transaction);
		try {
			createListingAssessmentService.saveAssessment(listingAssessment);
			Messagebox.show(Labels.getLabel("route.rating.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.LISTING_HISTORY_LIST);
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public ListingAssessment getListingAssessment() {
		return listingAssessment;
	}

	public void setListingAssessment(ListingAssessment listingAssessment) {
		this.listingAssessment = listingAssessment;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public Validator getListingAssessmentValidator() {
		return listingAssessmentValidator;
	}

	public void setListingAssessmentValidator(Validator listingAssessmentValidator) {
		this.listingAssessmentValidator = listingAssessmentValidator;
	}
}
