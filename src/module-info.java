module sdaProject {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    // Allow reflection access to the Bank package
    opens Bank to javafx.fxml;

    // Export Bank package for external use if needed
    exports Bank;
}
