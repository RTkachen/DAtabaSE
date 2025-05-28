package com.example.application.view;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "list", layout = MainLayout.class)
@PageTitle("List")
public class ListView extends VerticalLayout {

    public ListView() {
        setSpacing(false);

        // Пример: логотип сверху
        Image img = new Image("images/multikino.png", "multikino");
        img.setWidth("200px");
        add(img);

        // Пример: горизонтальный лэйаут с формой логина
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        LoginForm loginForm = new LoginForm();
        horizontalLayout.add(loginForm);

        add(horizontalLayout);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}
