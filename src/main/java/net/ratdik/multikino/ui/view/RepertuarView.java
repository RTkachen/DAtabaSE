package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.Film;
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

@Route(value = "", layout = MainLayout.class)
public class RepertuarView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(RepertuarView.class);

    private final FilmService filmService;
    private final SeansService seansService;

    @Autowired
    public RepertuarView(FilmService filmService, SeansService seansService) {
        this.filmService = filmService;
        this.seansService = seansService;

        setSizeFull();
        setPadding(true);
        addClassNames(LumoUtility.Background.BASE, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);

        H2 title = new H2("Repertuar");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Vertical.MEDIUM);
        add(title);

        // Przycisk "Wszystkie filmy"
        Button allFilmsButton = new Button("Wszystkie filmy", e -> getUI().ifPresent(ui -> ui.navigate("all-films")));
        allFilmsButton.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
        add(allFilmsButton);

        HorizontalLayout filmsLayout = new HorizontalLayout();
        filmsLayout.setWidthFull();
        filmsLayout.setSpacing(true);
        filmsLayout.setPadding(true);
        filmsLayout.getStyle().set("flex-wrap", "wrap");

        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        LocalDateTime endDate = LocalDateTime.now().plusWeeks(1);

        List<Film> films = seansService.findAll().stream()
                .filter(seans -> seans.getCzasRozpoczecia().isAfter(startDate) && seans.getCzasRozpoczecia().isBefore(endDate))
                .map(seans -> seans.getFilm())
                .distinct()
                .collect(Collectors.toList());

        for (Film film : films) {
            VerticalLayout filmCard = new VerticalLayout();
            filmCard.setWidth("200px");
            filmCard.setPadding(true);
            filmCard.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM, LumoUtility.BoxShadow.SMALL);
            filmCard.getStyle().set("cursor", "pointer");

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
            poster.setWidth("100%");
            poster.setHeight("300px");
            poster.addClassName(LumoUtility.BorderRadius.SMALL);
            poster.getElement().addEventListener("error", e -> {
                logger.warn("Błąd ładowania plakatu dla filmu '{}': {}", film.getNazwa(), posterFileName);
                poster.setSrc("/images/default.jpg");
            }).addEventData("event.preventDefault()");

            Span filmTitle = new Span(film.getNazwa());
            filmTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);

            Span filmInfo = new Span(film.getGatunek() + " • " + film.getCzasTrwania().toString());
            filmInfo.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

            filmCard.add(poster, filmTitle, filmInfo);
            filmCard.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("film/" + film.getId())));
            filmsLayout.add(filmCard);
        }

        Div filmsContainer = new Div(filmsLayout);
        filmsContainer.setWidthFull();
        filmsContainer.addClassName(LumoUtility.Overflow.AUTO);
        add(filmsContainer);
    }
}