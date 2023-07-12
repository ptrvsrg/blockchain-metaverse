module ru.nsu.sberlab.startmenu {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.nsu.sberlab.startmenu to javafx.fxml;
    exports ru.nsu.sberlab.startmenu;
}