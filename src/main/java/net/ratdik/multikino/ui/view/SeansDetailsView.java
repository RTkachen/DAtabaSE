package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.Bilet;
import net.ratdik.multikino.domain.Seans;
import net.ratdik.multikino.domain.User;
import net.ratdik.multikino.service.BiletService;
import net.ratdik.multikino.service.SeansService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Route(value = "seans/:seansId", layout = MainLayout.class)
public class SeansDetailsView extends VerticalLayout implements BeforeEnterObserver {

    private final SeansService seansService;
    private final BiletService biletService;
    private Seans seans;

    @Autowired
    public SeansDetailsView(SeansService seansService, BiletService biletService) {
        this.seansService = seansService;
        this.biletService = biletService;

        setSizeFull();
        setPadding(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String seansId = event.getRouteParameters().get("seansId").orElse(null);
        if (seansId == null || !seansId.matches("\\d+")) {
            event.rerouteTo("");
            return;
        }

        seans = seansService.findById(Integer.parseInt(seansId)).orElse(null);
        if (seans == null) {
            event.rerouteTo("");
            return;
        }

        setupSeansDetails();
    }

    private void setupSeansDetails() {
        removeAll();

        H2 title = new H2("Szczegóły seansu: " + seans.getFilm().getNazwa());
        title.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XXXLARGE);

        Span filmInfo = new Span("Film: " + seans.getFilm().getNazwa());
        filmInfo.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span salaInfo = new Span("Sala: " + seans.getSala().getId());
        salaInfo.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span startTime = new Span("Czas rozpoczęcia: " + seans.getCzasRozpoczecia());
        startTime.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span endTime = new Span("Czas zakończenia: " + seans.getCzasZakonczenia());
        endTime.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span wolneMiejsca = new Span("Wolne miejsca: " + seans.getWolneMiejsca());
        wolneMiejsca.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span dubbing = new Span("Dubbing: " + (seans.isDubbing() ? "Tak" : "Nie"));
        dubbing.addClassNames(LumoUtility.FontSize.MEDIUM);

        Span napisy = new Span("Napisy: " + (seans.getJezykNapisow() != null ? seans.getJezykNapisow().getNazwaJezyka() : "Brak"));
        napisy.addClassNames(LumoUtility.FontSize.MEDIUM);

        Button buyTicketButton = new Button("Kup bilet");
        buyTicketButton.addClassNames(LumoUtility.Background.SUCCESS, LumoUtility.TextColor.SUCCESS_CONTRAST);
        buyTicketButton.addClickListener(e -> confirmTicketPurchase());

        add(title, filmInfo, salaInfo, startTime, endTime, wolneMiejsca, dubbing, napisy, buyTicketButton);
    }

    private void confirmTicketPurchase() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            Notification.show("Proszę się zalogować, aby kupić bilet!");
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        // Sprawdzenie daty seansu
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime seansDate = seans.getCzasRozpoczecia();
        if (seansDate.toLocalDate().isBefore(now.toLocalDate())) {
            Notification.show("Nie można kupić biletu na seans z poprzednich dni!");
            return;
        }

        // Sprawdzenie wolnych miejsc
        if (seans.getWolneMiejsca() <= 0) {
            Notification.show("Brak wolnych miejsc na ten seans!");
            return;
        }

        Dialog confirmDialog = new Dialog();
        confirmDialog.add(new Span("Czy na pewno chcesz kupić bilet na seans " + seans.getFilm().getNazwa() + " o " + seans.getCzasRozpoczecia() + "?"));

        Button confirmButton = new Button("Potwierdź", e -> {
            try {
                Bilet bilet = biletService.zakupBiletu(seans, user);
                Notification.show("Bilet zakupiony! ID biletu: " + bilet.getId());
                setupSeansDetails(); // Odśwież widok, aby zaktualizować wolne miejsca
            } catch (IllegalStateException ex) {
                Notification.show(ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Wystąpił błąd: " + ex.getMessage());
            }
            confirmDialog.close();
        });

        Button cancelButton = new Button("Anuluj", e -> confirmDialog.close());
        confirmDialog.add(confirmButton, cancelButton);
        confirmDialog.open();
    }
}