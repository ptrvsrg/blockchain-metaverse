module ru.nsu.sberlab.startmenu {
    requires javafx.controls;
    requires javafx.fxml;
    requires wallet;
    requires crypto;
    requires lombok;
    requires ru.nsu.sberlab.gameintegration;

    exports ru.nsu.sberlab.startmenu.view;
    opens ru.nsu.sberlab.startmenu.view to javafx.fxml;
    exports ru.nsu.sberlab.startmenu.controller;
    opens ru.nsu.sberlab.startmenu.controller to javafx.fxml;
}