package Chart;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.scene.Group;

public class MainWindow extends Application {

    private int WINDOW_WIDTH = 1600;
    private int WINDOW_HEIGHT = 750;
    private int LEFT_BORDER = 50;

    public void start(Stage stage)
    {
        stage.setTitle("Dash board");

        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT-50);
        GraphicsContext context = canvas.getGraphicsContext2D();
        ChartBuilder chartBuilder = new ChartBuilder(context);


        // Text fields
        TextField processedObjectField = new TextField();
        processedObjectField.setLayoutX(200);
        processedObjectField.setLayoutY(400);
        processedObjectField.setMaxWidth(50);

        TextField correlatedObject1 = new TextField();
        correlatedObject1.setLayoutX(250);
        correlatedObject1.setLayoutY(450);
        correlatedObject1.setMaxWidth(50);

        TextField correlatedObject2 = new TextField();
        correlatedObject2.setLayoutX(330);
        correlatedObject2.setLayoutY(450);
        correlatedObject2.setMaxWidth(50);

        TextField histogramField = new TextField();
        histogramField.setLayoutX(200);
        histogramField.setLayoutY(500);
        histogramField.setMaxWidth(50);

        TextField correlationField = new TextField();
        correlationField.setLayoutX(200);
        correlationField.setLayoutY(550);
        correlationField.setMaxWidth(50);

        TextField mathExpectationField = new TextField();
        mathExpectationField.setLayoutX(270);
        mathExpectationField.setLayoutY(600);
        mathExpectationField.setMaxWidth(50);


        // Buttons
        Button timeChartButton = new Button("Build time chart");
        timeChartButton.setLayoutX(LEFT_BORDER);
        timeChartButton.setLayoutY(400);
        timeChartButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    if(chartBuilder.getOperationCode() != 1) chartBuilder.clear();
                    chartBuilder.setOperationCode(1);
                    chartBuilder.addProcessedObject(Integer.parseInt(processedObjectField.getText()));
                    chartBuilder.executeOperation();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    processedObjectField.setText("");
                    chartBuilder.drawAxises();
                    chartBuilder.drawUserTip();
                    showAlertDialog("Please, enter acceptable value");
                    return;
                }

                processedObjectField.setText("");
                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
            }
        });

        Button correlationChartButton = new Button("Build correlation chart");
        correlationChartButton.setLayoutX(LEFT_BORDER);
        correlationChartButton.setLayoutY(450);
        correlationChartButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.clear();
                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
                chartBuilder.setOperationCode(2);

                try {
                    chartBuilder.addProcessedObject(Integer.parseInt(correlatedObject1.getText()));
                    chartBuilder.addProcessedObject(Integer.parseInt(correlatedObject2.getText()));
                    correlatedObject1.setText("");
                    correlatedObject2.setText("");

                    chartBuilder.executeOperation();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    correlatedObject1.setText("");
                    correlatedObject2.setText("");
                    chartBuilder.drawAxises();
                    chartBuilder.drawUserTip();
                    showAlertDialog("Please, enter acceptable values");
                    return;
                }
            }
        });

        Button histogramButton = new Button("Build histogram");
        histogramButton.setLayoutX(LEFT_BORDER);
        histogramButton.setLayoutY(500);
        histogramButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.clear();
                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
                chartBuilder.setOperationCode(3);

                try {
                    chartBuilder.addProcessedObject(Integer.parseInt(histogramField.getText()));
                    histogramField.setText("");
                    chartBuilder.executeOperation();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    histogramField.setText("");
                    chartBuilder.drawAxises();
                    chartBuilder.drawUserTip();
                    showAlertDialog("Please, enter acceptable value");
                    return;
                }
            }
        });

        Button correlationButton = new Button("Count correlation");
        correlationButton.setLayoutX(LEFT_BORDER);
        correlationButton.setLayoutY(550);
        correlationButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.clear();
                chartBuilder.setOperationCode(4);

                try {
                    chartBuilder.addProcessedObject(Integer.parseInt(correlationField.getText()));
                    correlationField.setText("");
                    chartBuilder.executeOperation();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    correlationField.setText("");
                    chartBuilder.drawAxises();
                    chartBuilder.drawUserTip();
                    showAlertDialog("Please, enter acceptable value");
                    return;
                }

                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
            }
        });

        Button mathematicalExpectation = new Button("Mathematical expectation");
        mathematicalExpectation.setLayoutX(LEFT_BORDER);
        mathematicalExpectation.setLayoutY(600);
        mathematicalExpectation.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.clear();
                chartBuilder.setOperationCode(5);

                try {
                    chartBuilder.addProcessedObject(Integer.parseInt(mathExpectationField.getText()));
                    mathExpectationField.setText("");
                    chartBuilder.executeOperation();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    mathExpectationField.setText("");
                    chartBuilder.drawAxises();
                    chartBuilder.drawUserTip();
                    showAlertDialog("Please, enter acceptable value");
                    return;
                }

                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
            }
        });

        Button clearBtn = new Button("Clear");
        clearBtn.setLayoutX(LEFT_BORDER);
        clearBtn.setLayoutY(700);
        clearBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.clear();
                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
            }
        });

        Button addScaleButton = new Button("+");
        addScaleButton.setMinWidth(30);
        addScaleButton.setLayoutX(120);
        addScaleButton.setLayoutY(700);
        addScaleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.addScale();
                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
            }
        });

        Button subtractScaleButton = new Button("-");
        subtractScaleButton.setMinWidth(30);
        subtractScaleButton.setLayoutX(170);
        subtractScaleButton.setLayoutY(700);
        subtractScaleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                chartBuilder.subtractScale();
                chartBuilder.drawAxises();
                chartBuilder.drawUserTip();
            }
        });


        // create a Group
        Group group = new Group(canvas,
                timeChartButton,
                clearBtn,
                processedObjectField,
                correlationChartButton,
                correlatedObject1,
                correlatedObject2,
                addScaleButton,
                subtractScaleButton,
                histogramButton,
                histogramField,
                correlationButton,
                correlationField,
                mathematicalExpectation,
                mathExpectationField);

        // create a scene
        Scene scene = new Scene(group, WINDOW_WIDTH, WINDOW_HEIGHT);

        // set the scene
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void showAlertDialog (String information) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid operation");
        alert.setContentText(information);
        alert.showAndWait();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}