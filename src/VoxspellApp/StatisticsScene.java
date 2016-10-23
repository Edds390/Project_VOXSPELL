package VoxspellApp;

import VoxspellApp.Popups.HelpWindow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import models.Level;
import models.Status;
import models.Word;
import models.WordModel;
//import models.GraphFactory;


/**
 * Created by edson on 15/09/16.
 * This class is responsible for creating all the statistics that is relevant to showing the
 * statistics scene. It returns this a sa layout so that it can be set onto a scene where this class is
 * called.
 * The stats are shown as a pie graph showing the overall accuracy as well as bar graphs showing accuracies
 * specific for a word.
 */

public class StatisticsScene {
    private Scene _statsScene;
    private BorderPane _bgLayout;
    private WordModel _model;
    private ScrollPane _menu;
    private ScrollPane _graphSceneLayout;
    private int _bargraphHeight;

    private Button _helpButton;


    private int _level;

    //STYLE
    private final Glow graphGlow = new Glow(.7);


    public StatisticsScene(WordModel model){

        _model = model;//set the data structure to be used.

    }

    /**
     * returns a borderlayout which can be set onto a scene or another layout if necessary.
     * The borderlayout contains category hyperlinks and a barchart+piechart showing the
     * statistics for a given word model.
     *
     * @return borderlayout with all the relevant stats nodes.
     */
    public Node createScene(){

        _helpButton = new Button("?");
        _helpButton.setStyle("-fx-font: bold 30 latoheavy; -fx-base: #1db361; " +
                "-fx-background-radius: 40 40 40 40; -fx-text-fill:  white; -fx-border: 20px; -fx-border-color: white; -fx-border-radius: 40");
        _helpButton.setOnAction(e->{
            HelpWindow help = new HelpWindow(2);
            help.display();
        });
        _model.updateStatistics();//updates the statistics of the WordModel

        VBox optionLayout = new VBox(20);
        optionLayout.setPrefWidth(200);//set menu width
        optionLayout.setPadding(new Insets(20,20,20,20));
        //sets the hyperlink for overview
        Hyperlink link = new Hyperlink("Overview");
        link.setOnAction(e->{//set graphScene to the overall statistics setting
            _bgLayout.setCenter(_graphSceneLayout);
        });
        optionLayout.getChildren().add(link);
        //sets hyperlink for all levels of the particular spelling list of the model
        //iterate through levels
        for(int i = 1; i<_model.getTotalLevels()+1; i++){
            final int level = i;
            link = new Hyperlink("Level "+(i));
            link.setOnAction(e-> {//change the graphScene
                VBox graphLayout = new VBox(20);
                graphLayout.setPadding(new Insets(10));
                graphLayout.setAlignment(Pos.CENTER);
                //if the user has not attmepted this level yet, then deny access to it
                //and replace with a picture of a cat
                if (!_model.isStatsAccessible(level-1)) {
                    VBox deniedbox = new VBox(10);
                    deniedbox.setAlignment(Pos.CENTER);
                    deniedbox.setPadding(new Insets(20,20,20,80));
                    Image oopsImage = new Image("MediaResources/oops.png", 400, 400, false, true);
                    ImageView oopsView = new ImageView(oopsImage);
                    Label accessDeniedLabel = new Label("You have not unlocked this level yet.");
                    accessDeniedLabel.getStyleClass().add("statslabel");
                    Label accessDeniedLabel1 = new Label("Play a new game in the selected level and come back!");
                    accessDeniedLabel1.getStyleClass().add("statslabel");
                    deniedbox.getChildren().addAll(oopsView,accessDeniedLabel,accessDeniedLabel1);
                    graphLayout.getChildren().add(deniedbox);
                } else {
                    //if the user does have access to the level, then create a pie chart and bar chart for that
                    //particular level (based on the hyperlink's caption).
                    PieChart levelPie = createPie("Level " + (level) + " Accuracy", level-1);
                    BarChart<Number, String> levelBar = createBar("Word Statistics", level-1);
                    levelBar.getStylesheets().add("VoxspellApp/LayoutStyles");
                    levelBar.getStyleClass().add(".bargraph");
                    double[] percentages = getPercentage(levelPie.getData());

                    //show the accuracy of the level as a percentage
                    Label accuracy = new Label(String.format("%.2f", percentages[2])+"%");
                    accuracy.setStyle("-fx-font: bold 28 arial ;-fx-text-fill: white");
                    accuracy.setAlignment(Pos.CENTER);
                    graphLayout.getChildren().addAll(accuracy, levelPie);
                    drawPieLabels(levelPie, graphLayout);
                    graphLayout.getChildren().add(levelBar);
                }
                ScrollPane graphSceneLayout = new ScrollPane(graphLayout);//set the scrollpane with a vbox consisting of pie and bar
                graphSceneLayout.setStyle("-fx-background-color: rgba(251, 176, 64, 0.71)");
                _bgLayout.setCenter(graphSceneLayout);

            });
            optionLayout.getChildren().add(link);//add to menu vbox
        }
        optionLayout.setAlignment(Pos.CENTER);//set nodes to center of vbox
        _menu = new ScrollPane(optionLayout);
        _menu.setFitToWidth(true);//expand scrollpane x-wise

        VBox graphLayout = new VBox(15);

        //set the help button
        HBox helpBox = new HBox();
        helpBox.setAlignment(Pos.CENTER_RIGHT);
        helpBox.getChildren().add(_helpButton);
        graphLayout.getChildren().add(_helpButton);

        graphLayout.setAlignment(Pos.CENTER);
        graphLayout.setPadding(new Insets(10,10,10,10));

        //overall pie chart showing accuracy across all levels
        //set this as the overall hyperlink view.
        PieChart pie = createOverallPie();
        pie.setStyle("-fx-tick-label-fill: white");
        double[] percentages = getPercentage(pie.getData());
        Label accLabel = new Label(String.format("%.2f", percentages[2])+"%");
        accLabel.setStyle("-fx-font: bold 28 arial ;-fx-text-fill: white");
        graphLayout.getChildren().addAll(accLabel, pie);
        drawPieLabels(pie, graphLayout);
        _graphSceneLayout = new ScrollPane(graphLayout);
        _graphSceneLayout.setStyle("-fx-background-color: rgba(251, 176, 64, 0.7);");

        _bgLayout = new BorderPane();
        _bgLayout.setLeft(_menu);
        _bgLayout.setCenter(_graphSceneLayout);
        _bgLayout.getStylesheets().add("VoxspellApp/LayoutStyles");


        return _bgLayout;
    }

