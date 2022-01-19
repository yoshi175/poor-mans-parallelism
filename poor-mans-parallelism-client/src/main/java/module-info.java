module com.example.poormansparallelismclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires unirest.java;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.poormansparallelismclient to javafx.fxml;
    exports com.example.poormansparallelismclient;
}