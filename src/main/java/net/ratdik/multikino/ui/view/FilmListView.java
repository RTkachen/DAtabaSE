package net.ratdik.multikino.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.ratdik.multikino.domain.Film;
import net.ratdik.multikino.service.FilmService;

@Route(value = "films", layout = net.ratdik.multikino.ui.view.AdminLayout.class)
@PageTitle("Filmy | Cinema App")
public class FilmListView extends VerticalLayout {
    public FilmListView(FilmService filmService) {
        Grid<Film> grid = new Grid<>(Film.class);
        grid.removeAllColumns();
        grid.addColumn(Film::getId).setHeader("ID");
        grid.addColumn(Film::getNazwa).setHeader("Nazwa");
        grid.addColumn(Film::getRezyser).setHeader("Reżyser");
        grid.addColumn(Film::getCzasTrwania).setHeader("Czas");
        grid.addColumn(Film::getGatunek).setHeader("Gatunek");
        grid.addColumn(Film::getKrajPochodzenia).setHeader("Kraj");
        grid.addColumn(Film::getJezykWyswietlania).setHeader("Język");
        grid.addColumn(Film::getOcena).setHeader("Ocena");
        grid.setItems(filmService.findAll());
        add(grid);
    }
}
