package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.dto.UserRegistrationDto;
import net.ratdik.multikino.service.UserService;

@Route(value = "rejestracja", layout = net.ratdik.multikino.ui.view.MainLayout.class)
@PageTitle("Rejestracja | Cinema App")
public class RegistrationView extends Div {

    public RegistrationView(UserService userService) {
        // pełny ekran + flex środek
        setSizeFull();
        getStyle().set("display", "flex");
        getStyle().set("flexDirection", "column");
        getStyle().set("justifyContent", "center");
        getStyle().set("alignItems", "center");

        // Tytuł
        H2 title = new H2("Rejestracja");
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        // Pola formularza
        TextField firstName = new TextField("Imię");
        firstName.setWidth("300px");
        TextField lastName = new TextField("Nazwisko");
        lastName.setWidth("300px");
        DatePicker birthDate = new DatePicker("Data urodzenia");
        birthDate.setWidth("300px");
        EmailField email = new EmailField("Email");
        email.setWidth("300px");
        PasswordField password = new PasswordField("Hasło");
        password.setWidth("300px");
        PasswordField confirm = new PasswordField("Potwierdź hasło");
        confirm.setWidth("300px");

        // Przycisk rejestracji
        Button register = new Button("Zarejestruj się", e -> {
            // prosty walidator haseł
            if (!password.getValue().equals(confirm.getValue())) {
                Notification.show("Hasła muszą być takie same", 2000, Notification.Position.MIDDLE);
                return;
            }
            // przygotuj DTO
            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setFirstName(firstName.getValue());
            dto.setLastName(lastName.getValue());
            dto.setBirthDate(birthDate.getValue());
            dto.setEmail(email.getValue());
            dto.setPassword(password.getValue());
            try {
                userService.registerNewUser(dto);
                Notification.show("Zarejestrowano pomyślnie", 2000, Notification.Position.TOP_CENTER);
                UI.getCurrent().navigate("login");
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 2000, Notification.Position.MIDDLE);
            }
        });
        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        register.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        // Link do logowania
        Div footer = new Div();
        footer.addClassNames(LumoUtility.Margin.Top.SMALL);
        footer.add("Masz konto? ");
        Anchor toLogin = new Anchor("login", "Zaloguj się");
        footer.add(toLogin);

        // Dodaj wszystko do widoku
        add(title, firstName, lastName, birthDate, email, password, confirm, register, footer);
    }
}
