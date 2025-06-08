package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.ratdik.multikino.domain.User;
import org.springframework.security.access.annotation.Secured;

@Route(value = "admin-panel", layout = MainLayout.class)
@Secured("ROLE_MANAGER")
public class AdminPanelView extends HorizontalLayout implements BeforeEnterObserver {

    public AdminPanelView() {
        setSizeFull();

        // Menu boczne
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Użytkownicy", "admin-panel/users"));
        nav.addItem(new SideNavItem("Filmy", "admin-panel/films"));
        nav.addItem(new SideNavItem("Seanse", "admin-panel/seances"));
        nav.setWidth("200px");

        // Główna treść
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.add(new H2("Panel Administratora"));

        add(nav, content);

        // Domyślne przekierowanie na użytkowników
        getUI().ifPresent(ui -> ui.navigate("admin-panel/users"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getAttribute(User.class) == null) {
            event.rerouteTo(LoginView.class);
        }
    }
}