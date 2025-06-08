package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import net.ratdik.multikino.domain.*;
import net.ratdik.multikino.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

@Route(value = "admin-panel/films", layout = AdminLayout.class)
@Secured("ROLE_MANAGER")
public class AdminFilmsView extends VerticalLayout implements BeforeEnterObserver {

    private final FilmService filmService;
    private Grid<Film> filmsGrid;
    private String uploadedFileName;

    @Autowired
    public AdminFilmsView(FilmService filmService) {
        this.filmService = filmService;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Zarządzanie filmami");
        filmsGrid = new Grid<>(Film.class, false);
        filmsGrid.addColumn(Film::getId).setHeader("ID");
        filmsGrid.addColumn(Film::getNazwa).setHeader("Tytuł");
        filmsGrid.addColumn(Film::getRezyser).setHeader("Reżyser");
        filmsGrid.addColumn(Film::getCzasTrwania).setHeader("Czas trwania");
        filmsGrid.addColumn(Film::getGatunek).setHeader("Gatunek");
        filmsGrid.addColumn(Film::getKrajPochodzenia).setHeader("Kraj");
        filmsGrid.addColumn(Film::getJezykWyswietlania).setHeader("Język");
        filmsGrid.addComponentColumn(film -> {
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
            poster.setWidth("50px");
            poster.setHeight("75px");
            return poster;
        }).setHeader("Plakat");
        filmsGrid.addComponentColumn(film -> {
            Button delete = new Button("Usuń", e -> showDeleteDialog(film));
            return delete;
        }).setHeader("Akcje");
        filmsGrid.setItems(filmService.findAll());
        add(title, filmsGrid);

        H2 addTitle = new H2("Dodaj film");
        FormLayout filmForm = new FormLayout();
        TextField titleField = new TextField("Tytuł");
        TextField directorField = new TextField("Reżyser");
        TextField durationField = new TextField("Czas trwania (min)");
        ComboBox<Gatunek> genreField = new ComboBox<>("Gatunek");
        genreField.setItems(Gatunek.values());
        ComboBox<Kraj> countryField = new ComboBox<>("Kraj pochodzenia");
        countryField.setItems(Kraj.values());
        ComboBox<Jezyk> languageField = new ComboBox<>("Język wyświetlania");
        languageField.setItems(Jezyk.values());
        MemoryBuffer fileBuffer = new MemoryBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes("image/*");
        upload.addSucceededListener(event -> {
            uploadedFileName = event.getFileName();
            Notification.show("Plakat przesłany: " + uploadedFileName);
        });

        Button addFilm = new Button("Dodaj film", e -> {
            Film film = new Film();
            film.setNazwa(titleField.getValue());
            film.setRezyser(directorField.getValue());
            film.setCzasTrwania(LocalTime.ofSecondOfDay(Integer.parseInt(durationField.getValue()) * 60));
            film.setGatunek(genreField.getValue());
            film.setKrajPochodzenia(countryField.getValue());
            film.setJezykWyswietlania(languageField.getValue());

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
                filmsGrid.setItems(filmService.findAll()); // Odśwież Grid
                Notification.show("Film dodany!");
                clearForm(titleField, directorField, durationField, genreField, countryField, languageField);
            } catch (IOException ex) {
                Notification.show("Błąd podczas zapisywania plakatu: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                Notification.show("Nieprawidłowy format czasu trwania!");
            }
        });

        filmForm.add(titleField, directorField, durationField, genreField, countryField, languageField, upload, addFilm);
        add(addTitle, filmForm);
    }

    private void showDeleteDialog(Film film) {
        ConfirmDialog dialog = new ConfirmDialog();
        // Na razie bez logiki seansów, tylko podstawowe usuwanie
        dialog.setHeader("Usuń film");
        dialog.setText("Czy na pewno chcesz usunąć film?");
        dialog.setConfirmText("Tak");
        dialog.setCancelText("Anuluj");
        dialog.setConfirmButtonTheme("error");
        dialog.addConfirmListener(e -> {
            filmService.deleteById(film.getId());
            filmsGrid.setItems(filmService.findAll());
            Notification.show("Film usunięty!");
            dialog.close();
        });
        dialog.open();
    }

    private void clearForm(TextField titleField, TextField directorField, TextField durationField, ComboBox<Gatunek> genreField,
                           ComboBox<Kraj> countryField, ComboBox<Jezyk> languageField) {
        titleField.clear();
        directorField.clear();
        durationField.clear();
        genreField.clear();
        countryField.clear();
        languageField.clear();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null || !user.hasRole(RoleName.ROLE_MANAGER)) {
            event.rerouteTo(LoginView.class);
        }
    }
}