package com.example.application.view;

import com.example.application.entity.User;
import com.example.application.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;

@Route(value = "register", layout = MainLayout.class)
@PageTitle("Rejestracja")
public class RegistrationView extends VerticalLayout {

    private final UserService userService;
    private final Binder<User> binder = new Binder<>(User.class);

    public RegistrationView(@Autowired UserService userService) {
        this.userService = userService;
        configureLayout();
        buildForm();
    }

    private void configureLayout() {
        setSizeFull();
        setPadding(false);
        setMargin(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void buildForm() {
        H2 heading = new H2("Rejestracja");

        TextField firstName = new TextField("Imię");
        TextField lastName  = new TextField("Nazwisko");
        DatePicker dob      = new DatePicker("Data urodzenia");
        EmailField email    = new EmailField("Email");
        PasswordField pwd   = new PasswordField("Hasło");
        PasswordField confirm = new PasswordField("Potwierdź hasło");

        // Binder-валидация
        binder.forField(firstName)
                .asRequired("Imię jest wymagane")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lastName)
                .asRequired("Nazwisko jest wymagane")
                .bind(User::getLastName, User::setLastName);

        binder.forField(dob)
                .asRequired("Data urodzenia jest wymagana")
                .withValidator(date -> !date.isAfter(LocalDate.now()), "Data nie może być w przyszłości")
                .bind(User::getDateOfBirth, User::setDateOfBirth);

        binder.forField(email)
                .asRequired("Email jest wymagany")
                .withValidator(new EmailValidator("Nieprawidłowy format email"))
                .bind(User::getEmail, User::setEmail);

        binder.forField(pwd)
                .asRequired("Hasło jest wymagane")
                .withValidator(new StringLengthValidator("Hasło min. 6 znaków", 6, null))
                .bind(User::getPassword, User::setPassword);

        Button registerBtn = new Button("Zarejestruj się", e -> {
            if (!pwd.getValue().equals(confirm.getValue())) {
                Notification.show("Hasła się nie zgadzają", 3000, Position.MIDDLE);
                return;
            }
            User user = new User();
            try {
                binder.writeBean(user);
            } catch (ValidationException ex) {
                // Binder сам покажет ошибки на полях
                return;
            }
            userService.register(user);
            Notification.show("Zarejestrowano pomyślnie!", 2000, Position.BOTTOM_START);
            UI.getCurrent().navigate(LoginView.class);
        });
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.setWidthFull();

        Span haveAccount = new Span("Masz konto?");
        Anchor loginLink = new Anchor("login", "Zaloguj się");
        loginLink.getStyle().set("margin-left", "0.5em");

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                firstName, lastName,
                dob,       email,
                pwd,       confirm
        );
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",    1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setWidth("400px");

        VerticalLayout card = new VerticalLayout(
                heading,
                formLayout,
                registerBtn,
                new HorizontalLayout(haveAccount, loginLink)
        );
        card.setPadding(true);
        card.setSpacing(true);
        card.setWidth("420px");
        card.getStyle()
                .set("background-color", "white")
                .set("padding", "2rem")
                .set("border-radius", "8px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        add(card);
    }
}
