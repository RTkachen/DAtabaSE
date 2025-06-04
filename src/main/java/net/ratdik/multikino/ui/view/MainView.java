package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.ratdik.multikino.domain.Seans;
import net.ratdik.multikino.service.SeansService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = net.ratdik.multikino.ui.view.MainLayout.class)
@PageTitle("Repertuar | Cinema App")
public class MainView extends VerticalLayout implements AfterNavigationObserver {

    private final SeansService seansService;
    private final Grid<Seans> grid = new Grid<>(Seans.class, false);
    private final TextField filterTime = new TextField();

    @Autowired
    public MainView(SeansService seansService) {
        this.seansService = seansService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // 1) Nagłówek
        H2 title = new H2("Dostępne seanse");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);

        // 2) Panel filtrów
        filterTime.setPlaceholder("Filtruj po godzinie (HH:mm)");
        filterTime.setClearButtonVisible(true);
        filterTime.setValueChangeMode(ValueChangeMode.LAZY);
        filterTime.addValueChangeListener(e -> updateGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterTime);
        toolbar.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        // 3) Konfiguracja kolumn grida
        grid.addColumn(s -> s.getFilm().getNazwa()).setHeader("Film").setAutoWidth(true);
        grid.addColumn(Seans::getCzasRozpoczecia).setHeader("Czas rozpoczęcia").setAutoWidth(true);
        grid.addColumn(Seans::getCzasZakonczenia).setHeader("Czas zakończenia").setAutoWidth(true);
        grid.addColumn(s -> s.isDubbing() ? "Dubbing" : "Napsi").setHeader("Wersja").setAutoWidth(true);
        grid.addColumn(Seans::getWolneMiejsca).setHeader("Wolne miejsca").setAutoWidth(true);

        grid.setSizeFull();

        // 4) Układ: tytuł, filtr, lista
        add(title, toolbar, grid);
        expand(grid);
    }

    private void updateGrid() {
        String timeFilter = filterTime.getValue().trim();
        if (timeFilter.isEmpty()) {
            grid.setItems(seansService.findAll());
        } else {
            grid.setItems(seansService.findByHour(timeFilter));
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Załaduj dane przy pierwszym wejściu
        updateGrid();
    }
}
