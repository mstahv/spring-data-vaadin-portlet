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
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.example.plugins.AppWidgetSet")
@VaadinPortletUI
public class LibraryPortletUI extends UI {

    @Autowired
    LibraryPortletUserService service;

    private MTable<Book> bookListing;

    @Autowired
    BookForm bookForm;

    @Override
    protected void init(VaadinRequest request) {

        bookListing = new MTable<>(Book.class).withProperties("name",
                "publishDate").withHeight("250px").withFullWidth()
                .withGeneratedColumn("loan", entity -> {
                    User loaner = service.getLoaner(entity);
                    if (loaner == null) {
                        if (service.isAllowedToLoan()) {
                            Button btn = new Button("Loan", e -> {
                                service.borrowBook(entity);
                                listBooks();
                            });
                            btn.setStyleName(ValoTheme.BUTTON_PRIMARY);
                            return btn;
                        } else {
                            return "Login to loan";
                        }

                    } else if (service.isLoanedByMe(entity)) {
                        Button btn = new Button("Mark as returned", e -> {
                            service.releaseBook(entity);
                            listBooks();
                        });
                        btn.setStyleName(ValoTheme.BUTTON_DANGER);
                        return btn;
                    } else {
                        return "loaned by " + loaner.getFirstName() + " " + loaner.
                                getLastName();
                    }
                });

        if (service.isAdmin()) {
            bookListing.withGeneratedColumn("edit", (Book entity) -> {
                Button button = new Button(FontAwesome.PENCIL);
                button.addClickListener((Button.ClickEvent event) -> {
                    bookForm.setEntity(entity);
                    bookForm.setSavedHandler((Book entity1) -> {
                        service.save(entity1);
                        listBooks();
                        bookForm.getPopup().close();
                    });
                    bookForm.openInModalPopup();
                });
                return button;
            });
        }

        listBooks();

        final VerticalLayout layout = new MVerticalLayout(bookListing).
                withMargin(false);
        setContent(layout);

        if (service.isAdmin()) {
            layout.addComponent(new MButton("Add new Book", e -> {
                Book book = new Book();
                bookForm.setEntity(book);
                bookForm.setSavedHandler((Book entity) -> {
                    service.save(entity);
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
