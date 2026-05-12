package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArtistController {

    // --- Barre de recherche ---
    @FXML private TextField  searchField;
    @FXML private ComboBox<Discipline> disciplineFilter;

    // --- Tableau ---
    @FXML private TableView<Artist>        artistTable;
    @FXML private TableColumn<Artist, String>  nameColumn;
    @FXML private TableColumn<Artist, String>  cityColumn;
    @FXML private TableColumn<Artist, String>  emailColumn;
    @FXML private TableColumn<Artist, Integer> yearColumn;

    // --- Formulaire CRUD ---
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField cityField;
    @FXML private TextField birthYearField;
    @FXML private Label     statusLabel;

    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        disciplineFilter.setItems(FXCollections.observableArrayList(artistService.getAllDisciplines()));
        refreshTable();

        // Clic sur une ligne → remplit le formulaire
        artistTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> { if (newVal != null) populateForm(newVal); });
    }

    // ----------------------------------------------------------------
    // Recherche / Réinitialisation
    // ----------------------------------------------------------------
    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        Discipline d = disciplineFilter.getValue();
        String dName = (d != null) ? d.getName() : null;
        artistTable.setItems(FXCollections.observableArrayList(
                artistService.searchArtists(query, dName, null)));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        disciplineFilter.setValue(null);
        refreshTable();
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------
    @FXML
    private void handleAdd() {
        Artist artist = buildArtistFromForm();
        if (artist == null) return;
        try {
            artistService.createArtist(artist);
            refreshTable();
            handleClear();
            setStatus("Artiste ajouté avec succès.", true);
        } catch (Exception e) {
            setStatus("Erreur lors de l'ajout : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdate() {
        Artist artist = buildArtistFromForm();
        if (artist == null) return;
        try {
            artistService.updateArtist(artist);
            refreshTable();
            setStatus("Artiste mis à jour avec succès.", true);
        } catch (Exception e) {
            setStatus("Erreur lors de la mise à jour : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDelete() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            setStatus("Sélectionnez un artiste dans le tableau avant de supprimer.", false);
            return;
        }
        try {
            artistService.deleteArtist(name);
            refreshTable();
            handleClear();
            setStatus("Artiste supprimé.", true);
        } catch (Exception e) {
            setStatus("Erreur lors de la suppression : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        emailField.clear();
        cityField.clear();
        birthYearField.clear();
        artistTable.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private void refreshTable() {
        artistTable.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }

    private void populateForm(Artist artist) {
        nameField.setText(artist.getName() != null ? artist.getName() : "");
        emailField.setText(artist.getContactEmail() != null ? artist.getContactEmail() : "");
        cityField.setText(artist.getCity() != null ? artist.getCity() : "");
        birthYearField.setText(artist.getBirthYear() != null ? String.valueOf(artist.getBirthYear()) : "");
        statusLabel.setText("");
    }

    private Artist buildArtistFromForm() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            setStatus("Le nom et l'email sont obligatoires.", false);
            return null;
        }
        Artist a = new Artist();
        a.setName(name);
        a.setContactEmail(email);
        a.setCity(cityField.getText().trim().isEmpty() ? null : cityField.getText().trim());
        a.setActive(true);
        String yearText = birthYearField.getText().trim();
        if (!yearText.isEmpty()) {
            try {
                a.setBirthYear(Integer.parseInt(yearText));
            } catch (NumberFormatException ex) {
                setStatus("L'année de naissance doit être un nombre entier.", false);
                return null;
            }
        }
        return a;
    }

    private void setStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #388e3c;"
                : "-fx-text-fill: #c62828;");
    }
}