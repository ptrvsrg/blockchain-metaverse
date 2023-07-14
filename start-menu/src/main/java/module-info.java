module ru.nsu.sberlab.startmenu {
    requires javafx.controls;
    requires javafx.fxml;

    exports ru.nsu.sberlab.startmenu.view;
    opens ru.nsu.sberlab.startmenu.view to javafx.fxml;
    exports ru.nsu.sberlab.startmenu.controller;
    opens ru.nsu.sberlab.startmenu.controller to javafx.fxml;
}