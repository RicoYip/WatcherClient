package test;

import mytest.constant.Constants;
import mytest.utils.MyUtils;
import mytest.utils.PictureUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class ScreenShotTest {

    private static long syn = 0;

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Robot robot = new Robot();
        Rectangle rectangle = new Rectangle(dimension.width, dimension.height);
        String dirPath = (String) MyUtils.getProperties().get("tmp");
        File tmpFileParent = new File(dirPath);
        if (!tmpFileParent.exists()) tmpFileParent.mkdirs();
        while (true) {
            BufferedImage screenCapture = robot.createScreenCapture(rectangle);
            File file = new File(tmpFileParent, "tmp" + (++syn) + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            ImageIO.write(screenCapture, "jpeg", outputStream);
            outputStream.close();
            TimeUnit.MILLISECONDS.sleep(42);
            long end = System.currentTimeMillis();
//            if (end - start > 1000) {
            System.out.println("end" + (end - start));
//                return;
//            }
        }
    }


    public byte[] compressedImageAndGetByteArray(BufferedImage image, float quality) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(image).scale(1f).outputFormat("jpg").outputQuality(quality).toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCompressedImageAndGetByteArray() throws Exception {
        while (true) {
            BufferedImage bufferedImage = PictureUtils.screenshotNow();
            Jedis jedis = MyUtils.getJedis();
            byte[] bytes = compressedImageAndGetByteArray(bufferedImage, Constants.LOW_QUALITY);
            String encode = new BASE64Encoder().encode(bytes).replaceAll("\n", "").replaceAll("\r", "");
            jedis.lpush("frame", encode);
            TimeUnit.MILLISECONDS.sleep(300);
        }
    }


    @Test
    public void testReadFromRedis() throws IOException {
        Jedis jedis = MyUtils.getJedis();
        String frame = jedis.rpop("frame");
        File file = new File("d:/a.txt");
//        FileOutputStream outputStream = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("data:image/jpg;base64,"+frame);
    }

}
