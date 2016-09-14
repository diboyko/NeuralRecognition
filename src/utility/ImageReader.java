package utility;

import exceptions.ImageReadingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Created by Me on 15.04.2016.
 */
public class ImageReader {

    public static double[] getInputs(String inputImagePath) throws ImageReadingException {
        int blue, green, red, alpha;
        double result;
        BufferedImage img;

        File fileToRead = new File(inputImagePath);
        try {
            if (!fileToRead.getPath().endsWith(".png")) {
                throw new ImageReadingException("Image reading error! File is not an image or wrong image path!");
            }
            img = ImageIO.read(fileToRead);
        } catch (Exception e) {
            throw new ImageReadingException("Image reading error! File is not an image or wrong image path!");
        }


        if (img.getWidth() != 32 || img.getHeight() != 32)
            throw new ImageReadingException("Wrong image size. Should be 32x32!");

        int[] inputsRGB = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        double[] inputs = new double[inputsRGB.length];
        for (int i = 0; i < inputs.length; i++) {
            int color = inputsRGB[i];
            blue = (color & 0xff);
            green = (color & 0xff00) >> 8;
            red = (color & 0xff0000) >> 16;
            alpha = (color & 0xff000000) >>> 24;
            result = (double) (blue + green + red + (alpha)) / 1020;
            inputs[i] = result > 0.95 ? 0 : result;
        }

        return inputs;
    }

}
