package VoxspellApp.Popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by edson on 23/10/16.
 */
public class HelpWindow {
    private Stage window = new Stage();

    //Layouts
    private BorderPane _mainLayout;
    private VBox _screenLayout;
    private HBox _buttonLayout;

    //Buttons
    private Button _nextButton;
    private Button _prevButton;
    private Button _backButton;

    private int helpWindow;
    private int imageNumber=0;
    private int position=0;

    //ImageView Arraylist
    private ImageView[] _imageViewArray;

    public HelpWindow(int helpNum){
        helpWindow = helpNum;
        _nextButton=createButtons("NEXT ▶");
        _backButton=createButtons("GOT IT!");
        _prevButton=createButtons("◀ PREVIOUS");
        _prevButton.setDisable(true);



        setupImageList();
        setupVBox();
        setupEventHandlers();
        if (imageNumber==0){
            _nextButton.setDisable(true);
        }

    }

    private void setupImageList(){
        if(helpWindow==0){
            imageNumber=5;
            _imageViewArray= new ImageView[]{makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png")};


        } else if (helpWindow== 1){
            imageNumber=5;
            _imageViewArray = new ImageView[]{makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png")};
        } else if (helpWindow == 2){
            imageNumber=4;
            _imageViewArray = new ImageView[]{makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png")};
        } else if (helpWindow == 3) {
            imageNumber=5;
            _imageViewArray = new ImageView[]{makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png")};
        } else if (helpWindow == 4){
            imageNumber=2;
            _imageViewArray = new ImageView[]{makeImage("MediaResources/catplaysflute.png"),
            makeImage("MediaResources/catplaysflute.png")};
        }
    }

    private ImageView makeImage(String address){
        ImageView imv = new ImageView();
        Image img = new Image(address, 250, 250, false, true);
        imv.setImage(img);
        return imv;
    }

    private void setupVBox(){
        _mainLayout = new BorderPane();
        _screenLayout = new VBox();
        _screenLayout.setAlignment(Pos.CENTER);
        _screenLayout.setPadding(new Insets(5));
        _screenLayout.getChildren().add(_imageViewArray[0]);

        _buttonLayout = new HBox(10);
        _buttonLayout.setAlignment(Pos.CENTER);
        _buttonLayout.setPadding(new Insets(5));
        _buttonLayout.getChildren().addAll(_prevButton,_backButton,_nextButton);
        _mainLayout.setTop(_screenLayout);
        _mainLayout.setBottom(_buttonLayout);
    }

    private void refresh(){
        _screenLayout = new VBox();
        _screenLayout.setAlignment(Pos.CENTER);
        _screenLayout.setPadding(new Insets(5));
        _screenLayout.getChildren().add(_imageViewArray[position]);
        _mainLayout.setTop(_screenLayout);

    }

    private Button createButtons(String caption){
        Button newButton = new Button(caption);
        newButton.setPrefWidth(180);
        newButton.setPrefHeight(50);

        if (caption.equals("GOT IT!")){
            newButton.setStyle("-fx-font: bold 20 arial; -fx-base: #1db361; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");;
        } else {
            newButton.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");
        }
        return newButton;
    }

    private void setupEventHandlers(){
        _nextButton.setOnAction(e->{
            position++;
            if(position==_imageViewArray.length-1){
                _nextButton.setDisable(true);
            }
            if(position>0){
                _prevButton.setDisable(false);
            }
            refresh();


        });
        _prevButton.setOnAction(e->{
            position--;
            if(position==0){
                _prevButton.setDisable(true);
            }
            if(position<_imageViewArray.length-1){
                _nextButton.setDisable(false);
            }
            refresh();

        });
        _backButton.setOnAction(e->{
            window.close();
        });
    }

    public void display(){
        window.initModality(Modality.APPLICATION_MODAL);//modality for suppressing main window
        window.setTitle("New Spelling List");
        window.setMinWidth(500);
        window.setResizable(false);
        _mainLayout.setStyle("-fx-base: #1d194b;");
        Scene scene = new Scene(_mainLayout);

        window.setScene(scene);
        window.showAndWait();
    }

}
