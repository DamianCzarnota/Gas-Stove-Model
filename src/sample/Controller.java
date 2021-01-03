package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {


    @FXML
    public Pane FireLeft;
    public Pane FireRight;
    public Button KnobLeftUp;
    public Button KnobLeftDown;
    public Button KnobOvenProgram;
    public Button KnobOvenTemperature;
    public Button KnobRightUp;
    public Button KnobRightDown;
    public Pane OvenCenter;
    public Pane OvenCenterLight;
    public Label screen;

    private final double limit = 0.7223,lowestTemperature=110,soundLimit=0.005556;
    private final double Min = -180, Max =0;
    private double CurrentRotate, MouseX,KnobRotation;
    private String programs[]={"Tradycyjny","Termoobieg","Rozmrażanie","Grill"};
    int function=0;

    Media ignite,flame;

    MediaPlayer LeftUp,LeftDown,RightUp,RightDown,sparke;
    boolean leftUpFlame=false,leftDownFlame=false,rightDownFlame=false,rightUpFlame=false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ignite = new Media(Paths.get("src\\sample\\Sparking.mp3").toUri().toString());
        flame = new Media(Paths.get("src\\sample\\Flame.mp3").toUri().toString());
        sparke = new MediaPlayer(ignite);
        sparke.setOnEndOfMedia(()->{sparke.stop(); sparke.seek(Duration.ZERO);});
        LeftUp=new MediaPlayer(flame);
        LeftUp.setOnEndOfMedia(()->{ LeftUp.seek(Duration.ZERO); });
        LeftDown=new MediaPlayer(flame);
        LeftDown.setOnEndOfMedia(()->{ LeftDown.seek(Duration.ZERO); });
        RightUp=new MediaPlayer(flame);
        RightUp.setOnEndOfMedia(()->{ RightUp.seek(Duration.ZERO); });
        RightDown=new MediaPlayer(flame);
        RightDown.setOnEndOfMedia(()->{ RightDown.seek(Duration.ZERO); });

        Timer time = new Timer(screen,KnobOvenTemperature);
        time.setDaemon(true);
        time.start();
        MouseX=0;
        KnobRotation=0;
    }

    public void Rotate(MouseEvent mouseEvent) {
        Button b = (Button)mouseEvent.getSource();
        double rotation = CurrentRotate + (mouseEvent.getSceneX() - MouseX);
        if(rotation<Min) rotation=Min;
        if(rotation>Max) rotation=Max;
        b.setRotate(rotation);

        if(b.getId().equals(KnobLeftUp.getId())){
            leftUpFlame = fire(FireLeft,KnobLeftUp,KnobLeftDown,leftUpFlame,LeftUp);
            if(leftUpFlame==true){
                LeftUp.setVolume((-KnobLeftUp.getRotate())*soundLimit);
            }
        }
        if(b.getId().equals(KnobLeftDown.getId())){
            leftDownFlame = fire(FireLeft,KnobLeftDown,KnobLeftUp,leftDownFlame,LeftDown);
            if(leftDownFlame==true){
                LeftDown.setVolume(((-KnobLeftDown.getRotate())*soundLimit));
            }
        }
        if(b.getId().equals(KnobRightDown.getId())){
            rightDownFlame = fire(FireRight,KnobRightDown,KnobRightUp,rightDownFlame,RightDown);
            if(rightDownFlame==true){
                RightDown.setVolume((-KnobRightDown.getRotate())*soundLimit);
            }
        }
        if(b.getId().equals(KnobRightUp.getId())){
            rightUpFlame = fire(FireRight,KnobRightUp,KnobRightDown,rightUpFlame,RightUp);
            if(rightUpFlame==true){
                RightUp.setVolume((-KnobRightUp.getRotate())*soundLimit);
            }
        }


        if(b.getId().equals(KnobOvenProgram.getId())){
            if(KnobOvenTemperature.getRotate()!=0){
                if(-KnobOvenProgram.getRotate()<45){
                    function=0;
                }
                if(-KnobOvenProgram.getRotate()>45&&-KnobOvenProgram.getRotate()<90){
                    function=1;
                }
                if(-KnobOvenProgram.getRotate()>90&&-KnobOvenProgram.getRotate()<135){
                    function=2;
                }
                if(-KnobOvenProgram.getRotate()>135){
                    function=3;
                }
                screen.setText((int)(-KnobOvenTemperature.getRotate()* limit +lowestTemperature)+" °C\n"+programs[function]);
            }
        }
        if(b.getId().equals(KnobOvenTemperature.getId())){
            if(KnobOvenTemperature.getRotate()!=0){
                OvenCenter.setVisible(false);
                OvenCenterLight.setVisible(true);
                screen.setText((int)(-KnobOvenTemperature.getRotate()*limit+lowestTemperature)+" °C \n"+programs[function]);
            }else{
                OvenCenter.setVisible(true);
                OvenCenterLight.setVisible(false);
                LocalDateTime time = LocalDateTime.now();
                if(time.getMinute()<10)
                    screen.setText(time.getHour()+":0"+time.getMinute());
                else
                    screen.setText(time.getHour()+":"+time.getMinute());
            }

        }
    }
    private boolean fire(Pane fire,Button burner,Button oppositeBurner,boolean isOnFire,MediaPlayer sound){
        if(burner.getRotate()!=0){
            fire.setVisible(true);
            if(isOnFire==false){
                isOnFire=true;
                sound.play();
            }
        }else{
            if(oppositeBurner.getRotate()==0)
                fire.setVisible(false);
            isOnFire=false;
            sound.stop();
        }
        return isOnFire;
    }
    public void RotateInit(MouseEvent mouseEvent) {
        Button b = (Button)mouseEvent.getSource();
        if(!(b.getId().equals(KnobOvenProgram.getId())||b.getId().equals(KnobOvenTemperature.getId()))){
            if(b.getRotate()==0) {
                sparke.play();
            }
        }
        CurrentRotate=b.getRotate();
        MouseX= mouseEvent.getSceneX();
    }

    public void GetRotation(MouseEvent mouseEvent) {
        Button b = (Button)mouseEvent.getSource();
        KnobRotation = ((-b.getRotate())*0.5556);
        System.out.println(b.getId());
        System.out.println("Volume: "+(int)KnobRotation + "%");
    }
}
class Timer extends Thread{
    @FXML
    Button temperature;
    Label label;

    Timer(Label l,Button temp){
        label=l;
        temperature=temp;
    }
    public void run(){
        try {
            while (true){
                TimeUnit.SECONDS.sleep(1);
                LocalDateTime time = LocalDateTime.now();
                String print = new String();
                if(time.getMinute()<10)
                    print= time.getHour()+":0"+time.getMinute();
                else
                    print= time.getHour()+":"+time.getMinute();
                if(temperature.getRotate()==0){
                    Platform.runLater(new Runnable() {
                        String toPrint;
                        @Override
                        public void run() {
                            label.setText(toPrint);
                        }
                        private Runnable init(String time){
                            toPrint=time;
                            return this;
                        }
                    }.init(print));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}