package com.example.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.plugins.backend.Book;
import com.example.plugins.backend.BookRepository;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;
import com.vaadin.spring.annotation.UIScope;

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

    void addBook(Book entity) {
        repository.save(entity);
    }

}
