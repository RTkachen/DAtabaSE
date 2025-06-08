package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.domain.Seans;
import net.ratdik.multikino.service.FilmService;
import net.ratdik.multikino.service.SeansService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "film/:filmId", layout = MainLayout.class)
public class FilmDetailsView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(FilmDetailsView.class);

    private final FilmService filmService;
    private final SeansService seansService;
    private Film film;

    @Autowired
    public FilmDetailsView(FilmService filmService, SeansService seansService) {
        this.filmService = filmService;
        this.seansService = seansService;

        setSizeFull();
        setPadding(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String filmId = event.getRouteParameters().get("filmId").orElse(null);
        if (filmId == null || !filmId.matches("\\d+")) {
            event.rerouteTo("");
            return;
        }

        film = filmService.findById(Integer.parseInt(filmId)).orElse(null);
        if (film == null) {
            event.rerouteTo("");
            return;
        }

        setupFilmDetails();
    }

    private void setupFilmDetails() {
        removeAll();

        HorizontalLayout filmInfoLayout = new HorizontalLayout();
        filmInfoLayout.setWidthFull();
        filmInfoLayout.setSpacing(true);
        filmInfoLayout.setPadding(true);

        String posterFileName = film.getPlakat();
        Image poster = new Image();
        if (posterFileName != null && !posterFileName.isEmpty()) {
            poster.setSrc(new StreamResource(posterFileName, () -> {
                try {
                    return new FileInputStream(Paths.get("src/main/resources/static/uploads/", posterFileName).toFile());
                } catch (IOException e) {
                    logger.warn("Błąd ładowania plakatu dla filmu '{}': {}", film.getNazwa(), e.getMessage());
                    return null;
                }
            }));
        } else {
            poster.setSrc("/images/default.jpg");
        }
        poster.setAlt("Plakat filmu: " + film.getNazwa());
        poster.setWidth("300px");
        poster.setHeight("450px");
        poster.addClassName(LumoUtility.BorderRadius.MEDIUM);
        poster.getElement().addEventListener("error", e -> {
            logger.warn("Błąd ładowania plakatu dla filmu '{}': {}", film.getNazwa(), posterFileName);
            poster.setSrc("/images/default.jpg");
        }).addEventData("event.preventDefault()");

        VerticalLayout details = new VerticalLayout();
        details.setSpacing(true);

        H2 filmTitle = new H2(film.getNazwa());
        filmTitle.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE);

        Span director = new Span("Reżyser: " + film.getRezyser());
        director.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span genre = new Span("Gatunek: " + film.getGatunek());
        genre.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span duration = new Span("Czas trwania: " + film.getCzasTrwania().toString());
        duration.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span country = new Span("Kraj: " + film.getKrajPochodzenia());
        country.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span language = new Span("Język: " + film.getJezykWyswietlania());
        language.addClassNames(LumoUtility.FontSize.MEDIUM);

        details.add(filmTitle, director, genre, duration, country, language);
        filmInfoLayout.add(poster, details);

        H2 seancesTitle = new H2("Seanse");
        seancesTitle.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXLARGE, LumoUtility.Margin.Vertical.MEDIUM);

        HorizontalLayout seancesLayout = new HorizontalLayout();
        seancesLayout.setWidthFull();
        seancesLayout.setSpacing(true);
        seancesLayout.getStyle().set("flex-wrap", "wrap");

        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime endDate = LocalDateTime.now().plusWeeks(1);

        List<Seans> seances = seansService.findAll().stream()
                .filter(seans -> seans.getFilm().getId().equals(film.getId()))
                .filter(seans -> seans.getCzasRozpoczecia().isAfter(startDate) && seans.getCzasRozpoczecia().isBefore(endDate))
                .collect(Collectors.toList());

        for (Seans seans : seances) {
            Button seansButton = new Button(seans.getCzasRozpoczecia().toString() + " (Sala " + seans.getSala().getId() + ")");
            seansButton.addClassNames(LumoUtility.Background.PRIMARY, LumoUtility.TextColor.PRIMARY_CONTRAST);
            seansButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("seans/" + seans.getId())));
            seancesLayout.add(seansButton);
        }

        add(filmInfoLayout, seancesTitle, seancesLayout);
    }
}