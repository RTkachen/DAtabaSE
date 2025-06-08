package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.ratdik.multikino.domain.Bilet;
import net.ratdik.multikino.domain.RoleName;
import net.ratdik.multikino.domain.Status;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.service.BiletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "tickets", layout = MainLayout.class) // Zmiana trasy na /tickets i użycie MainLayout
@Secured({"ROLE_MANAGER", "ROLE_CASHIER"}) // Dostęp dla Managerów i Kasjerów
public class AdminTicketsView extends VerticalLayout implements BeforeEnterObserver {

    private final BiletService biletService;
    private Grid<Bilet> biletGrid;

    @Autowired
    public AdminTicketsView(BiletService biletService) {
        this.biletService = biletService;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Zarządzanie biletami");

        // Dodanie filtru (ComboBox)
        ComboBox<String> filterCombo = new ComboBox<>("Wybierz bilet po nazwie filmu");
        List<String> filmNames = biletService.findAll().stream()
                .map(bilet -> bilet.getSeans().getFilm().getNazwa())
                .distinct()
                .collect(Collectors.toList());
        filterCombo.setItems(filmNames);
        filterCombo.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                List<Bilet> filteredBilets = biletService.findAll().stream()
                        .filter(bilet -> bilet.getSeans().getFilm().getNazwa().equals(event.getValue()))
                        .collect(Collectors.toList());
                biletGrid.setItems(filteredBilets);
            } else {
                biletGrid.setItems(biletService.findAll());
            }
        });

        biletGrid = new Grid<>(Bilet.class, false);
        biletGrid.addColumn(Bilet::getId).setHeader("ID");
        biletGrid.addColumn(bilet -> bilet.getSeans().getId()).setHeader("Seans ID");
        biletGrid.addColumn(bilet -> bilet.getSeans().getFilm().getNazwa()).setHeader("Film");
        biletGrid.addColumn(bilet -> bilet.getUser().getFirstName() + " " + bilet.getUser().getLastName()).setHeader("Użytkownik");
        biletGrid.addColumn(Bilet::getDataZakupu).setHeader("Data zakupu");
        biletGrid.addColumn(Bilet::getStatus).setHeader("Status");
        biletGrid.addComponentColumn(bilet -> {
            Button delete = new Button("Usuń", e -> {
                biletService.delete(bilet.getId());
                biletGrid.setItems(biletService.findAll());
                Notification.show("Bilet usunięty!");
            });
            return delete;
        }).setHeader("Usuń");
        biletGrid.addComponentColumn(bilet -> {
            Button changeStatus = new Button("Zmień status", e -> showChangeStatusDialog(bilet));
            return changeStatus;
        }).setHeader("Zmień status");
        biletGrid.setItems(biletService.findAll());

        HorizontalLayout filterLayout = new HorizontalLayout(filterCombo);
        filterLayout.setPadding(true);
        add(title, filterLayout, biletGrid);
    }

    private void showChangeStatusDialog(Bilet bilet) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Zmień status biletu ID: " + bilet.getId());

        ComboBox<Status> statusCombo = new ComboBox<>("Nowy status");
        statusCombo.setItems(Status.values());
        statusCombo.setValue(bilet.getStatus()); // Ustawienie aktualnego statusu jako domyślnego

        Button saveButton = new Button("Zapisz", e -> {
            bilet.setStatus(statusCombo.getValue());
            biletService.save(bilet); // Zakładam, że BiletService ma metodę save
            biletGrid.setItems(biletService.findAll());
            Notification.show("Status zmieniony na: " + statusCombo.getValue());
            dialog.close();
        });

        Button cancelButton = new Button("Anuluj", e -> dialog.close());
        dialog.add(statusCombo, new HorizontalLayout(saveButton, cancelButton));
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null || (!user.hasRole(RoleName.ROLE_MANAGER) && !user.hasRole(RoleName.ROLE_CASHIER))) {
            event.rerouteTo(LoginView.class);
        }
    }
}