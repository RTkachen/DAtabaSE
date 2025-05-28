package com.example.application.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

/**
 * Стартовая страница приложения.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Welcome")
public class WelcomeView extends VerticalLayout {
    public WelcomeView() {
        // здесь содержимое страницы
    }
}
