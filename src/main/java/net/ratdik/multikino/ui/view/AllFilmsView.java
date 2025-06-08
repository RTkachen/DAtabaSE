package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.domain.Gatunek;
import net.ratdik.multikino.domain.Kraj;
import net.ratdik.multikino.domain.Jezyk;
import net.ratdik.multikino.service.FilmService;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "all-films", layout = MainLayout.class)
@PageTitle("Repertuar | Cinema App")
public class AllFilmsView extends VerticalLayout {

    private final FilmService filmService;
    private final HorizontalLayout filmsLayout = new HorizontalLayout();
    private TextField searchField;
    private com.vaadin.flow.component.combobox.ComboBox<Gatunek> genreFilter;
    private com.vaadin.flow.component.combobox.ComboBox<Kraj> countryFilter;
    private Button resetFilterButton;

    public AllFilmsView(FilmService filmService) {
        this.filmService = filmService;

        setSizeFull();
        setPadding(true);
        addClassNames(LumoUtility.Background.BASE, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);

        H2 title = new H2("Wszystkie filmy");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Vertical.MEDIUM);
        add(title);

        // Dodanie pól filtrowania i wyszukiwania
        searchField = new TextField("Wyszukaj po tytule");
        searchField.addValueChangeListener(event -> updateFilms());
        searchField.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);

        genreFilter = new com.vaadin.flow.component.combobox.ComboBox<>("Filtruj po gatunku");
        genreFilter.setItems(Gatunek.values());
        genreFilter.addValueChangeListener(event -> updateFilms());

        countryFilter = new com.vaadin.flow.component.combobox.ComboBox<>("Filtruj po kraju");
        countryFilter.setItems(Kraj.values());
        countryFilter.addValueChangeListener(event -> updateFilms());

        resetFilterButton = new Button("Reset filtra", e -> {
            searchField.clear();
            genreFilter.clear();
            countryFilter.clear();
            updateFilms();
        });
        resetFilterButton.addClassNames(LumoUtility.Background.CONTRAST_5);

        HorizontalLayout filterLayout = new HorizontalLayout(searchField, genreFilter, countryFilter, resetFilterButton);
        filterLayout.setSpacing(true);
        filterLayout.setPadding(true);
        add(filterLayout);

        filmsLayout.setWidthFull();
        filmsLayout.setSpacing(true);
        filmsLayout.setPadding(true);
        filmsLayout.getStyle().set("flex-wrap", "wrap");
        updateFilms();
        add(filmsLayout);
    }

    private void updateFilms() {
        filmsLayout.removeAll();
        String searchTerm = searchField.getValue() != null ? searchField.getValue().toLowerCase() : "";
        Gatunek selectedGenre = genreFilter.getValue();
        Kraj selectedCountry = countryFilter.getValue();

        List<Film> films = filmService.findAll().stream()
                .filter(film -> {
                    boolean matchesSearch = searchTerm.isEmpty() || film.getNazwa().toLowerCase().contains(searchTerm);
                    boolean matchesGenre = selectedGenre == null || film.getGatunek() == selectedGenre;
                    boolean matchesCountry = selectedCountry == null || film.getKrajPochodzenia() == selectedCountry;
                    return matchesSearch && matchesGenre && matchesCountry;
                })
                .sorted((f1, f2) -> f1.getNazwa().compareToIgnoreCase(f2.getNazwa())) // Sortowanie alfabetyczne
                .collect(Collectors.toList());

        for (Film film : films) {
            VerticalLayout filmCard = new VerticalLayout();
            filmCard.setWidth("200px");
            filmCard.setPadding(true);
            filmCard.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM, LumoUtility.BoxShadow.SMALL);
            filmCard.getElement().getStyle().set("cursor", "pointer");
            filmCard.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("film/" + film.getId()))); // Przejście do szczegółów

            String posterFileName = film.getPlakat();
            Image poster = new Image();
            if (posterFileName != null && !posterFileName.isEmpty()) {
                poster.setSrc(new StreamResource(posterFileName, () -> {
                    try {
                        return new FileInputStream(Paths.get("src/main/resources/static/uploads/", posterFileName).toFile());
                    } catch (IOException e) {
                        System.err.println("Błąd ładowania plakatu: " + e.getMessage());
                        return null;
                    }
                }));
            } else {
                poster.setSrc("/images/default.jpg");
            }
            poster.setWidth("100%");
            poster.setHeight("300px");
            poster.addClassName(LumoUtility.BorderRadius.SMALL);

            Span filmTitle = new Span(film.getNazwa());
            filmTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);

            Span filmInfo = new Span(film.getGatunek() + " • " + film.getCzasTrwania());
            filmInfo.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

            filmCard.add(poster, filmTitle, filmInfo);
            filmsLayout.add(filmCard);
        }
    }
}