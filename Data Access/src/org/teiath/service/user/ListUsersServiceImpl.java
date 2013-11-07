package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.search.UserSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import javax.mail.internet.AddressException;

@Service("listUsersService")
@Transactional
public class ListUsersServiceImpl
		implements ListUsersService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;

	@Override
	public SearchResults<User> searchUsersByCriteria(UserSearchCriteria criteria)
			throws ServiceException {
		SearchResults<User> results;

		try {
			results = userDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return results;
	}

	@Override
	public void toggleActivation(User user)
			throws ServiceException {

		try {
			if (user.isApproved()) {
				user.setApproved(false);
				userDAO.save(user);

				//Create and send Email
				String mailSubject = emailProperties.getUserDisApprovalSubject();
				String mailBody = emailProperties.getUserDisApprovalBody();
				mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, mailBody);
			} else {
				user.setApproved(true);
				userDAO.save(user);

				//Create and send Email
				String mailSubject = emailProperties.getUserApprovalSubject();
				String mailBody = emailProperties.getUserApprovalBody();
				mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject, mailBody);
			}
		} catch (TransactionException e) {
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}
}
