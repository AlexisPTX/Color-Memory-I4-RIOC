module pinoteaux.projetrioc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens pinoteaux.projetrioc to javafx.fxml;
    exports pinoteaux.projetrioc;
    exports pinoteaux.projetrioc.gamepart;
    opens pinoteaux.projetrioc.gamepart to javafx.fxml;
    exports pinoteaux.projetrioc.menu;
    opens pinoteaux.projetrioc.menu to javafx.fxml;
}