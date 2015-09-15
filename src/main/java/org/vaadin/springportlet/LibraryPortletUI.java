package org.vaadin.springportlet;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.VaadinPortletUI;
import org.vaadin.springportlet.backend.Book;
import org.vaadin.springportlet.backend.BookRepository;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.button.MButton;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.example.plugins.AppWidgetSet")
@VaadinPortletUI
public class LibraryPortletUI extends UI {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    LibraryPortletUserService mySpringService;

    private MTable<Book> bookListing;

    @Autowired
    BookForm bookForm;

    @Override
    protected void init(VaadinRequest request) {
        ensureTestData();

        bookListing = new MTable<>(Book.class).withProperties("name",
                "publishDate").withHeight("250px").withFullWidth()
                .withGeneratedColumn("loan", entity -> {
                    User loaner = mySpringService.getLoaner(entity);
                    if (loaner == null) {
                        if (mySpringService.isAllowedToLoan()) {
                            Button btn = new Button("Loan", e -> {
                                mySpringService.loanBook(entity);
                                listBooks();
                            });
                            btn.setStyleName(ValoTheme.BUTTON_PRIMARY);
                            return btn;
                        } else {
                            return "Login to loan";
                        }

                    } else if (mySpringService.isLoanedByMe(entity)) {
                        Button btn = new Button("Mark as returned", e -> {
                            mySpringService.releaseBook(entity);
                            listBooks();
                        });
                        btn.setStyleName(ValoTheme.BUTTON_DANGER);
                        return btn;
                    } else {
                        return "loaned by " + loaner.getFirstName() + " " + loaner.
                                getLastName();
                    }
                });

        listBooks();

        final VerticalLayout layout = new MVerticalLayout(bookListing).withMargin(false);
        setContent(layout);

        
        if (mySpringService.isAllowedToLoan()) {
            layout.addComponent(new MButton("Add new Book", e -> {
                Book book = new Book();
                bookForm.setEntity(book);
                bookForm.setSavedHandler((Book entity) -> {
                    mySpringService.addBook(entity);
                    listBooks();
                    bookForm.getPopup().close();
                });
                bookForm.openInModalPopup();
            }).withIcon(FontAwesome.PLUS));
        }

    }

    private void ensureTestData() {
        if (bookRepository.count() == 0) {
            try {
                Book book = new Book();
                book.setName("Book of Vaadin V1");
                List<User> companyUsers = UserLocalServiceUtil.getCompanyUsers(
                        PortalUtil.getCompany(VaadinLiferayRequest.
                                getCurrentPortletRequest()).getCompanyId(), 1, 5);
                book.setLoanedBy(companyUsers.get(0).getEmailAddress());

                bookRepository.save(book);
                book = new Book();
                book.setName("Book of Vaadin, Vol 1");
                book.setLoanedBy(companyUsers.get(1).getEmailAddress());
                bookRepository.save(book);
                book = new Book();
                book.setName("Book of Vaadin, Vol 2");
                book.setPublishDate(new Date());
                bookRepository.save(book);
            } catch (SystemException | PortalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void listBooks() {
        List<Book> allBooks = bookRepository.findAll();
        bookListing.setBeans(allBooks);
    }

}
