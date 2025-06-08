package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.ratdik.multikino.domain.*;
import net.ratdik.multikino.service.FilmService;
import net.ratdik.multikino.service.JezykNapisowService;
import net.ratdik.multikino.service.SalaService;
import net.ratdik.multikino.service.SeansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Route(value = "admin-panel/seances", layout = AdminLayout.class)
@Secured("ROLE_MANAGER")
public class AdminSeancesView extends VerticalLayout implements BeforeEnterObserver {

    private final SeansService seansService;
    private final FilmService filmService;
    private final SalaService salaService;
    private final JezykNapisowService jezykNapisowService;
    private Grid<Seans> seansGrid;

    @Autowired
    public AdminSeancesView(SeansService seansService, FilmService filmService, SalaService salaService,
                            JezykNapisowService jezykNapisowService) {
        this.seansService = seansService;
        this.filmService = filmService;
        this.salaService = salaService;
        this.jezykNapisowService = jezykNapisowService;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Zarządzanie seansami");
        seansGrid = new Grid<>(Seans.class, false);
        seansGrid.addColumn(Seans::getId).setHeader("ID");
        seansGrid.addColumn(seans -> seans.getFilm().getNazwa()).setHeader("Film");
        seansGrid.addColumn(seans -> seans.getSala().getId()).setHeader("Sala");
        seansGrid.addColumn(seans -> seans.getJezykNapisow() != null ? seans.getJezykNapisow().getNazwaJezyka().name() : "Brak").setHeader("Język napisów");
        seansGrid.addColumn(Seans::getCzasRozpoczecia).setHeader("Czas rozpoczęcia");
        seansGrid.addColumn(Seans::getCzasZakonczenia).setHeader("Czas zakończenia");
        seansGrid.addColumn(Seans::getWolneMiejsca).setHeader("Wolne miejsca");
        seansGrid.addColumn(Seans::isDubbing).setHeader("Dubbing");
        seansGrid.addComponentColumn(seans -> {
            Button delete = new Button("Usuń", e -> {
                seansService.delete(seans.getId());
                seansGrid.setItems(seansService.findAll());
                Notification.show("Seans usunięty!");
            });
            return delete;
        }).setHeader("Akcje");
        seansGrid.setItems(seansService.findAll());
        add(title, seansGrid);

        H2 addTitle = new H2("Dodaj seans");
        FormLayout seansForm = new FormLayout();
        ComboBox<Film> filmSelect = new ComboBox<>("Film");
        filmSelect.setItems(filmService.findAll());
        filmSelect.setItemLabelGenerator(Film::getNazwa);
        ComboBox<Sala> salaSelect = new ComboBox<>("Sala");
        salaSelect.setItems(salaService.findAll());
        salaSelect.setItemLabelGenerator(sala -> "Sala " + sala.getId());
        ComboBox<Jezyk> subtitleLanguageField = new ComboBox<>("Język napisów");
        subtitleLanguageField.setItems(Jezyk.values());
        subtitleLanguageField.setItemLabelGenerator(jezyk -> jezyk.name().charAt(0) + jezyk.name().substring(1).toLowerCase()); // Np. "POLSKI" -> "Polski"
        DateTimePicker startTimeField = new DateTimePicker("Czas rozpoczęcia");
        startTimeField.setStep(Duration.ofMinutes(15));
        startTimeField.setLocale(java.util.Locale.forLanguageTag("pl-PL"));
        Checkbox dubbingField = new Checkbox("Dubbing");
        dubbingField.addValueChangeListener(event -> {
            if (event.getValue()) {
                subtitleLanguageField.setValue(null); // Reset przy dubbingu
                subtitleLanguageField.setEnabled(false); // Wyłącz pole napisów
            } else {
                subtitleLanguageField.setEnabled(true); // Włącz pole napisów
            }
        });

        Button addSeans = new Button("Dodaj seans", e -> {
            Film selectedFilm = filmSelect.getValue();
            Sala selectedSala = salaSelect.getValue();
            if (selectedFilm == null || selectedSala == null || startTimeField.getValue() == null) {
                Notification.show("Wypełnij wszystkie wymagane pola!");
                return;
            }

            Seans seans = new Seans();
            seans.setFilm(selectedFilm);
            seans.setSala(selectedSala);
            if (!dubbingField.getValue()) {
                Jezyk selectedLanguage = subtitleLanguageField.getValue();
                if (selectedLanguage == null) {
                    Notification.show("Wybierz język napisów, jeśli nie ma dubbingu!");
                    return;
                }
                Optional<JezykNapisow> existingJezyk = jezykNapisowService.findAll().stream()
                        .filter(jn -> jn.getNazwaJezyka() == selectedLanguage)
                        .findFirst();
                seans.setJezykNapisow(existingJezyk.orElseGet(() -> {
                    JezykNapisow newJezyk = new JezykNapisow();
                    newJezyk.setNazwaJezyka(selectedLanguage);
                    return jezykNapisowService.save(newJezyk);
                }));
            } else {
                seans.setJezykNapisow(null); // Brak napisów przy dubbingu
            }
            LocalDateTime startTime = startTimeField.getValue();
            seans.setCzasRozpoczecia(startTime);
            Duration filmDuration = Duration.ofMinutes(selectedFilm.getCzasTrwania().toSecondOfDay() / 60);
            LocalDateTime endTime = startTime.plus(filmDuration).plusMinutes(20);
            seans.setCzasZakonczenia(endTime);
            seans.setWolneMiejsca(selectedSala.getPojemnosc());
            seans.setDubbing(dubbingField.getValue());
            seansService.save(seans);
            seansGrid.setItems(seansService.findAll());
            Notification.show("Seans dodany!");
            filmSelect.clear();
            salaSelect.clear();
            subtitleLanguageField.clear();
            startTimeField.clear();
            dubbingField.clear();
        });
        seansForm.add(filmSelect, salaSelect, subtitleLanguageField, startTimeField, dubbingField, addSeans);
        add(addTitle, seansForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null || !user.hasRole(RoleName.ROLE_MANAGER)) {
            event.rerouteTo(LoginView.class);
        }
    }
}