package mytest.screen;

import mytest.cmd.OperatorClient;
import mytest.constant.Constants;
import mytest.utils.MyUtils;
import mytest.utils.PictureUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author ylq
 * @date 2020/3/30
 */
public class ScreenShotClient {

    private static final Logger logger = LoggerFactory.getLogger(ScreenShotClient.class);

    public ScreenShotClient() throws Exception {
        logger.info("屏幕监控客户端开始监控...");
        startService();
    }

    private void startService() throws Exception {
        while (true) {
            BufferedImage bufferedImage = PictureUtils.screenshotNow();
            Jedis jedis = MyUtils.getJedis();
            byte[] bytes = compressedImageAndGetByteArray(bufferedImage, Constants.LOW_QUALITY);
            String encode = new BASE64Encoder().encode(bytes).replaceAll("\n", "").replaceAll("\r", "");
            jedis.lpush("frame", encode);
            TimeUnit.MILLISECONDS.sleep(200);
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

}
