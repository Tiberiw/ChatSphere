module org.map.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.map.socialnetwork to javafx.fxml;
    exports org.map.socialnetwork;

    opens org.map.socialnetwork.controller.admin to javafx.fxml;
    exports org.map.socialnetwork.controller.admin;

    opens org.map.socialnetwork.service to javafx.fxml;
    exports org.map.socialnetwork.service;

    opens org.map.socialnetwork.domain to javafx.base;

    exports org.map.socialnetwork.utils to javafx.fxml;

    opens org.map.socialnetwork.controller to javafx.fxml;
    exports org.map.socialnetwork.controller to javafx.fxml;

    opens org.map.socialnetwork.controller.user to javafx.fxml;
    exports org.map.socialnetwork.controller.user to javafx.fxml;

}