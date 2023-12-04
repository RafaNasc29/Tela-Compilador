package com.example.telacompilador;

import Compilador.Compilador;
import Compilador.misc.ErrorMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;


import java.io.*;
import java.util.List;

public class Controller {
    @FXML
    private CodeArea codeArea;

    @FXML
    private ListView<String> console;
    private File selectedFile;

    @FXML
    public void saveOrNewMethod() throws IOException {
        Stage stage = new Stage();

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Save File");

        selectedFile = chooser.showSaveDialog(stage);
        FileWriter FW = new FileWriter(selectedFile.getAbsolutePath());
        FW.write(codeArea.getText());
        FW.close();
    }

    public void runMethod() throws IOException {
        saveMethod();
        Compilador.run(selectedFile);
        loadConsole();

        console.setOnMouseClicked(event -> {
            int selectedIndex = console.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedError = console.getItems().get(selectedIndex);
                int lineNumber = extractLineNumber(selectedError);
                if (lineNumber > 0) {
                    navigateToLine(lineNumber);
                }
            }
        });
    }

    private int extractLineNumber(String errorMessage) {
        // Procura pelo padrão "Erro na Linha [número]:"
        int lineNumber = -1;
        String pattern = "Erro na Linha ";
        int index = errorMessage.indexOf(pattern);
        if (index != -1) {
            String numString = errorMessage.substring(index + pattern.length());
            try {
                lineNumber = Integer.parseInt(numString.split(":")[0].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return lineNumber;
    }

    private void navigateToLine(int lineNumber) {
        codeArea.moveTo(lineNumber - 1, 0); // Move o cursor para a linha especificada
        codeArea.requestFollowCaret(); // Solicita à codeArea para rolar para a posição do cursor
    }

    public void loadConsole() {
        List<ErrorMessage> errorMessageList = Compilador.errorMessageList;

        ObservableList<String> errors = FXCollections.observableArrayList();
        if (!errorMessageList.isEmpty()) {
            for (ErrorMessage errorMessage : errorMessageList) {
                String errorString = " Erro na Linha " + errorMessage.line + ": " + errorMessage.message;
                errors.add(errorString);
            }
        }else{
            errors.add("Compilado Com Sucesso!");
        }
        console.setItems(errors);
    }

    public void openMethod() throws IOException {
        Stage stage = new Stage();

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Open File");

        selectedFile = chooser.showOpenDialog(stage);
        FileReader FR = new FileReader(selectedFile.getAbsolutePath());
        BufferedReader BR = new BufferedReader(FR);

        StringBuilder sb = new StringBuilder();
        String myText;

        while ((myText = BR.readLine()) != null) {
            sb.append(myText).append("\n");
        }

        codeArea.replaceText(sb.toString());
    }

    public void exitMethod() {
        Platform.exit();
    }

    public void setCodeArea(CodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public void saveMethod() throws IOException {
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Falha ao Salvar");
            alert.setHeaderText("Arquivo Não encontrado!\nPor favor Salve o arquivo através do 'Save As'");
            alert.showAndWait();
        }
        assert selectedFile != null;
        FileWriter FW = new FileWriter(selectedFile.getAbsolutePath());
        FW.write(codeArea.getText());
        FW.close();
    }
}
