package salaryCheck.view;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class StandardStyles {

    public static Background getBackground(StandardBackgrounds standardBackground){

        Background background = new Background(new BackgroundFill(Color.web("#FFFFFF", 0.0), null, null));

        switch (standardBackground){
            case GREEN -> background = new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null));
            case RED -> background = new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null));
            case TRANSIENT -> background = new Background(new BackgroundFill(Color.web("#FFFFFF", 0.0), null, null));
        }
        return background;
    }

    public static Tooltip getTooltip(String message){
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(new Duration(150));
        tooltip.setFont(Font.font(14));

        return tooltip;
    }

    public enum StandardBackgrounds{
        GREEN,
        RED,
        TRANSIENT
    }
}
