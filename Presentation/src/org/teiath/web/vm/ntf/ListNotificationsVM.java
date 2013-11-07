package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.User;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ListNotificationsService;
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
public class ListNotificationsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListNotificationsVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#dateFrom")
	private Datebox dateFrom;
	@Wire("#dateTo")
	private Datebox dateTo;
	@Wire("#typesCombo")
	private Combobox typesCombo;

	@WireVariable
	private ListNotificationsService listNotificationsService;

	private NotificationSearchCriteria notificationSearchCriteria;
	private ListModelList<Notification> notificationsList;
	private Notification selectedNotification;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if (ZKSession.getAttribute("notificationSearchCriteria") != null) {
			notificationSearchCriteria = (NotificationSearchCriteria) ZKSession.getAttribute("notificationSearchCriteria");
			ZKSession.removeAttribute("notificationSearchCriteria");

			selectedNotification= null;
			notificationsList = new ListModelList<>();
			try {
				SearchResults<Notification> results = listNotificationsService
						.searchNotificationsByCriteria(notificationSearchCriteria);
				Collection<Notification> notifications = results.getData();
				notificationsList.addAll(notifications);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(notificationSearchCriteria.getPageNumber());
				if (notifications.isEmpty()) {
					Messagebox.show(MessageBuilder.buildErrorMessage(Labels.getLabel("notifications.InboxNotFound"),
							Labels.getLabel("notifications")), Labels.getLabel("common.messages.search"), Messagebox.OK,
							Messagebox.INFORMATION);
				}
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
		else {
			//Initial search criteria
			notificationSearchCriteria = new NotificationSearchCriteria();
			paging.setPageSize(10);
			notificationSearchCriteria.setPageSize(paging.getPageSize());
			notificationSearchCriteria.setPageNumber(0);
			notificationSearchCriteria.setUser(loggedUser);
			notificationSearchCriteria.setType(Notification.TYPE_GOODS);
			notificationSearchCriteria.setOrderField("sentDate");
			notificationSearchCriteria.setOrderDirection("descending");

			notificationsList = new ListModelList<>();

			try {
				SearchResults<Notification> results = listNotificationsService
						.searchNotificationsByCriteria(notificationSearchCriteria);
				Collection<Notification> notifications = results.getData();
				notificationsList.addAll(notifications);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(notificationSearchCriteria.getPageNumber());
				if (notificationsList.isEmpty()) {
					empty.setVisible(true);
				}
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		notificationSearchCriteria.setOrderField(listheader.getId());
		notificationSearchCriteria.setOrderDirection(listheader.getSortDirection());
		notificationSearchCriteria.setPageNumber(0);
		selectedNotification = null;
		notificationsList.clear();

		try {
			SearchResults<Notification> results = listNotificationsService
					.searchNotificationsByCriteria(notificationSearchCriteria);
			Collection<Notification> notifications = results.getData();
			notificationsList.addAll(notifications);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(notificationSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedNotification")
	public void onPaging() {
		if (notificationsList != null) {
			notificationSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Notification> results = listNotificationsService
						.searchNotificationsByCriteria(notificationSearchCriteria);
				selectedNotification = null;
				notificationsList.clear();
				notificationsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(notificationSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedNotification")
	public void onSearch() {
		selectedNotification = null;
		notificationsList.clear();
		notificationSearchCriteria.setPageNumber(0);
		notificationSearchCriteria.setPageSize(paging.getPageSize());

		try {
			SearchResults<Notification> results = listNotificationsService
					.searchNotificationsByCriteria(notificationSearchCriteria);
			Collection<Notification> notifications = results.getData();
			notificationsList.addAll(notifications);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(notificationSearchCriteria.getPageNumber());
			if (notifications.isEmpty()) {
				Messagebox.show(MessageBuilder.buildErrorMessage(Labels.getLabel("notifications.InboxNotFound"),
						Labels.getLabel("notifications")), Labels.getLabel("common.messages.search"), Messagebox.OK,
						Messagebox.INFORMATION);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedNotification", "notificationSearchCriteria"})
	public void onResetSearch() {
		dateFrom.setRawValue(null);
		dateTo.setRawValue(null);
		//typesCombo.setSelectedItem(null);
		//notificationSearchCriteria.setType(null);
		notificationSearchCriteria.setDateFrom(null);
		notificationSearchCriteria.setDateTo(null);
		notificationsList.clear();
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("notificationId", selectedNotification.getId());
		ZKSession.setAttribute("notificationSearchCriteria", notificationSearchCriteria);
		ZKSession.sendRedirect(PageURL.NOTIFICATION_VIEW);
	}

	@Command
	public void onDelete() {
		if (selectedNotification != null) {
			Messagebox.show(Labels.getLabel("notifications.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listNotificationsService.deleteNotification(selectedNotification);
								Messagebox.show(Labels.getLabel("notifications.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
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
		SearchResults<Notification> results = null;
		notificationSearchCriteria.setPageNumber(0);
		notificationSearchCriteria.setPageSize(0);
		try {
			results = listNotificationsService.searchNotificationsByCriteria(notificationSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Collection<Notification> notifications = results.getData();

		Report report = ReportToolkit.requestNotificationsReport(notifications, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public NotificationSearchCriteria getNotificationSearchCriteria() {
		return notificationSearchCriteria;
	}

	public void setNotificationSearchCriteria(NotificationSearchCriteria notificationSearchCriteria) {
		this.notificationSearchCriteria = notificationSearchCriteria;
	}

	public ListModelList<Notification> getNotificationsList() {
		return notificationsList;
	}

	public void setNotificationsList(ListModelList<Notification> notificationsList) {
		this.notificationsList = notificationsList;
	}

	public Notification getSelectedNotification() {
		return selectedNotification;
	}

	public void setSelectedNotification(Notification selectedNotification) {
		this.selectedNotification = selectedNotification;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}
}
