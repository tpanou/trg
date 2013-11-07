package org.teiath.web.vm.secure;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import javax.servlet.ServletRequest;

public class IndexVM {

	@WireVariable
	UserLoginService userLoginService;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		System.out.println("**** CN: " + ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("cn"));
		System.out.println("**** SN: " + ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("sn"));

		// Todo get data from request
		String username = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("cn").toString();
		String firstname = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("cn").toString();
		String lastname = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("sn").toString();
		String email = "aaa@fff.gr";
		Integer userType = 1;

		User user = new User();
		user.setUserName(username);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setEmail(email);
		user.setUserType(userType);

		try {
			ZKSession.setAttribute("AUTH_USER", userLoginService.ssoLogin(user));
			ZKSession.sendRedirect("/zul/trg/trg_listings_list.zul");
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}

}
