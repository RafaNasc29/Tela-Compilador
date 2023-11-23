module com.example.telacompilador {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires richtextfx.fat;
    requires javafx.graphics;

    opens com.example.telacompilador to javafx.fxml;
    exports com.example.telacompilador;
}