package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.domain.Gatunek;
import net.ratdik.multikino.domain.Jezyk;
import net.ratdik.multikino.domain.Kraj;
import net.ratdik.multikino.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

@Route("test-films")
public class TestFilmView extends VerticalLayout {

    private final FilmService filmService;
    private String uploadedFileName;

    @Autowired
    public TestFilmView(FilmService filmService) {
        this.filmService = filmService;

        setSizeFull();
        setPadding(true);
        addClassNames(LumoUtility.Background.BASE, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);

        H2 title = new H2("Testowa lista filmów");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Vertical.MEDIUM);

        refreshFilmList();

        H2 addTitle = new H2("Dodaj nowy film");
        TextField nameField = new TextField("Nazwa filmu");
        MemoryBuffer fileBuffer = new MemoryBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes("image/*");
        upload.addSucceededListener(event -> {
            uploadedFileName = event.getFileName();
            add(new H2("Plakat przesłany: " + uploadedFileName));
        });

        Button addFilm = createAddFilmButton(nameField, fileBuffer);
        add(title, addTitle, nameField, upload, addFilm);
    }

    private Button createAddFilmButton(TextField nameField, MemoryBuffer fileBuffer) {
        return new Button("Dodaj film", e -> {
            Film film = new Film();
            film.setNazwa(nameField.getValue());
            film.setRezyser("Domyślny reżyser");
            film.setCzasTrwania(LocalTime.of(1, 30));
            film.setGatunek(Gatunek.DOKUMENT);
            film.setKrajPochodzenia(Kraj.KOREA);
            film.setJezykWyswietlania(Jezyk.POLSKI);

            try {
                if (fileBuffer.getInputStream().available() > 0) {
                    String staticUploadDir = "src/main/resources/static/uploads/";
                    Path uploadPath = Paths.get(staticUploadDir);
                    if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                    String fileName = System.currentTimeMillis() + "_" + uploadedFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(fileBuffer.getInputStream(), filePath);
                    film.setPlakat(fileName);
                }
                filmService.save(film);
                getUI().ifPresent(ui -> ui.access(() -> {
                    removeAll();
                    add(createInitialLayout());
                    refreshFilmList();
                    add(new H2("Film dodany: " + film.getNazwa()));
                }));
            } catch (IOException ex) {
                add(new H2("Błąd podczas zapisywania plakatu: " + ex.getMessage()));
            }
        });
    }

    private VerticalLayout createInitialLayout() {
        H2 title = new H2("Testowa lista filmów");
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Vertical.MEDIUM);
        H2 addTitle = new H2("Dodaj nowy film");
        TextField nameField = new TextField("Nazwa filmu");
        MemoryBuffer fileBuffer = new MemoryBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes("image/*");
        upload.addSucceededListener(event -> {
            uploadedFileName = event.getFileName();
            add(new H2("Plakat przesłany: " + uploadedFileName));
        });
        return new VerticalLayout(title, addTitle, nameField, upload, createAddFilmButton(nameField, fileBuffer));
    }

    private void refreshFilmList() {
        List<Film> films = filmService.findAll();
        for (Film film : films) {
            VerticalLayout filmCard = new VerticalLayout();
            filmCard.setWidth("200px");
            filmCard.setPadding(true);
            filmCard.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM, LumoUtility.BoxShadow.SMALL);

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

            filmCard.add(poster, new Span(film.getNazwa()));
            add(filmCard);
        }
    }
}