package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ViewNotificationService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@SuppressWarnings("UnusedDeclaration")
public class ViewNotificationVM
		extends BaseVM {

	@Wire("#notificationCriteriaDetails")
	private Rows notificationCriteriaDetails;
	@Wire("#notificationCriteriaLabel")
	private Label notificationCriteriaLabel;

	static Logger log = Logger.getLogger(ViewNotificationVM.class.getName());

	@WireVariable
	private ViewNotificationService viewNotificationService;

	private Notification notification;

	@AfterCompose
	@NotifyChange("notification")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		try {
			notification = viewNotificationService
					.getNotificationById((Integer) ZKSession.getAttribute("notificationId"));

			if (notification.getNotificationCriteria() != null) {

				notificationCriteriaLabel.setVisible(true);

				if (notification.getNotificationCriteria().getTitle() != null) {

					Label titleLabel = new Label();
					titleLabel.setValue("Κριτήριο ειδοποίησης:");
					Label criterionTitle = new Label();
					criterionTitle.setValue(notification.getNotificationCriteria().getTitle());

					criterionTitle.setWidth("100px");
					titleLabel.setWidth("100px");
					Row titleRow = new Row();
					titleRow.appendChild(titleLabel);
					titleRow.appendChild(criterionTitle);
					notificationCriteriaDetails.appendChild(titleRow);

					Label descriptionLabel = new Label();
					descriptionLabel.setValue("Περιγραφή κριτηρίου:");
					Label criterionDescription = new Label();
					criterionDescription.setValue(notification.getNotificationCriteria().getDescription());
					Row descriptionRow = new Row();
					descriptionRow.appendChild(descriptionLabel);
					descriptionRow.appendChild(criterionDescription);
					notificationCriteriaDetails.appendChild(descriptionRow);

					if (notification.getNotificationCriteria().getTransactionType() != null) {
						Label transactionTypeLabel = new Label();
						transactionTypeLabel.setValue("Τύπος συναλλαγής:");
						Label criterionTransactionType = new Label();
						criterionTransactionType
								.setValue(notification.getNotificationCriteria().getTransactionType().getName());
						Row transactionTypeRow = new Row();
						transactionTypeRow.appendChild(transactionTypeLabel);
						transactionTypeRow.appendChild(criterionTransactionType);
						notificationCriteriaDetails.appendChild(transactionTypeRow);
					}

					if (notification.getNotificationCriteria().getProductCategory() != null) {
						Label productCategoryLabel = new Label();
						productCategoryLabel.setValue("Κατηγορία προϊόντος:");
						Label criterionProductCategory = new Label();
						criterionProductCategory
								.setValue(notification.getNotificationCriteria().getProductCategory().getName());
						Row productCategoryRow = new Row();
						productCategoryRow.appendChild(productCategoryLabel);
						productCategoryRow.appendChild(criterionProductCategory);
						notificationCriteriaDetails.appendChild(productCategoryRow);
					}

					if (notification.getNotificationCriteria().getProductStatus() != null) {
						Label productStatusLabel = new Label();
						productStatusLabel.setValue("Κατάσταση προϊόντος:");
						Label criterionProductStatus = new Label();
						criterionProductStatus
								.setValue(notification.getNotificationCriteria().getProductStatus().getName());
						Row productStatusRow = new Row();
						productStatusRow.appendChild(productStatusLabel);
						productStatusRow.appendChild(criterionProductStatus);
						notificationCriteriaDetails.appendChild(productStatusRow);
					}

					if ((notification.getNotificationCriteria().getPurchaseDateFrom() != null) && (notification
							.getNotificationCriteria().getPurchaseDateTo() != null)) {
						Label purchaseDateLabel = new Label();
						purchaseDateLabel.setValue("Διάστημα αγοράς:");
						Label criterionPurchaseDatePeriod = new Label();
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						String dateFrom = df.format(notification.getNotificationCriteria().getPurchaseDateFrom());
						String dateTo = df.format(notification.getNotificationCriteria().getPurchaseDateTo());
						criterionPurchaseDatePeriod.setValue(dateFrom + " έως " + dateTo);
						Row purchasePeriodRow = new Row();
						purchasePeriodRow.appendChild(purchaseDateLabel);
						purchasePeriodRow.appendChild(criterionPurchaseDatePeriod);
						notificationCriteriaDetails.appendChild(purchasePeriodRow);
					}

					if (notification.getNotificationCriteria().getMaxPrice() != null) {
						Label maxPriceLabel = new Label();
						maxPriceLabel.setValue("Μέγιστη τιμή προϊόντος:");
						Label criterionMaxprice = new Label();
						DecimalFormat df = new DecimalFormat("###,##0.00");
						String greekFormattedDecimal = df.format(notification.getNotificationCriteria().getMaxPrice());
						criterionMaxprice.setValue(greekFormattedDecimal);
						Row maxPriceRow = new Row();
						maxPriceRow.appendChild(maxPriceLabel);
						maxPriceRow.appendChild(criterionMaxprice);
						notificationCriteriaDetails.appendChild(maxPriceRow);
					}

					if (notification.getNotificationCriteria().getKeywords() != null) {
						Label keywordsLabel = new Label();
						keywordsLabel.setValue("Λέξεις κλειδιά:");
						Label criterionKeywords = new Label();
						criterionKeywords.setValue(notification.getNotificationCriteria().getKeywords());
						Row keywordsRow = new Row();
						keywordsRow.appendChild(keywordsLabel);
						keywordsRow.appendChild(criterionKeywords);
						notificationCriteriaDetails.appendChild(keywordsRow);
					}
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
	}

	@Command
	public void onTransition() {

		if ((notification.getType() == Notification.TYPE_GOODS) && (loggedUser.getId() == notification.getListing()
				.getUser().getId())) {
			ZKSession.setAttribute("listingId", notification.getListing().getId());
			ZKSession.setAttribute("fromNotification", true);
			ZKSession.sendRedirect(PageURL.LISTING_VIEW);
		} else if (notification.getType() == Notification.TYPE_GOODS) {
			ZKSession.setAttribute("listingId", notification.getListing().getId());
			ZKSession.setAttribute("fromNotification", true);
			ZKSession.sendRedirect(PageURL.LISTING_VIEW_BUYER);
		}
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}
