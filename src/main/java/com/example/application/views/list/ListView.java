package com.example.application.views.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("Home")
@Route("")
public class ListView extends VerticalLayout {

    public ListView() {
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        setMargin(false); // dodaj to

        // GÓRNY NIEBIESKI PASEK
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setSpacing(false); // dodaj to
        topBar.setMargin(false);  // dodaj to
        topBar.setPadding(true);  // zmień to
        topBar.setWidthFull();
        topBar.setHeight("50px"); // dodaj to aby ustalić wysokość paska
        topBar.getStyle()
            .set("background-color", "#ae00ff") // dodaj # przed kolorem
            .set("padding", "10px")
            .set("color", "white")
            .set("position", "fixed") // dodaj to
            .set("top", "0") // dodaj to
            .set("z-index", "1000"); // dodaj to
        topBar.setJustifyContentMode(JustifyContentMode.END);

        Button loginButton = new Button("Zaloguj się");
        Button logoutButton = new Button("Wyloguj się");

        loginButton.getStyle().set("color", "white").set("background", "transparent").set("border", "1px solid white");
        logoutButton.getStyle().set("color", "white").set("background", "transparent").set("border", "1px solid white");

        topBar.add(loginButton, logoutButton);
        add(topBar); // dodanie paska na górę

        // GŁÓWNA TREŚĆ
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("This place intentionally left empty");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}