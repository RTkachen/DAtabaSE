package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.RoleName;
import net.ratdik.multikino.domain.User;

public class AdminLayout extends AppLayout implements BeforeEnterObserver {

    private SideNav nav;

    public AdminLayout() {
        H1 logo = new H1("Cinema App");
        logo.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);

        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1");

        header.add(logo, spacer);

        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user != null) {
            String name = user.getFirstName();
            String suffix = user.hasRole(RoleName.ROLE_MANAGER) ? " (Manager)" :
                    user.hasRole(RoleName.ROLE_CASHIER) ? " (Cashier)" : "";
            Span userInfo = new Span(name + suffix);
            userInfo.addClassNames(LumoUtility.FontSize.MEDIUM);

            Button logout = new Button("Wyloguj", e -> {
                VaadinSession.getCurrent().close();
                UI.getCurrent().getPage().reload();
            });
            logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            header.add(userInfo, logout);
        }

        addToNavbar(header);

        // Inicjalizacja SideNav
        nav = new SideNav();
        nav.addItem(new SideNavItem("Użytkownicy", "admin-panel/users"));
        nav.addItem(new SideNavItem("Filmy", "admin-panel/films"));
        nav.addItem(new SideNavItem("Seanse", "admin-panel/seances"));
        nav.addItem(new SideNavItem("Bilety", "/tickets"));
        nav.setWidth("200px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String path = event.getLocation().getPath();
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        // Sprawdzenie uprawnień – brak roli managera lub brak zalogowania
        if (user == null || !user.hasRole(RoleName.ROLE_MANAGER)) {
            event.rerouteTo(LoginView.class);
            return; // Ważne, aby przerwać dalsze przetwarzanie
        }

        // Usuwamy menu boczne przed dodaniem nowego, aby uniknąć duplikatów
        setDrawerOpened(false);
        getElement().executeJs("this.shadowRoot.querySelector('vaadin-app-layout').shadowRoot.querySelector('#drawer').innerHTML = ''");

        // Dodajemy menu boczne tylko na podstronach admina
        if (path.startsWith("admin-panel")) {
            addToDrawer(nav);
            setDrawerOpened(true);
        }

        // Przekierowanie, jeśli użytkownik wejdzie na /admin-panel
        if (path.equals("admin-panel")) {
            event.rerouteTo("admin-panel/users");
        }
    }
}