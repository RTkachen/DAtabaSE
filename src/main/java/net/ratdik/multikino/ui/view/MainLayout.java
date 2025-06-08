package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.RoleName;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.ui.view.LoginView;
import net.ratdik.multikino.ui.view.RegistrationView;

public class MainLayout extends AppLayout {

    public MainLayout() {
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
        if (user == null) {
            header.add(
                    new RouterLink("Rejestracja", RegistrationView.class),
                    new RouterLink("Logowanie", LoginView.class)
            );
        } else {
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

            if (user.hasRole(RoleName.ROLE_MANAGER) || user.hasRole(RoleName.ROLE_CASHIER)) {
                header.add(new RouterLink("Panel biletów", AdminTicketsView.class));
            }
            if (user.hasRole(RoleName.ROLE_MANAGER)) {
                header.add(new RouterLink("Panel Admina", AdminUsersView.class));
            }
            header.add(userInfo, logout);
        }

        addToNavbar(header);
    }
}