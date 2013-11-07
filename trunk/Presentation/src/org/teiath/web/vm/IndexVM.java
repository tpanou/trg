package org.teiath.web.vm;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

public class IndexVM {

	static Logger log = Logger.getLogger(IndexVM.class.getName());

	@Wire("#east")
	East east;
	@Wire("#west")
	West west;
	@Wire("#iForgotButton")
	Toolbarbutton iForgotButton;
	@Wire("#iForgotLabel")
	Label iForgotLabel;
	@Wire("#ssoLogin")
	Label ssoLogin;

	@WireVariable
	UserLoginService userLoginService;

	private String username;
	private String password;
	private String messageLabel;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		ssoLogin.setSclass("linkTextLabel");

		if (ZKSession.getAttribute("AUTH_USER") != null) {
			east.setSize("0%");
			west.setSize("100%");
		}

		if ((Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {
			iForgotButton.setVisible(true);
			iForgotLabel.setVisible(false);
		}
	}

	@Command
	public void onLogin() {
		User user;

		try {
			user = userLoginService.login(username, password);
			if (user != null) {
				ZKSession.setAttribute("AUTH_USER", user);
				Object adCode = Sessions.getCurrent().getAttribute("AD_CODE");
				Sessions.getCurrent().removeAttribute("AD_CODE");
				if (adCode != null) {
					ZKSession.sendPureRedirect("/zul/trg/trg_listing_view.zul?ukeysession=" + ZKSession
							.getCurrentWinID() + "&code=" + adCode.toString());
				} else {
					ZKSession.sendRedirect("/zul/trg/trg_listings_list.zul");
				}
			} else {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(Labels.getLabel("user.access.fail"), Labels.getLabel("user.access")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		} catch (AuthenticationException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.access")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.access")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	public void onRegister() {
		ZKSession.sendRedirect("/zul/user/user_create.zul");
	}

	@Command
	public void iForgot() {
		ZKSession.sendRedirect("/reset_request.zul");
	}

	@Command
	public void onSSOLogin() {
		ZKSession.sendPureRedirect("https://swaps.ubuntuweb.org/secure");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMessageLabel() {
		return messageLabel;
	}

	public void setMessageLabel(String messageLabel) {
		this.messageLabel = messageLabel;
	}
}