    /**
     * helper function to create a piechart based on the model
     * @param title title of the piechart
     * @param level level of the piechart based on an int
     * @return piechart showing accuracies as percentages
     */
    private PieChart createPie(String title, int level){
        int[] _levelData = _model.findAccuracy(level);//int array size 3 level -1 b/c model accuracy starts at 0
        double[] levelData = new double[3];//data showing all the accuracies for each master,fault,failed
        double total = 0;
        //find frequencies across all datas
        for(int i = 0; i < levelData.length; i++){
            total += _levelData[i];
        }
        //set the accuracies
        for(int i = 0; i < levelData.length; i++){
            int value = _levelData[i];
            if (value != 0){
                levelData[i] = (value/total)*100;
            }
        }
        //set the data fields for the piechart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Failed", levelData[0]),
                new PieChart.Data("Faulted", levelData[1]),
                new PieChart.Data("Mastered", levelData[2])
        );
        final PieChart pieGraph = new PieChart(pieData);
        pieGraph.setTitle(title);
        return pieGraph;
    }

    /**
     * separate helper function to only create the pie chart for the overall accuracies
     * across all levels
     * @return piechart showing accuracy across all levels
     */
    private PieChart createOverallPie(){

        int[] _levelData = _model.getOverall();//int array size 3 show accuracies across all levels
        double[] levelData = new double[3];
        double total = 0;
        //populate accuracy array
        for(int i = 0; i < levelData.length; i++){
            total += _levelData[i];
        }
        //calcualte accuracies
        for(int i = 0; i < levelData.length; i++){
            int value = _levelData[i];
            if (value != 0){
                levelData[i] = (value/total)*100;
            }
        }
        //populate piechart based on accuracy array
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Failed", levelData[0]),
                new PieChart.Data("Faulted", levelData[1]),
                new PieChart.Data("Mastered", levelData[2])
        );
        final PieChart pieGraph = new PieChart(pieData);

        pieGraph.setTitle("Overall Accuracy");

        pieGraph.setStyle(".bargraph; -fx-text-fill: white");
        return pieGraph;
    }

    /**
     * helper function to create the barchart
     * @param title title of barchart
     * @param level level which the bar chart represents
     * @return barchart showing accuracies for each level
     */
    private BarChart<Number, String> createBar(String title, int level){
        Level currentLevel = _model.getLevel(level);
        currentLevel.sort();//sort words alphabetically
        int bargraphHeight=150;

        //set the xAxis node
        final NumberAxis xAxis = new NumberAxis();
        //set the yAxis node
        final CategoryAxis yAxis = new CategoryAxis();
        //populate bar chart with x and y axis nodes
        final BarChart<Number, String> barGraph = new BarChart<Number, String>(xAxis, yAxis);
        barGraph.setTitle(title);
        xAxis.setLabel("Frequency");
        yAxis.setLabel("Word");
        yAxis.tickLabelFontProperty().set(Font.font(16));//set the y axis (words) font size
        yAxis.setStyle("-fx-tick-label-fill: white");
        xAxis.setStyle("-fx-text-label-fill: white");

        //create series data for each of faield, faulted, and mastered to create a composite bar graph
        XYChart.Series failSeries = new XYChart.Series();
        failSeries.setName("Failed");

        XYChart.Series faultSeries = new XYChart.Series();
        faultSeries.setName("Faulted");

        XYChart.Series masterSeries = new XYChart.Series();
        masterSeries.setName("Mastered");

        //loop through each word in the current level
        for (Word word: currentLevel){
            if (word.getStatus() != Status.Unseen){
                bargraphHeight+=80;//incremement height for each word addition
                //add data poitns with first param being word count and second being word string form
                final XYChart.Data<Number, String> failData = new XYChart.Data(word.getStat(0), word.getWord());
                drawBarLabels(failData);
                final XYChart.Data<Number, String> faultData = new XYChart.Data(word.getStat(1), word.getWord());
                drawBarLabels(faultData);
                final XYChart.Data<Number, String> masterData = new XYChart.Data(word.getStat(2), word.getWord());
                drawBarLabels(masterData);
                //add the data of the series to the XYChart.
                failSeries.getData().add(failData);
                faultSeries.getData().add(faultData);
                masterSeries.getData().add(masterData);
            }
        }


        barGraph.getData().addAll(failSeries, faultSeries, masterSeries);
        barGraph.setMinHeight(bargraphHeight);
        barGraph.setCategoryGap(25);
        //barGraph.setStyle("-fx-font-size: 18px");//set font size of axis
        barGraph.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-fill: transparent");

        return barGraph;
    }

    /**
     * set up the labelling functionality where user can click on the pie graph to see accuracy as percentage
     * @param pieGraph piegraph showing accuracies
     * @param background layout for piegraph to be set
     */
    private void drawPieLabels(PieChart pieGraph, VBox background){
        final Label percentage = new Label("");
        percentage.setStyle("-fx-font: 22 arial;");
        for (final PieChart.Data data : pieGraph.getData()){

            //on mouse click, show the data as percentage
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    percentage.setTranslateX(event.getX());
                    percentage.setTranslateY(event.getY() - 240);
                    percentage.setText(String.format("%.2f", data.getPieValue()) + "%");
                }
            });
        }
        background.getChildren().add(percentage);
    }

    /**
     * helper functio nto get the percentage data for each of the master fail fault
     * and return as an array
     * @param primitive list of primitive data
     * @return double array showing percentages of eahc of the accuracies
     */
    private double[] getPercentage(ObservableList<PieChart.Data> primitive){
        double total=0;
        double[] percentageList = new double[3];
        double[] primList = new double[3];
        int j = 0;
        //loop through the values to find the total of master fault fail
        for (PieChart.Data element:primitive){
            total+=element.getPieValue();
            primList[j] = element.getPieValue();
            j++;
        }
        //using the total, calcualte the percentage of the master accuracy.
        for (int i = 0; i<percentageList.length; i++){
            if (primList[i] != 0){
                percentageList[i] = (primList[i]/total)*100;
            }
        }
        return  percentageList;
    }

    /**
     * helper function to draw the labels of the barchart showing the number of words associated with that bar
     * @param data the number of words frequency for that particular bar
     */
    private void drawBarLabels(XYChart.Data<Number, String> data) {
        data.nodeProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, final Node newValue) {
                if (newValue != null) {
                    final Node dataNode = data.getNode();
                    final Text text = new Text(data.getXValue()+"");
                    text.setStyle("-fx-text-fill: white");

                    dataNode.parentProperty().addListener(new ChangeListener<Parent>() {
                        @Override
                        public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                            Group parentGroup = (Group) newValue;
                            parentGroup.getChildren().add(text);

                        }
                    });
                    dataNode.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
                        @Override
                        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                            //set position of count label on bar graph
                            text.setLayoutX(Math.round(newValue.getMinX()+newValue.getWidth()/2-text.prefWidth(-1)/2)*2+15);//set x position
                            text.setLayoutY(Math.round(newValue.getMinY()-text.prefHeight(-1)*0.5)+20);//set y position
                        }
                    });
                }
            }
        });




    }
}
