module pinoteaux.projetrioc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens pinoteaux.projetrioc to javafx.fxml;
    exports pinoteaux.projetrioc;
}