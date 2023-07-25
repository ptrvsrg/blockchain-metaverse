module ru.nsu.sberlab.startmenu {
    requires javafx.controls;
    requires javafx.fxml;
//    requires wallet;
//    requires crypto;
    requires lombok;
    requires ru.nsu.sberlab.gameintegration;
    requires blockchainintegration;
    requires core;
    requires java.sql;
    requires org.apache.logging.log4j;

    exports ru.nsu.sberlab.startmenu.view;
    opens ru.nsu.sberlab.startmenu.view to javafx.fxml;
    exports ru.nsu.sberlab.startmenu.controller;
    opens ru.nsu.sberlab.startmenu.controller to javafx.fxml;
}
