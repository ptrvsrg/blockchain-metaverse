module startmenu {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.apache.logging.log4j;
    requires gameintegration;
    requires blockchainintegration;
    requires core;
    requires java.sql;

    exports ru.nsu.sberlab.startmenu.view;
    opens ru.nsu.sberlab.startmenu.view to javafx.fxml;
    exports ru.nsu.sberlab.startmenu.controller;
    opens ru.nsu.sberlab.startmenu.controller to javafx.fxml;
}
