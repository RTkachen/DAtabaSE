package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Route("image-gallery")
public class ImageGalleryView extends VerticalLayout {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ImageGalleryView() {
        // Utwórz folder, jeśli nie istnieje
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Wyświetl istniejące obrazy
        refreshGallery();

        // Konfiguracja uploadu
        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            File file = new File(UPLOAD_DIR + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(buffer.getInputStream().readAllBytes());
                refreshGallery(); // Odśwież galerię po udanym uploadzie
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        add(upload);
    }

    private void refreshGallery() {
        removeAll(); // Wyczyść obecne obrazy
        try (Stream<Path> paths = Files.walk(Paths.get(UPLOAD_DIR))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".jpg") || path.toString().endsWith(".png"))
                    .forEach(path -> {
                        Image image = new Image();
                        image.setSrc(new StreamResource(path.getFileName().toString(), () -> {
                            try {
                                return new FileInputStream(path.toFile());
                            } catch (Exception e) {
                                return null;
                            }
                        }));
                        image.setWidth("300px");
                        add(image);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Dodaj upload ponownie po odświeżeniu
        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            File file = new File(UPLOAD_DIR + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(buffer.getInputStream().readAllBytes());
                refreshGallery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        add(upload);
    }
}