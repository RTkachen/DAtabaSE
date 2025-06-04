package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import net.ratdik.multikino.domain.RoleName;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.ui.view.*;

public class AdminLayout extends AppLayout {

    public AdminLayout() {
        // 1) HEADER
        H1 logo = new H1("Cinema App");
        logo.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.add(logo);

        // 2) USER INFO + LOGOUT
        User current = VaadinSession.getCurrent().getAttribute(User.class);
        if (current != null) {
            String name = current.getFirstName();
            if (current.hasRole(RoleName.ROLE_MANAGER)) {
                name += " (Kierownik)";
            } else if (current.hasRole(RoleName.ROLE_CASHIER)) {
                name += " (Kasjer)";
            }
            Span userSpan = new Span(name);
            RouterLink logout = new RouterLink("Wyloguj", LoginView.class);
            header.add(userSpan, logout);
        } else {
            RouterLink login = new RouterLink("Zaloguj", LoginView.class);
            header.add(login);
        }

        addToNavbar(header);

        // 3) DRAWER (MENU) – wyświetlamy tylko jeśli to Kierownik
        if (current != null && current.hasRole(RoleName.ROLE_MANAGER)) {
            RouterLink accountsLink     = new RouterLink("Konta", AccountsView.class);
            RouterLink filmListLink     = new RouterLink("Filmy", FilmListView.class);
            RouterLink addFilmLink      = new RouterLink("Dodaj film", AddFilmView.class);
            RouterLink addSessionLink   = new RouterLink("Dodaj seans", AddSessionView.class);

            VerticalLayout menu = new VerticalLayout(
                    accountsLink,
                    filmListLink,
                    addFilmLink,
                    addSessionLink
            );
            addToDrawer(menu);
        }
    }
}
