// src/main/java/net/ratdik/multikino/ui/view/AddFilmView.java
package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.service.FilmService;

@Route(value = "add-film", layout = net.ratdik.multikino.ui.view.AdminLayout.class)
@PageTitle("Dodaj film | Cinema App")
public class AddFilmView extends VerticalLayout {
    public AddFilmView(FilmService filmService) {
        TextField nazwa = new TextField("Nazwa");
        TextField rezyser = new TextField("Reżyser");
        TextField gatunek = new TextField("Gatunek");
        TextField kraj = new TextField("Kraj pochodzenia");
        TextField jezyk = new TextField("Język wyświetlania");
        TimePicker czas = new TimePicker("Czas trwania");
        Button add = new Button("Dodaj", e -> {
            Film f = Film.builder()
                    .nazwa(nazwa.getValue())
                    .rezyser(rezyser.getValue())
                    .gatunek(gatunek.getValue())
                    .krajPochodzenia(kraj.getValue())
                    .jezykWyswietlania(jezyk.getValue())
                    .czasTrwania(czas.getValue())
                    .build();
            filmService.save(f);
            Notification.show("Film dodany");
            nazwa.clear();
            rezyser.clear();
            gatunek.clear();
            kraj.clear();
            jezyk.clear();
            czas.clear();
        });
        add(nazwa, rezyser, gatunek, kraj, jezyk, czas, add);
    }
}
