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
import javafx.scene.image.ImageView;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
public class Controller implements Initializable {
    @FXML
    public ImageView FireLeftUpper;
    public ImageView FireLeftLower;
    public ImageView FireRightUpper;
    public ImageView FireRightLower;
    public Button KnobLeftUp;
    public Button KnobLeftDown;
    public Button KnobOvenProgram;
    public Button KnobOvenTemperature;
    public Button KnobRightUp;
    public Button KnobRightDown;
    public Pane OvenCenter;
    public Pane OvenCenterLight;
    public Label screen;

    private final double LIMIT = 0.7223, LOWEST_TEMPERATURE =110;
    private final double MIN = -180, MAX =0;
    private double CurrentRotate, MouseX;
    private final String PROGRAMS[]={"Tradycyjny","Termoobieg","Rozmrażanie","Grill"};
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
    }
    public void Rotate(MouseEvent mouseEvent) {
        Button b = (Button)mouseEvent.getSource();
        double rotation = CurrentRotate + (mouseEvent.getSceneX() - MouseX);
        if(rotation< MIN) rotation= MIN;
        if(rotation> MAX) rotation= MAX;
        b.setRotate(rotation);
        if(b.getId().equals(KnobLeftUp.getId())){
            leftUpFlame = fire(FireLeftUpper,KnobLeftUp,leftUpFlame,LeftUp);
        }
        if(b.getId().equals(KnobLeftDown.getId())){
            leftDownFlame = fire(FireLeftLower,KnobLeftDown,leftDownFlame,LeftDown);
        }
        if(b.getId().equals(KnobRightDown.getId())){
            rightDownFlame = fire(FireRightLower,KnobRightDown,rightDownFlame,RightDown);
        }
        if(b.getId().equals(KnobRightUp.getId())){
            rightUpFlame = fire(FireRightUpper,KnobRightUp,rightUpFlame,RightUp);
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
                screen.setText((int)(-KnobOvenTemperature.getRotate()* LIMIT + LOWEST_TEMPERATURE)+" °C\n"+ PROGRAMS[function]);
            }
        }
        if(b.getId().equals(KnobOvenTemperature.getId())){
            if(KnobOvenTemperature.getRotate()!=0){
                OvenCenter.setVisible(false);
                OvenCenterLight.setVisible(true);
                screen.setText((int)(-KnobOvenTemperature.getRotate()* LIMIT + LOWEST_TEMPERATURE)+" °C \n"+ PROGRAMS[function]);
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
    private boolean fire(ImageView fire, Button burner,boolean isOnFire, MediaPlayer sound){
        if(burner.getRotate()!=0){
            fire.setVisible(true);
            if(isOnFire==false){
                isOnFire=true;
                sound.play();
                sparke.play();
            }
            if(-burner.getRotate()<=90) {
                fire.setY(0.5*burner.getRotate()+39);
                fire.setFitHeight(-burner.getRotate() + (burner.getRotate() / 2));
            }
            else {
                fire.setY((-burner.getRotate()*(21.0/90.0))-27);
                fire.setFitHeight((burner.getRotate())*(23.0/90.0) + 68);
            }
            if(-burner.getRotate()<=90)
                sound.setVolume((-burner.getRotate())*(1.0/90.0));
            else
                sound.setVolume(((-burner.getRotate())*(-0.75/90.0))+1.75);
        }else{
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
            while(true){
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
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}