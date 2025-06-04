package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.service.UserService;

@Route(value = "login", layout = MainLayout.class)
@PageTitle("Logowanie | Cinema App")
public class LoginView extends Div implements BeforeEnterObserver {

    public LoginView(UserService userService) {
        setSizeFull();
        getStyle().set("display", "flex");
        getStyle().set("flexDirection", "column");
        getStyle().set("justifyContent", "center");
        getStyle().set("alignItems", "center");

        H2 title = new H2("Logowanie");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Bottom.MEDIUM);

        EmailField email = new EmailField("Email");
        email.setWidth("300px");
        PasswordField password = new PasswordField("Hasło");
        password.setWidth("300px");

        Button login = new Button("Zaloguj się", e -> {
            try {
                User user = userService.login(email.getValue(), password.getValue());
                VaadinSession.getCurrent().setAttribute(User.class, user);
                Notification.show("Zalogowano", 2000, Notification.Position.TOP_CENTER);
                UI.getCurrent().getPage().reload(); // wymusza odświeżenie layoutu
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 2000, Notification.Position.MIDDLE);
            }
        });
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        login.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        add(title, email, password, login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            // już zalogowany — nie pokazuj logowania
            event.forwardTo("");
        }
    }
}
