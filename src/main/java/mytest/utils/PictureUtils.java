package mytest.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PictureUtils {

    private static Robot robot;
    private final static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private final static Rectangle rectangle = new Rectangle(dimension.width, dimension.height);

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage screenshotNow(){
        return robot.createScreenCapture(rectangle);
    }

}
