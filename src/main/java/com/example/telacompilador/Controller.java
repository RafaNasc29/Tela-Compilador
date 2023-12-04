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
