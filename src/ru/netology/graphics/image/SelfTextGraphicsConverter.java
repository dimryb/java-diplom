package ru.netology.graphics.image;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class SelfTextGraphicsConverter implements TextGraphicsConverter {
    private int maxWidth = 0;
    private int maxHeight = 0;
    private double maxRatio = 0;
    private TextColorSchema schema = new WindTextColorSchema();

    /**
     * Рассчитывает на сколько нужно поделить измерение чтобы не превышать максимум
     * @param dimension - значение измерение
     * @param maxDimension - максимальное значение измерения
     * @return делитель измерения
     */
    private double calcDividerDimension(int dimension, int maxDimension){
        double divider = 1;
        if (maxDimension > 0){
            if (dimension > maxDimension) {
                divider = (double) dimension / maxDimension;
            }
        }
        return divider;
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        // Вот так просто мы скачаем картинку из интернета :)
        BufferedImage img = ImageIO.read(new URL(url));
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (maxRatio > 0){
            double ratio = (imgWidth > imgHeight) ? (double)imgWidth/imgHeight : (double) imgHeight/imgWidth;
            if (ratio > maxRatio){
                throw new BadImageSizeException(ratio, maxRatio);
            }
        }

        double dividerWidth = calcDividerDimension(imgWidth, maxWidth);
        double dividerHeight = calcDividerDimension(imgHeight, maxHeight);
        double divider = (dividerWidth > dividerHeight) ? dividerWidth : dividerHeight;
        int newWidth = (int) (imgWidth / divider);
        int newHeight = (int) (imgHeight / divider);

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();

        StringBuilder textGraphicBuilder = new StringBuilder();
        for (int h = 0; h < newHeight; h++){
            for (int w = 0; w < newWidth; w++){
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                textGraphicBuilder.append(c);//запоминаем символ c
                textGraphicBuilder.append(c);
            }
            textGraphicBuilder.append("\n");
        }
        return textGraphicBuilder.toString(); // Возвращаем собранный текст.
    }

    @Override
    public void setMaxWidth(int width) {
        maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
