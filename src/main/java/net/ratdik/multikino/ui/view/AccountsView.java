package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.service.UserService;

@Route(value = "accounts", layout = net.ratdik.multikino.ui.view.AdminLayout.class)
@PageTitle("Konta użytkowników | Cinema App")
public class AccountsView extends VerticalLayout {

    public AccountsView(UserService userService) {
        setSizeFull();

        Grid<User> grid = new Grid<>(User.class, false);
        grid.addColumn(User::getEmail).setHeader("Email");
        grid.addColumn(User::getFirstName).setHeader("Imię");
        grid.addColumn(User::getLastName).setHeader("Nazwisko");
        grid.addColumn(User::getBirthDate).setHeader("Data urodzenia");
        grid.addColumn(u -> u.isBlocked() ? "Tak" : "Nie").setHeader("Zablokowane");
        grid.addColumn(u ->
                u.getRoles().stream()
                        .map(r -> r.getName().name())
                        .reduce((a,b) -> a + ", " + b)
                        .orElse("")
        ).setHeader("Role");

        grid.setItems(userService.findAll());
        add(grid);
    }
}
