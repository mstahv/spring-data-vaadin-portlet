package com.example.plugins;

import com.example.plugins.backend.Book;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

@org.springframework.stereotype.Component
@Scope(proxyMode = ScopedProxyMode.NO,value = "prototype")
public class BookForm extends AbstractForm<Book> {

    DateField publishDate = new DateField("publishDate");

    TextField name = new MTextField("name").withFullWidth();

    MTextArea description = new MTextArea("description").withFullWidth();

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new FormLayout(
                        name,
                        description,
                        publishDate
                ),
                getToolbar()
        );
    }

}
