package org.vaadin.springportlet;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.springportlet.backend.Book;
import org.vaadin.springportlet.backend.BookRepository;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;
import com.vaadin.spring.annotation.UIScope;

/**
 * A service class that accesses JPA entities via Sprin Data Repository and
 * combines that data with the users specific details provided by Liferay
 * services. Also used to hide some Liferay specific code from the UI layer.
 */
@Component
@UIScope
public class LibraryPortletUserService {

	@Autowired
	private BookRepository repository;

	private User currentUser;

	private Company company;

	@PostConstruct
	public void init() {
		try {
			currentUser = PortalUtil.getUser(VaadinLiferayRequest.getCurrentPortletRequest());
			company = PortalUtil.getCompany(VaadinLiferayRequest.getCurrentPortletRequest());
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ensureTestData();
	}

	public User getCurrentUser() {
		if (currentUser == null) {
			try {
				currentUser = PortalUtil.getUser(VaadinLiferayRequest.getCurrentPortletRequest());
				company = PortalUtil.getCompany(VaadinLiferayRequest.getCurrentPortletRequest());
			} catch (PortalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return currentUser;
	}

	public void loanBook(Book b) {
		b.setLoanedBy(currentUser.getEmailAddress());
		repository.save(b);
	}

	public void releaseBook(Book b) {
		b.setLoanedBy(null);
		repository.save(b);
	}

	public User getLoaner(Book b) {
		if (b.getLoanedBy() == null) {
			return null;
		} else {
			try {
				return UserLocalServiceUtil.getUserByEmailAddress(company.getCompanyId(), b.getLoanedBy());
			} catch (PortalException e) {
				throw new RuntimeException(e);
			} catch (SystemException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isLoanedByMe(Book entity) {
		if (getCurrentUser() == null) {
			return false;
		}
		return currentUser.getEmailAddress().equals(entity.getLoanedBy());
	}

	public boolean isAllowedToLoan() {
		return getCurrentUser() != null;
	}
	
	public boolean isAdmin() {
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
		return permissionChecker.isOmniadmin();
	}

	void addBook(Book entity) {
		repository.save(entity);
	}

	private void ensureTestData() {
		if (repository.count() == 0) {
			try {
				Book book = new Book();
				book.setName("Book of Vaadin V1");
				List<User> companyUsers = UserLocalServiceUtil.getCompanyUsers(
						PortalUtil.getCompany(VaadinLiferayRequest.getCurrentPortletRequest()).getCompanyId(), 1, 5);
				book.setLoanedBy(companyUsers.get(0).getEmailAddress());

				repository.save(book);
				book = new Book();
				book.setName("Book of Vaadin, Vol 1");
				book.setLoanedBy(companyUsers.get(1).getEmailAddress());
				repository.save(book);
				book = new Book();
				book.setName("Book of Vaadin, Vol 2");
				book.setPublishDate(new Date());
				repository.save(book);
			} catch (SystemException | PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<Book> findAll() {
		return repository.findAll();
	}

	public List<String> getCompanyUserEmails() {
		try {
			List<User> companyUsers = UserLocalServiceUtil
					.getCompanyUsers(company.getCompanyId(), 0, 1000);
			return companyUsers.stream()
					.map(u -> u.getEmailAddress())
					.collect(Collectors.toList());

		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

}
