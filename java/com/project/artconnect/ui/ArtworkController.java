package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArtworkController {

    @FXML private TableView<Artwork>            artworkTable;
    @FXML private TableColumn<Artwork, String>  titleColumn;
    @FXML private TableColumn<Artwork, String>  typeColumn;
    @FXML private TableColumn<Artwork, Double>  priceColumn;
    @FXML private TableColumn<Artwork, String>  statusColumn;
    @FXML private TableColumn<Artwork, String>  artistColumn;

    @FXML private TextField              titleField;
    @FXML private ComboBox<Artist>       artistCombo;
    @FXML private TextField              mediumField;
    @FXML private TextField              yearField;
    @FXML private TextField              priceField;
    @FXML private ComboBox<Artwork.Status> statusCombo;
    @FXML private Label                  statusLabel;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private final ArtistService  artistService  = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getArtist() != null
                        ? cellData.getValue().getArtist().getName() : "Unknown"));

        artistCombo.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
        statusCombo.setItems(FXCollections.observableArrayList(Artwork.Status.values()));

        refreshTable();

        artworkTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> { if (newVal != null) populateForm(newVal); });
    }

    @FXML
    private void handleAdd() {
        Artwork artwork = buildFromForm();
        if (artwork == null) return;
        try {
            artworkService.createArtwork(artwork);
            refreshTable();
            handleClear();
            setStatus("Œuvre ajoutée avec succès.", true);
        } catch (Exception e) {
            setStatus("Erreur lors de l'ajout : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleUpdate() {
        Artwork artwork = buildFromForm();
        if (artwork == null) return;
        try {
            artworkService.updateArtwork(artwork);
            refreshTable();
            setStatus("Œuvre mise à jour avec succès.", true);
        } catch (Exception e) {
            setStatus("Erreur lors de la mise à jour : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleDelete() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            setStatus("Sélectionnez une œuvre dans le tableau avant de supprimer.", false);
            return;
        }
        try {
            artworkService.deleteArtwork(title);
            refreshTable();
            handleClear();
            setStatus("Œuvre supprimée.", true);
        } catch (Exception e) {
            setStatus("Erreur lors de la suppression : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleClear() {
        titleField.clear();
        artistCombo.setValue(null);
        mediumField.clear();
        yearField.clear();
        priceField.clear();
        statusCombo.setValue(null);
        artworkTable.getSelectionModel().clearSelection();
        statusLabel.setText("");
    }

    private void refreshTable() {
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
    }

    private void populateForm(Artwork artwork) {
        titleField.setText(artwork.getTitle() != null ? artwork.getTitle() : "");
        if (artwork.getArtist() != null) {
            String name = artwork.getArtist().getName();
            artistCombo.getItems().stream()
                    .filter(a -> a.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .ifPresent(artistCombo::setValue);
        } else {
            artistCombo.setValue(null);
        }
        mediumField.setText(artwork.getMedium() != null ? artwork.getMedium() : "");
        yearField.setText(artwork.getCreationYear() != null
                ? String.valueOf(artwork.getCreationYear()) : "");
        priceField.setText(String.valueOf(artwork.getPrice()));
        statusCombo.setValue(artwork.getStatus());
        statusLabel.setText("");
    }

    private Artwork buildFromForm() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            setStatus("Le titre est obligatoire.", false);
            return null;
        }
        Artwork a = new Artwork();
        a.setTitle(title);
        a.setArtist(artistCombo.getValue());
        String medium = mediumField.getText().trim();
        a.setMedium(medium.isEmpty() ? null : medium);
        a.setType(medium.isEmpty() ? null : medium);
        String yearText = yearField.getText().trim();
        if (!yearText.isEmpty()) {
            try {
                a.setCreationYear(Integer.parseInt(yearText));
            } catch (NumberFormatException ex) {
                setStatus("L'année doit être un entier.", false);
                return null;
            }
        }
        String priceText = priceField.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                a.setPrice(Double.parseDouble(priceText));
            } catch (NumberFormatException ex) {
                setStatus("Le prix doit être un nombre.", false);
                return null;
            }
        }
        a.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : Artwork.Status.FOR_SALE);
        return a;
    }

    private void setStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #388e3c;"
                : "-fx-text-fill: #c62828;");
    }
}