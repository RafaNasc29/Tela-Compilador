package com.example.telacompilador;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.value.Val;

import java.io.IOException;
import java.util.function.IntFunction;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("compilador.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 824, 623);

        Controller controller = fxmlLoader.getController();

        CodeArea codeArea = (CodeArea) fxmlLoader.getNamespace().get("codeArea");

        controller.setCodeArea(codeArea);

        IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
        IntFunction<Node> arrowFactory = new ArrowFactory(codeArea.currentParagraphProperty());
        IntFunction<Node> graphicFactory = line -> {
            HBox hbox = new HBox(
                    numberFactory.apply(line),
                    arrowFactory.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };

        codeArea.setParagraphGraphicFactory(graphicFactory);
        stage.setTitle("Compilador");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

class ArrowFactory implements IntFunction<Node> {
    private final ObservableValue<Integer> shownLine;

    ArrowFactory(ObservableValue<Integer> shownLine) {
        this.shownLine = shownLine;
    }

    @Override
    public Node apply(int lineNumber) {
        javafx.scene.shape.Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
        triangle.setFill(Color.GREEN);

        ObservableValue<Boolean> visible = Val.map(shownLine, sl -> sl == lineNumber);

        triangle.visibleProperty().bind(
                Val.flatMap(triangle.sceneProperty(), scene -> scene != null ? visible : Val.constant(false)));

        return triangle;
    }
}

