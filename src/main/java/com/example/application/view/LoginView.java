package com.example.application.view;

import com.example.application.entity.User;
import com.example.application.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

@Route(value = "login", layout = MainLayout.class)
@PageTitle("login")
public class LoginView extends VerticalLayout {

    private final UserService userService;
    private final Binder<LoginCredentials> binder = new Binder<>(LoginCredentials.class);

    @Autowired
    public LoginView(UserService userService) {
        this.userService = userService;
        configureLayout();
        buildForm();
    }

    private void configureLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void buildForm() {
        H2 heading = new H2("Logowanie");

        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Hasło");

        // Настройка Binder'а для валидации
        binder.forField(emailField)
                .asRequired("Email jest wymagany")
                .withValidator(new EmailValidator("Nieprawidłowy format email"))
                .bind(LoginCredentials::getEmail, LoginCredentials::setEmail);

        binder.forField(passwordField)
                .asRequired("Hasło jest wymagane")
                .withValidator(new StringLengthValidator("Hasło min. 6 znaków", 6, null))
                .bind(LoginCredentials::getPassword, LoginCredentials::setPassword);

        Button loginBtn = new Button("Zaloguj się", e -> {
            LoginCredentials creds = new LoginCredentials();
            try {
                binder.writeBean(creds);
            } catch (ValidationException ex) {
                return; // ошибки в полях отобразятся автоматически
            }
            userService.authenticate(creds.getEmail(), creds.getPassword())
                    .ifPresentOrElse(user -> {
                        // Сохраняем в сессии и переходим
                        VaadinSession.getCurrent().setAttribute(User.class, user);
                        UI.getCurrent().navigate("list");
                    }, () -> {
                        Notification.show("Nieprawidłowy email lub hasło", 3000, Notification.Position.MIDDLE);
                    });
        });
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginBtn.setWidthFull();

        Span noAccount = new Span("Nie masz konta?");
        Anchor registerLink = new Anchor("register", "Zarejestruj się");
        registerLink.getStyle().set("margin-left", "0.5em");

        FormLayout formLayout = new FormLayout();
        formLayout.add(emailField, passwordField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );
        formLayout.setWidth("400px");

        VerticalLayout card = new VerticalLayout(
                heading,
                formLayout,
                loginBtn,
                new HorizontalLayout(noAccount, registerLink)
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

    /** DTO для биндинга полей логина */
    public static class LoginCredentials {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
