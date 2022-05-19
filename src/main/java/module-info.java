module edu.rtu.dynamix {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens edu.rtu.dynamix to javafx.fxml;
    exports edu.rtu.dynamix;
}