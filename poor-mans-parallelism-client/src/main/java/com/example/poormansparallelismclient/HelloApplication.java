package com.example.poormansparallelismclient;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GridPane formPane = getFormPane();
        addUIControls(formPane);
        Scene scene = new Scene(formPane, 800, 800);
        stage.setTitle("Mandelbrot image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void addUIControls(GridPane gridPane) {
        Label headerLabel = new Label("Mandelbrot image");
        gridPane.add(headerLabel, 0, 0, 2, 1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0, 20, 0));

        Label min_c_re_label = new Label("Min_c_re");
        gridPane.add(min_c_re_label, 0, 1);
        TextField min_c_re_textField = new TextField();
        min_c_re_textField.setPrefHeight(40);
        gridPane.add(min_c_re_textField, 1, 1);


        Label min_c_im_label = new Label("Min_c_im");
        gridPane.add(min_c_im_label, 0, 2);
        TextField min_c_im_textField = new TextField();
        min_c_im_textField.setPrefHeight(40);
        gridPane.add(min_c_im_textField, 1, 2);

        Label max_c_re_label = new Label("Max_c_re");
        gridPane.add(max_c_re_label, 0, 3);
        TextField max_c_re_textField = new TextField();
        max_c_re_textField.setPrefHeight(40);
        gridPane.add(max_c_re_textField, 1, 3);

        Label max_c_im_label = new Label("Max_c_im");
        gridPane.add(max_c_im_label, 0, 4);
        TextField max_c_im_textField = new TextField();
        max_c_im_textField.setPrefHeight(40);
        gridPane.add(max_c_im_textField, 1, 4);

        Label x_label = new Label("x");
        gridPane.add(x_label, 0, 5);
        TextField x_textField = new TextField();
        x_textField.setPrefHeight(40);
        gridPane.add(x_textField, 1, 5);

        Label y_label = new Label("y");
        gridPane.add(y_label, 0, 6);
        TextField y_textField = new TextField();
        y_textField.setPrefHeight(40);
        gridPane.add(y_textField, 1, 6);

        Label inf_n_label = new Label("inf_n");
        gridPane.add(inf_n_label, 0, 7);
        TextField inf_n_textField = new TextField();
        inf_n_textField.setPrefHeight(40);
        gridPane.add(inf_n_textField, 1, 7);

        Label serverPortsLabel = new Label("Server ports to distribute workload on:");
        gridPane.add(serverPortsLabel, 0, 8, 2, 1);
        Map<Integer, Boolean> serverPortsMap = new HashMap<>();
        for (int i = 9000; i < 9010; i++) {
            serverPortsMap.put(i, false);
        }
        String[] serverPorts = {"9000", "9001", "9002", "9003", "9004", "9005", "9006", "9007", "9008", "9009"};
        GridPane checkBoxPane = new GridPane();
        gridPane.add(checkBoxPane, 0, 9, 10, 1);

        for (int i = 0; i < serverPorts.length; i++) {
            CheckBox checkBox = new CheckBox(serverPorts[i]);
            checkBox.setOnAction(actionEvent -> {
                if (checkBox.isSelected())
                    serverPortsMap.put(Integer.parseInt(checkBox.getText()), true);
                else
                    serverPortsMap.put(Integer.parseInt(checkBox.getText()), false);
            });
            checkBoxPane.add(checkBox, i, 0, 1, 1);
        }

        Button calculateButton = new Button("Calculate");
        calculateButton.setPrefHeight(40);
        calculateButton.setPrefWidth(100);
        calculateButton.setDefaultButton(true);
        gridPane.add(calculateButton, 0, 10, 2, 1);
        GridPane.setHalignment(calculateButton, HPos.CENTER);
        GridPane.setMargin(calculateButton, new Insets(20, 0, 20, 0));

        calculateButton.setOnAction(actionEvent -> {
            double min_c_re = Double.parseDouble(min_c_re_textField.getText());
            double min_c_im = Double.parseDouble(min_c_im_textField.getText());
            double max_c_re = Double.parseDouble(max_c_re_textField.getText());
            double max_c_im = Double.parseDouble(max_c_im_textField.getText());
            int width = Integer.parseInt(x_textField.getText());
            int height = Integer.parseInt(y_textField.getText());
            int inf_n = Integer.parseInt(inf_n_textField.getText());

            List<Integer> serverPortList = serverPortsMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
            int serverPortSize = serverPortList.size();

            // Checking the greatest possible number of server ports to utilize
            while (!(height % serverPortSize == 0)) {
                serverPortSize--;
            }

            double[][] apportionMinAndMaxIm = new double[serverPortSize][2];
            double slice = (max_c_im - min_c_im) / serverPortSize;
            double tempMinAndMaxC = min_c_im;
            for (int i = 0; i < apportionMinAndMaxIm.length; i++) {
                apportionMinAndMaxIm[i][0] = tempMinAndMaxC;
                tempMinAndMaxC += slice;
                apportionMinAndMaxIm[i][1] = tempMinAndMaxC;
            }
            int apportionHeight = height / serverPortSize;

            BufferedImage[] bufferedImageList = new BufferedImage[serverPortSize];

            StackPane stackPane = new StackPane();
            for (int i = 0; i < serverPortSize; i++) {

                BufferedImage bufferedImage = MandelbrotImage.getImage(
                        min_c_re, apportionMinAndMaxIm[i][0], max_c_re, apportionMinAndMaxIm[i][1], width, apportionHeight, inf_n, serverPortList.get(i));
                ImageView imageView = new ImageView(SwingFXUtils.toFXImage(bufferedImage, null));
                stackPane.getChildren().add(imageView);

//                    int finalI = i;
//                    new Thread(() -> {
//                        BufferedImage bufferedImage = MandelbrotImage.getImage(min_c_re, apportionMinAndMaxIm[finalI][0], max_c_re,
//                                apportionMinAndMaxIm[finalI][1], width, apportionHeight, inf_n, serverPortList.get(finalI));
//                        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(bufferedImage, null));
//                        gridPane.add(imageView, 0, finalI, 2, 2);
//                    }).start();

            }
            Stage imageStage = new Stage();
            Scene scene = new Scene(stackPane);
            imageStage.setScene(scene);
            imageStage.show();
        });

    }

    private GridPane getFormPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        return gridPane;
    }

}