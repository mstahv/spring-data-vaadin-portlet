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
import com.liferay.portal.model.CompanyModel;
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

	public void save(Book entity) {
		repository.save(entity);
	}

	public List<Book> findAll() {
		return repository.findAll();
	}


	private User currentUser;

	private Company company;

	@PostConstruct
	public void init() {
		initUserAndCompany();
		ensureTestData();
	}

	private void initUserAndCompany() {
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

	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Sets the email field of the Book entity to current Liferay users
	 * main email and persists changes to DB.
	 * 
	 * @param b the book to be set loaned by current user
	 */
	public void borrowBook(Book b) {
		b.setBorrowedBy(getCurrentUser().getEmailAddress());
		repository.save(b);
	}

	public void releaseBook(Book b) {
		b.setBorrowedBy(null);
		repository.save(b);
	}

	public User getBorrower(Book b) {
		if (b.getBorrowedBy() == null) {
			return null;
		} else {
			try {
				return UserLocalServiceUtil.getUserByEmailAddress(getCompany().getCompanyId(), b.getBorrowedBy());
			} catch (PortalException e) {
				throw new RuntimeException(e);
			} catch (SystemException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private CompanyModel getCompany() {
		return company;
	}

	public boolean isBorrowedByMe(Book entity) {
		if (getCurrentUser() == null) {
			return false;
		}
		return currentUser.getEmailAddress().equals(entity.getBorrowedBy());
	}

	public boolean isAllowedToBorrow() {
		return getCurrentUser() != null;
	}
	
	public boolean isAdmin() {
		PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
		return permissionChecker.isOmniadmin();
	}

	public List<String> getCompanyUserEmails() {
		try {
			List<User> companyUsers = UserLocalServiceUtil
					.getCompanyUsers(getCompany().getCompanyId(), 0, 1000);
			return companyUsers.stream()
					.map(u -> u.getEmailAddress())
					.collect(Collectors.toList());

		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	private void ensureTestData() {
		if (repository.count() == 0) {
			try {
				Book book = new Book();
				book.setName("Book of Vaadin V1");
				List<User> companyUsers = UserLocalServiceUtil.getCompanyUsers(
						PortalUtil.getCompany(VaadinLiferayRequest.getCurrentPortletRequest()).getCompanyId(), 1, 5);
				book.setBorrowedBy(companyUsers.get(0).getEmailAddress());

				repository.save(book);
				book = new Book();
				book.setName("Book of Vaadin, Vol 1");
				book.setBorrowedBy(companyUsers.get(1).getEmailAddress());
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

}
