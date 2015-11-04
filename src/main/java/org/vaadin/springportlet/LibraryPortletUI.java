package org.vaadin.springportlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.VaadinPortletUI;
import org.vaadin.springportlet.backend.Book;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.liferay.portal.model.User;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.example.plugins.AppWidgetSet")
@VaadinPortletUI
public class LibraryPortletUI extends UI {

    @Autowired
    private LibraryPortletUserService service;

    private MTable<Book> bookListing;

    @Autowired
    private BookForm bookForm;

    @Override
    protected void init(VaadinRequest request) {

        bookListing = new MTable<>(Book.class).withProperties("name",
                "publishDate").withHeight("250px").withFullWidth()
                .withGeneratedColumn("loan", entity -> {
                    User borrower = service.getBorrower(entity);
                    if (borrower == null) {
                        if (service.isAllowedToBorrow()) {
                            return new MButton("Borrow", e -> {
                                service.borrowBook(entity);
                                listBooks();
                            }).withStyleName(ValoTheme.BUTTON_PRIMARY);
                        } else {
                            return "Login to loan";
                        }
                    } else if (service.isBorrowedByMe(entity)) {
                        return new MButton("Mark as returned", e -> {
                            service.releaseBook(entity);
                            listBooks();
                        }).withStyleName(ValoTheme.BUTTON_DANGER);
                    } else {
                        return "borrowed by " + borrower.getFirstName() + " " + borrower.
                                getLastName();
                    }
                });

        if (service.isAdmin()) {
            bookListing.withGeneratedColumn("edit", book -> {
                return new MButton(FontAwesome.PENCIL, e -> {
                    bookForm.setEntity(book);
                    bookForm.setSavedHandler(b -> {
                        service.save(b);
                        listBooks();
                        bookForm.getPopup().close();
                    });
                    bookForm.openInModalPopup();
                });
            });
        }

        listBooks();

        final VerticalLayout layout = new MVerticalLayout(bookListing).
                withMargin(false);
        setContent(layout);

        if (service.isAdmin()) {
            layout.addComponent(new MButton("Add new Book", e -> {
                bookForm.setEntity(new Book());
                bookForm.setSavedHandler(book -> {
                    service.save(book);
                    listBooks();
                    bookForm.getPopup().close();
                });
                bookForm.openInModalPopup();
            }).withIcon(FontAwesome.PLUS));
        }

    }

    private void listBooks() {
        bookListing.setBeans(service.findAll());
    }

}
