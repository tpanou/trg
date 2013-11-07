package org.teiath.web.vm.templates;

import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

@SuppressWarnings("UnusedDeclaration")
public class MainTemplateVM {

	@Wire("#routeReportsMenu")
	private Menuitem routeReportsMenu;
	@Wire("#listingReportsMenu")
	private Menuitem listingReportsMenu;
	@Wire("#eventReportsMenu")
	private Menuitem eventReportsMenu;
	@Wire("#accommodationReportsMenu")
	private Menuitem accommodationReportsMenu;
	@Wire("#myActionsMenu")
	private Menuitem myActionsMenu;
	@Wire("#createActionMenu")
	private Menuitem createActionMenu;
	@Wire("#adminMenu")
	private Menu adminMenu;
	@Wire("#newHomeMenu")
	private Menuitem newHomeMenu;
	@Wire("#routesMenu")
	private Menu routesMenu;
	@Wire("#listingsMenu")
	private Menu listingsMenu;
	@Wire("#actionsMenu")
	private Menu actionsMenu;
	@Wire("#roommatesMenu")
	private Menu roommatesMenu;
	@Wire("#myInterestsMenu")
	private Menu myInterestsMenu;
	@Wire("#myHistoryMenu")
	private Menu myHistoryMenu;
	@Wire("#mySettingsMenu")
	private Menu mySettingsMenu;
	@Wire("#myHomeMenu")
	private Menuitem myHomeMenu;
	@Wire("#userLabel")
	private Label userLabel;
	@Wire("#usersMenu")
	private Menuitem usersMenu;
	@Wire("#valuesMenu")
	private Menuitem valuesMenu;
	@Wire("#userActionsMenu")
	private Menuitem userActionsMenu;

	private User user;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		ZKSession.removeAttribute("aImage");
		ZKSession.removeAttribute("image");

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {
			userLabel.setVisible(false);
		}

		if (ZKSession.getAttribute("AUTH_USER") != null) {
			user = (User) ZKSession.getAttribute("AUTH_USER");

			for (UserRole userRole : user.getRoles()) {
				if (userRole.getCode() == User.USER_ROLE_ADMINISTRATION_CLERK) {
					adminMenu.setVisible(true);
					listingReportsMenu.setVisible(true);
				}


				if (userRole.getCode() == User.USER_ROLE_ADMINISTRATOR) {
					adminMenu.setVisible(true);
					valuesMenu.setVisible(true);
					usersMenu.setVisible(true);
					userActionsMenu.setVisible(true);
				}
			}
		} else {
			ZKSession.invalidate();
			ZKSession.sendPureRedirect("/index.zul");
		}
	}

	@Command
	public void onMenuSelect(
			@BindingParam("selectedMenu")
			String selectedMenu) {
		ZKSession.removeAttribute("fromNotification");
		ZKSession.removeAttribute("fromHistory");
		ZKSession.removeAttribute("fromUserAction");
		ZKSession.sendRedirect("/zul" + selectedMenu + ".zul");
	}

	@Command
	public void logout() {
		Messagebox.show(Labels.getLabel("template.logout_message"), Labels.getLabel("template.logout"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.invalidateFull();
						ZKSession.sendPureRedirect("/index.zul");
						break;
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	public void home() {
		ZKSession.sendRedirect("/index.zul");
	}

	@Command
	public void manual() {
		ZKSession.sendRedirect("/manualMain.zul");
	}

	@Command
	public void api() {
		ZKSession.sendRedirect("/apiMainTemplate.zul");
	}

	@Command
	public void terms() {
		Window window = (Window) Executions.createComponents("/zul/trg/terms/termsPopup.zul", null, null);
		window.doModal();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}