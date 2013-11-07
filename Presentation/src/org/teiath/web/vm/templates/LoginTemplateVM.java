package org.teiath.web.vm.templates;

import org.teiath.data.domain.User;
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
public class LoginTemplateVM {

	@Wire("#myRoutesMenu")
	private Menuitem myRoutesMenu;
	@Wire("#newRouteMenu")
	private Menuitem newRouteMenu;
	@Wire("#myListingsMenu")
	private Menuitem myListingsMenu;
	@Wire("#newListingMenu")
	private Menuitem newListingMenu;
	@Wire("#myActionsMenu")
	private Menuitem myActionsMenu;
	@Wire("#newActionMenu")
	private Menuitem newActionMenu;
	@Wire("#newHomeMenu")
	private Menuitem newHomeMenu;
	@Wire("#myHomeMenu")
	private Menuitem myHomeMenu;
	@Wire("#myInboxNotificationsMenu")
	private Menuitem myInboxNotificationsMenu;
	@Wire("#myLogoutMenu")
	private Menuitem myLogoutMenu;
	@Wire("#loginMenu")
	private Menuitem loginMenu;
	@Wire("#myInterestsMenu")
	private Menu myInterestsMenu;
	@Wire("#myHistoryMenu")
	private Menu myHistoryMenu;
	@Wire("#mySettingsMenu")
	private Menu mySettingsMenu;
	@Wire("#loggedUserLabel")
	private Label loggedUserLabel;
	@Wire("#routeReportsMenu")
	private Menuitem routeReportsMenu;
	@Wire("#listingReportsMenu")
	private Menuitem listingReportsMenu;
	@Wire("#eventReportsMenu")
	private Menuitem eventReportsMenu;
	@Wire("#accommodationReportsMenu")
	private Menuitem accommodationReportsMenu;
	@Wire("#adminMenu")
	private Menu adminMenu;
	@Wire("#routesMenu")
	private Menu routesMenu;
	@Wire("#listingsMenu")
	private Menu listingsMenu;
	@Wire("#actionsMenu")
	private Menu actionsMenu;
	@Wire("#roommatesMenu")
	private Menu roommatesMenu;

	private User user;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onMenuSelect(
			@BindingParam("selectedMenu")
			String selectedMenu) {
		ZKSession.sendRedirect("/zul" + selectedMenu + ".zul");
	}

	@Command
	public void loginWindow() {
		Window window = (Window) Executions.createComponents("/login.zul", null, null);
		window.doModal();
	}

	@Command
	public void logout() {
		Messagebox.show(Labels.getLabel("template.logout_message"), Labels.getLabel("template.logout"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.invalidate();
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
	public void api() {
		ZKSession.sendRedirect("/apiLoginTemplate.zul");
	}

	@Command
	public void manual() {
		ZKSession.sendRedirect("/manualLogin.zul");
	}

	@Command
	public void terms() {
		ZKSession.sendRedirect("/zul/trg/terms/trg_terms.zul");
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}