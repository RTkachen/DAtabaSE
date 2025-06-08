package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.ratdik.multikino.domain.RoleName;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "admin-panel/users", layout = AdminLayout.class)
@Secured("ROLE_MANAGER")
public class AdminUsersView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private Grid<User> usersGrid;
    private User currentUser; // Aktualnie zalogowany użytkownik
    private TextField searchField;

    @Autowired
    public AdminUsersView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);

        // Pobranie aktualnie zalogowanego użytkownika
        currentUser = VaadinSession.getCurrent().getAttribute(User.class);

        H2 title = new H2("Zarządzanie użytkownikami");

        // Dodanie pola wyszukiwania
        searchField = new TextField("Wyszukaj (Imię, Nazwisko, Email)");
        searchField.setValueChangeMode(ValueChangeMode.EAGER); // Aktualizacja przy każdej zmianie
        searchField.addValueChangeListener(event -> updateGridItems(event.getValue()));

        usersGrid = new Grid<>(User.class, false);
        usersGrid.addColumn(User::getId).setHeader("N").setSortable(true);
        usersGrid.addColumn(User::getFirstName).setHeader("Imię").setSortable(true);
        usersGrid.addColumn(User::getLastName).setHeader("Nazwisko").setSortable(true);
        usersGrid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        usersGrid.addColumn(User::getBirthDate).setHeader("Data urodzenia").setSortable(true);

        usersGrid.addComponentColumn(user -> {
            Checkbox cashier = new Checkbox("Kasjer", user.hasRole(RoleName.ROLE_CASHIER));
            cashier.addValueChangeListener(event -> {
                if (event.isFromClient()) { // Sprawdzamy, czy zmiana pochodzi od użytkownika
                    if (event.getValue()) userService.assignRole(user.getId(), RoleName.ROLE_CASHIER);
                    else userService.removeRole(user.getId(), RoleName.ROLE_CASHIER);
                    Notification.show("Rola Kasjer zaktualizowana!");
                }
            });
            return cashier;
        }).setHeader("Kasjer");

        usersGrid.addComponentColumn(user -> {
            Checkbox manager = new Checkbox("Kierownik", user.hasRole(RoleName.ROLE_MANAGER));
            manager.addValueChangeListener(event -> {
                if (event.isFromClient()) { // Sprawdzamy, czy zmiana pochodzi od użytkownika
                    if (user.getId().equals(currentUser.getId())) {
                        Notification.show("Nie możesz usunąć sobie roli Kierownika!", 2000, Notification.Position.MIDDLE);
                        manager.setValue(true); // Przywracamy poprzednią wartość
                    } else {
                        if (event.getValue()) userService.assignRole(user.getId(), RoleName.ROLE_MANAGER);
                        else userService.removeRole(user.getId(), RoleName.ROLE_MANAGER);
                        Notification.show("Rola Kierownik zaktualizowana!");
                    }
                }
            });
            return manager;
        }).setHeader("Kierownik");

        usersGrid.addComponentColumn(user -> {
            Checkbox blocked = new Checkbox("Zablokowany", user.isBlocked());
            blocked.addValueChangeListener(event -> {
                if (event.isFromClient()) { // Sprawdzamy, czy zmiana pochodzi od użytkownika
                    if (user.getId().equals(currentUser.getId())) {
                        Notification.show("Nie możesz zablokować samego siebie!", 2000, Notification.Position.MIDDLE);
                        blocked.setValue(false); // Przywracamy poprzednią wartość
                    } else {
                        user.setBlocked(event.getValue());
                        userService.save(user);
                        Notification.show("Status użytkownika zaktualizowany!");
                    }
                }
            });
            return blocked;
        }).setHeader("Zablokowany");

        // Ustawienie początkowych danych
        updateGridItems("");

        add(title, searchField, usersGrid);
    }

    private void updateGridItems(String searchTerm) {
        List<User> allUsers = userService.findAll();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchLower = searchTerm.toLowerCase(); // Локальная копия для модификации
            allUsers = allUsers.stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(searchLower)
                            || user.getLastName().toLowerCase().contains(searchLower)
                            || user.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        usersGrid.setItems(allUsers);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null || !user.hasRole(RoleName.ROLE_MANAGER)) {
            event.rerouteTo(LoginView.class);
        }
    }
}