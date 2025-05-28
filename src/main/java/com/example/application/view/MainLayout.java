package com.example.application.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // Заголовок
        H1 title = new H1("Cinema App");
        title.getStyle()
                .set("margin", "0")
                .set("color", "white")
                .set("font-size", "1.2em");

        // "Rejestracja" — прозрачный фон, белая рамка и текст
        Button registerButton = new Button("Rejestracja", e -> UI.getCurrent().navigate("register"));
        registerButton.getStyle()
                .set("background-color", "transparent")
                .set("color", "white")
                .set("border", "2px solid white")
                .set("border-radius", "4px")
                .set("padding", "0.5em 1em");

        // "Logowanie" — обычная залитая первичная
        Button loginButton = new Button("Logowanie", e -> UI.getCurrent().navigate("login"));
        loginButton.getStyle()
                .set("background-color", "#006cff")  // или оставьте Lumo_PRIMARY
                .set("color", "white");

        HorizontalLayout nav = new HorizontalLayout(registerButton, loginButton);
        nav.getStyle().set("gap", "0.5em");

        // Собираем шапку
        HorizontalLayout header = new HorizontalLayout(title, nav);
        header.expand(title);
        header.setWidthFull();
        header.getStyle()
                .set("background-color", "#1C2B3A")
                .set("padding", "0.5em");
        nav.getStyle().set("margin-left", "auto");

        addToNavbar(header);
    }
}
