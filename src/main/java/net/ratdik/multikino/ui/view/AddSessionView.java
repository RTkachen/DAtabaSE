package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.domain.JezykNapisow;
import net.ratdik.multikino.domain.Sala;
import net.ratdik.multikino.domain.Seans;
import net.ratdik.multikino.service.FilmService;
import net.ratdik.multikino.service.JezykNapisowService;
import net.ratdik.multikino.service.SalaService;
import net.ratdik.multikino.service.SeansService;

@Route(value = "add-session", layout = net.ratdik.multikino.ui.view.AdminLayout.class)
@PageTitle("Dodaj seans | Cinema App")
public class AddSessionView extends VerticalLayout {

    public AddSessionView(SeansService seansService,
                          FilmService filmService,
                          SalaService salaService,
                          JezykNapisowService jezykService) {

        ComboBox<Film> film = new ComboBox<>("Film");
        film.setItems(filmService.findAll());
        film.setItemLabelGenerator(Film::getNazwa);

        ComboBox<Sala> sala = new ComboBox<>("Sala");
        sala.setItems(salaService.findAll());
        sala.setItemLabelGenerator(s -> "Sala " + s.getId());

        ComboBox<JezykNapisow> jezyk = new ComboBox<>("Napisy");
        jezyk.setItems(jezykService.findAll());
        jezyk.setItemLabelGenerator(JezykNapisow::getNazwaJezyka);

        DateTimePicker start = new DateTimePicker("Rozpoczęcie");
        DateTimePicker end = new DateTimePicker("Zakończenie");

        IntegerField wolne = new IntegerField("Wolne miejsca");
        wolne.setMin(0);

        Checkbox dubbing = new Checkbox("Dubbing");

        Button add = new Button("Dodaj", e -> {
            Seans s = Seans.builder()
                    .film(film.getValue())
                    .sala(sala.getValue())
                    .jezykNapsiow(jezyk.getValue())
                    .czasRozpoczecia(start.getValue())
                    .czasZakonczenia(end.getValue())
                    .wolneMiejsca(wolne.getValue())
                    .dubbing(dubbing.getValue())
                    .build();
            seansService.save(s);
            Notification.show("Dodano seans");
            film.clear();
            sala.clear();
            jezyk.clear();
            start.clear();
            end.clear();
            wolne.clear();
            dubbing.clear();
        });

        add(film, sala, jezyk, start, end, wolne, dubbing, add);
    }
}
