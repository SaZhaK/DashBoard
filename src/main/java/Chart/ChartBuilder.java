package Chart;

import au.com.bytecode.opencsv.CSVReader;
import javafx.scene.canvas.GraphicsContext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ChartBuilder {

    private GraphicsContext context;

    private CSVReader reader = null;
    private String[] nextLine;
    private int totalAmountOfObjects = 21;

    private int xAxis = 300;
    private int yAxis = 50;
    private double scale = 10;
    private double[] prevX;
    private double[] prevY;

    private int operationCode;
    private ArrayList<Integer> processedObjects = new ArrayList<>();

    private Color[] objectNameColors = new Color[totalAmountOfObjects + 1];

    private Color[] colors = new Color[]{
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.ORANGE,
            Color.VIOLET,
            Color.SKYBLUE,
            Color.SALMON,
            Color.PURPLE,
            Color.PERU,
            Color.SEAGREEN,
            Color.ROSYBROWN,
            Color.YELLOWGREEN,
            Color.TAN,
            Color.TOMATO,
            Color.OLIVE,
            Color.ROYALBLUE,
            Color.DEEPPINK,
            Color.NAVY,
            Color.MAROON,
            Color.ORCHID,
            Color.DARKVIOLET
    };

    public ChartBuilder(GraphicsContext context) {
        this.context = context;
        clear();
        drawAxises();
        drawUserTip();
    }

    private void setObjectNameColors() {
        for (int i = 0; i < totalAmountOfObjects + 1; i++) {
            objectNameColors[i] = Color.BLACK;
        }
    }

    public void addScale() {
        this.scale += 2;
        context.clearRect(0, 0, 1600, 650);
        executeOperation();
    }

    public void subtractScale() {
        if (this.scale - 2 > 0) {
            if (operationCode == 3) this.scale -= 50;
            else this.scale -= 2;
            context.clearRect(0, 0, 1600, 650);
            executeOperation();
        }
    }

    public void clear() {
        this.processedObjects = new ArrayList<>();
        this.operationCode = 0;
        this.scale = 20;
        setObjectNameColors();
        context.clearRect(0, 0, 1600, 650);
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public int getOperationCode() {
        return operationCode;
    }

    public void addProcessedObject(int processedObject) {
        this.processedObjects.add(processedObject);
    }

    private void openCSV() throws FileNotFoundException {
        reader = new CSVReader(new FileReader("Data/series.csv"), ',', '"', 0);
    }

    private void timeChart() throws IOException, NumberFormatException {
        double x = yAxis, y;

        context.beginPath();
        context.setStroke(Color.BLACK);
        context.strokeText("Time", 1500, xAxis + 20);
        context.strokeText("Price", yAxis + 10, 50);
        context.closePath();

        int curYear = 1960;
        int counter = 0;
        int place = 0;
        nextLine = reader.readNext(); // Reading column names
        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < processedObjects.size(); i++) {
                y = 300. - scale * Double.parseDouble(nextLine[processedObjects.get(i)]);

                context.beginPath();
                context.setStroke(colors[i % colors.length]);
                objectNameColors[processedObjects.get(i)] = colors[i % colors.length];
                context.moveTo(prevX[i], prevY[i]);
                context.lineTo(x, y);
                context.stroke();
                context.closePath();

                prevX[i] = x;
                prevY[i] = y;
            }
            context.beginPath();
            context.setStroke(Color.BLACK);
            context.setFont(Font.font("Arial", 10));
            counter++;
            if (counter % 12 == 0) {
                place++;
                curYear++;
                if (place % 2 == 0) context.strokeText(String.valueOf(curYear), x, xAxis + 15);
                else context.strokeText(String.valueOf(curYear), x, xAxis + 30);
            }
            context.closePath();
            x += scale / 10.;
        }

        reader.close();
    }

    private void correlationChart() throws IOException {
        double x, y;
        double dotRadius = 4;

        nextLine = reader.readNext(); // Reading column names
        context.beginPath();
        context.setStroke(Color.BLACK);
        context.strokeText(nextLine[processedObjects.get(0)], 1450, xAxis + 20);
        context.strokeText(nextLine[processedObjects.get(1)], yAxis + 10, 50);
        context.closePath();

        while ((nextLine = reader.readNext()) != null) {
            x = 50. + scale * Double.parseDouble(nextLine[processedObjects.get(0)]);
            y = 300. - scale * Double.parseDouble(nextLine[processedObjects.get(1)]);

            context.beginPath();
            context.setFill(colors[0]);
            context.fillOval(x, y, dotRadius, dotRadius);
            context.stroke();
            context.closePath();
        }

        reader.close();
    }

    private void histogram() throws IOException {
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        int sampleSize = 0;

        // Getting min and max value
        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            if (Double.parseDouble(nextLine[processedObjects.get(0)]) < xMin)
                xMin = Double.parseDouble(nextLine[processedObjects.get(0)]);

            if (Double.parseDouble(nextLine[processedObjects.get(0)]) > xMax)
                xMax = Double.parseDouble(nextLine[processedObjects.get(0)]);

            ++sampleSize;
        }

        // Calculating scope of variation
        double scopeOfVariation = xMax - xMin;

        // Sturges formula
        int amountOfIntervals = (int) (1 + 3.322 * Math.log10(sampleSize));

        //Calculating interval length
        double intervalLength = scopeOfVariation / amountOfIntervals;

        // Setting intervals
        double leftBorder = xMin;
        Interval[] intervals = new Interval[amountOfIntervals];
        for (int i = 0; i < amountOfIntervals; i++) {
            double rightBorder = leftBorder + intervalLength;
            intervals[i] = new Interval(leftBorder, rightBorder);
            leftBorder = rightBorder;
        }

        reader.close();
        openCSV();
        nextLine = reader.readNext();

        int[] counters = new int[amountOfIntervals];
        double[] relativeFrequencies = new double[amountOfIntervals];
        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < amountOfIntervals; i++) {
                if (Double.parseDouble(nextLine[processedObjects.get(0)]) >= intervals[i].getLeftBorder() &&
                        Double.parseDouble(nextLine[processedObjects.get(0)]) < intervals[i].getRightBorder())
                    ++counters[i];
            }
        }

        double maxRelativeFrequency = Double.MIN_VALUE;
        for (int i = 0; i < amountOfIntervals; i++) {
            relativeFrequencies[i] = (1.) * counters[i] / sampleSize;

            if (relativeFrequencies[i] > maxRelativeFrequency) maxRelativeFrequency = relativeFrequencies[i];
        }

        double x = yAxis + 20;

        double scaleY = 200 / maxRelativeFrequency;
        double scaleX = 1000. / (amountOfIntervals * intervalLength);

        context.beginPath();

        int accuracy = 100;

        for (int i = 0; i < amountOfIntervals; i++) {
            context.setFill(colors[i % colors.length]);
            context.fillRect(x, xAxis - relativeFrequencies[i] * scaleY,
                    intervalLength * scaleX, relativeFrequencies[i] * scaleY);
            context.strokeText(((double) Math.round(intervals[i].getLeftBorder() * accuracy) / accuracy) + " - " +
                    ((double) Math.round(intervals[i].getRightBorder() * accuracy) / accuracy), x + 18, xAxis + 20);

            x += intervalLength * scaleX;
        }

        context.strokeText("Price", 1500, xAxis + 20);
        context.strokeText("Relative frequency", yAxis + 10, 50);
        context.closePath();
        reader.close();
    }

    private void mathematicalExpectation() throws IOException {

        timeChart();

        openCSV();
        int totalAmount = 0;
        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            ++totalAmount;
        }
        reader.close();

        openCSV();

        // Getting min and max value
        nextLine = reader.readNext();
        double sum = 0;
        while ((nextLine = reader.readNext()) != null) {
            sum += Double.parseDouble(nextLine[processedObjects.get(0)]);
        }

        double averageY = sum / totalAmount;

        reader.close();

        int accuracy = 100;

        context.beginPath();
        context.moveTo(50, 300. - scale * averageY);
        context.lineTo(1500, 300. - scale * averageY);
        context.setStroke(colors[1]);
        context.strokeText(String.valueOf((double) Math.round(averageY * accuracy) / accuracy), 20, 300. - scale * averageY);
        context.stroke();
        context.closePath();

        openCSV();

        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        int sampleSize = 0;

        // Getting min and max value
        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            if (Double.parseDouble(nextLine[processedObjects.get(0)]) < xMin)
                xMin = Double.parseDouble(nextLine[processedObjects.get(0)]);

            if (Double.parseDouble(nextLine[processedObjects.get(0)]) > xMax)
                xMax = Double.parseDouble(nextLine[processedObjects.get(0)]);

            ++sampleSize;
        }

        // Calculating scope of variation
        double scopeOfVariation = xMax - xMin;

        // Sturges formula
        int amountOfIntervals = (int) (1 + 3.322 * Math.log10(sampleSize));

        //Calculating interval length
        double intervalLength = scopeOfVariation / amountOfIntervals;

        // Setting intervals
        double leftBorder = xMin;
        Interval[] intervals = new Interval[amountOfIntervals];
        for (int i = 0; i < amountOfIntervals; i++) {
            double rightBorder = leftBorder + intervalLength;
            intervals[i] = new Interval(leftBorder, rightBorder);
            leftBorder = rightBorder;
        }

        reader.close();
        openCSV();
        nextLine = reader.readNext();

        int[] counters = new int[amountOfIntervals];
        double[] relativeFrequencies = new double[amountOfIntervals];
        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < amountOfIntervals; i++) {
                if (Double.parseDouble(nextLine[processedObjects.get(0)]) >= intervals[i].getLeftBorder() &&
                        Double.parseDouble(nextLine[processedObjects.get(0)]) < intervals[i].getRightBorder())
                    ++counters[i];
            }
        }

        for (int i = 0; i < amountOfIntervals; i++) {
            relativeFrequencies[i] = (1.) * counters[i] / sampleSize;
        }

        reader.close();

        double expectedY = 0;
        openCSV();
        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < amountOfIntervals; i++) {
                if (Double.parseDouble(nextLine[processedObjects.get(0)]) >= intervals[i].getLeftBorder() &&
                        Double.parseDouble(nextLine[processedObjects.get(0)]) < intervals[i].getRightBorder()) {
                    expectedY += Double.parseDouble(nextLine[processedObjects.get(0)]) * relativeFrequencies[i];
                }
            }
        }

        expectedY /= 100;

        reader.close();

        openCSV();

        double averageDispersion = 0;
        double mathExpectationDispersion = 0;

        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            averageDispersion += Math.pow((Double.parseDouble(nextLine[processedObjects.get(0)]) - averageY), 2);
            mathExpectationDispersion += Math.pow((Double.parseDouble(nextLine[processedObjects.get(0)]) - expectedY), 2);
        }

        averageDispersion /= sampleSize;
        mathExpectationDispersion /= sampleSize;

        context.beginPath();
        context.setStroke(colors[2]);
        context.moveTo(50, (300. - scale * expectedY));
        context.lineTo(1500, (300. - scale * expectedY));
        context.strokeText(String.valueOf((double) Math.round(expectedY * accuracy) / accuracy), 1520, 300. - scale * expectedY);
        context.stroke();

        context.setFill(colors[1]);
        context.fillRect(750, 400, 50, 10);
        context.setFill(colors[2]);
        context.fillRect(750, 430, 50, 10);

        context.setStroke(Color.BLACK);
        context.setFont(Font.font("Arial Bold", 14));
        context.strokeText("Arithmetic mean: " + (double) Math.round(averageY * accuracy) / accuracy, 500, 410);
        context.strokeText("Mathematical expectation: " + (double) Math.round(expectedY * accuracy) / accuracy, 500, 440);
        context.strokeText("Difference: " + Math.abs((double) Math.round((averageY - expectedY) * accuracy) / accuracy), 500, 470);
        context.strokeText("Dispersion relevant to the arithmetic mean: " +
                (double) Math.round(averageDispersion * accuracy) / accuracy, 500, 500);
        context.strokeText("Dispersion relevant to the mathematical expectation: " +
                (double) Math.round(mathExpectationDispersion * accuracy) / accuracy, 500, 530);

        context.closePath();

        reader.close();
    }

    public void correlationMatrix() throws IOException {
        openCSV();

        double[] averageX = new double[totalAmountOfObjects];
        double[] averageY = new double[totalAmountOfObjects];
        for (int i = 0; i < totalAmountOfObjects; i++) {
            averageX[i] = 0;
            averageY[i] = 0;
        }

        int counter = 0;
        nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < totalAmountOfObjects; i++) {
                averageX[i] += Double.parseDouble(nextLine[processedObjects.get(0)]);
                averageY[i] += Double.parseDouble(nextLine[i + 1]);

            }
            counter++;
        }
        reader.close();

        for (int i = 0; i < totalAmountOfObjects; i++) {
            averageX[i] /= counter;
            averageY[i] /= counter;
        }

        openCSV();
        nextLine = reader.readNext();
        double[] nominator = new double[totalAmountOfObjects];
        double[] denominatorFactor1 = new double[totalAmountOfObjects];
        double[] denominatorFactor2 = new double[totalAmountOfObjects];
        double[] factor1 = new double[totalAmountOfObjects];
        double[] factor2 = new double[totalAmountOfObjects];

        for (int i = 0; i < totalAmountOfObjects; i++) {
            nominator[i] = 0;
            denominatorFactor1[i] = 0;
            denominatorFactor2[i] = 0;
        }
        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < totalAmountOfObjects; i++) {
                factor1[i] = Double.parseDouble(nextLine[processedObjects.get(0)]) - averageX[i];
                factor2[i] = Double.parseDouble(nextLine[i + 1]) - averageY[i];
                nominator[i] += factor1[i] * factor2[i];

                denominatorFactor1[i] += factor1[i] * factor1[i];
                denominatorFactor2[i] += factor2[i] * factor2[i];
            }
        }
        reader.close();

        int accuracy = 1000;
        double[] correlationMatrix = new double[totalAmountOfObjects];

        for (int i = 0; i < totalAmountOfObjects; i++) {
            correlationMatrix[i] = (double) Math.round((nominator[i] / Math.sqrt(denominatorFactor1[i] * denominatorFactor2[i]))
                    * accuracy) / accuracy;
        }

        int x = 50;
        context.beginPath();
        for (int i = 0; i < totalAmountOfObjects; i++) {
            if (correlationMatrix[i] != 1) {
                context.setFill(colors[i % colors.length]);
                context.fillRect(x, xAxis - correlationMatrix[i] * 200, 50, correlationMatrix[i] * 200);

                context.setStroke(Color.BLACK);
                context.strokeText(String.valueOf(correlationMatrix[i]), x + 5, xAxis + 20);

                objectNameColors[i + 1] = colors[i % colors.length];

                x += 50;
            }
        }
        context.closePath();
    }

    public void executeOperation() {

        // Opening CSV
        try {
            openCSV();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Setting starting coordinates
        prevX = new double[totalAmountOfObjects];
        prevY = new double[totalAmountOfObjects];
        for (int i = 0; i < totalAmountOfObjects; i++) {
            prevX[i] = yAxis;
            prevY[i] = xAxis;
        }

        try {
            switch (operationCode) {
                case 1:
                    timeChart();
                    break;
                case 2:
                    correlationChart();
                    break;
                case 3:
                    histogram();
                    break;
                case 4:
                    correlationMatrix();
                    break;
                case 5:
                    mathematicalExpectation();
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawAxises() {
        context.beginPath();
        context.setStroke(Color.BLACK);
        context.moveTo(yAxis, 30);
        context.lineTo(yAxis, xAxis + 50); //oy
        context.moveTo(10, xAxis);
        context.lineTo(1550, xAxis); //ox
        context.stroke();
        context.closePath();
    }

    public void drawUserTip() {
        try {
            openCSV();

            String[] names = reader.readNext();

            context.beginPath();
            context.setStroke(Color.BLACK);
            context.setFont(Font.font("Arial Bold", 14));
            context.clearRect(1200, 350, 300, 230);

            int x = 1250, y = 370;
            for (int i = 1; i < totalAmountOfObjects + 1; i++) {
                context.setStroke(objectNameColors[i]);
                context.strokeText(i + ". " + names[i].toUpperCase(), x, y);
                y += 20;
                if (i % 11 == 0) {
                    y = 370;
                    x += 140;
                }
            }
            context.closePath();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}