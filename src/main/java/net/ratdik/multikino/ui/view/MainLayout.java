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
        // Logo
        H1 logo = new H1("Cinema App");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Nagłówek jako jeden poziomy layout
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);

        // Spacer wypychający wszystko na prawo
        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1");

        header.add(logo, spacer);

        // Pobranie zalogowanego użytkownika
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            // Gość
            header.add(
                    new RouterLink("Rejestracja", RegistrationView.class),
                    new RouterLink("Logowanie",   LoginView.class)
            );
        } else {
            // Zalogowany — wyświetlamy imię
            String name = user.getFirstName();
            // Sprawdź kasjera/kierownika
            String suffix = user.getRoles().stream()
                    .map(r -> r.getName())
                    .filter(n -> n == RoleName.ROLE_MANAGER || n == RoleName.ROLE_CASHIER)
                    .map(n -> n == RoleName.ROLE_MANAGER ? " (Manager)" : " (Cashier)")
                    .findFirst().orElse("");
            Span userInfo = new Span(name + suffix);
            userInfo.addClassNames(LumoUtility.FontSize.MEDIUM);

            // Przycisk wylogowania
            Button logout = new Button("Wyloguj", e -> {
                VaadinSession.getCurrent().close();
                UI.getCurrent().getPage().reload();
            });
            logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            header.add(userInfo, logout);
        }

        addToNavbar(header);
    }
}
