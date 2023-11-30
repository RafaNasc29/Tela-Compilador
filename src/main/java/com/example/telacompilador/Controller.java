package com.example.telacompilador;

import Compilador.Compilador;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;


import java.io.*;

public class Controller {
    @FXML
    private CodeArea codeArea;

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
        Compilador.run(selectedFile);
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
}
