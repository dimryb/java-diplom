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

        // Если конвертеру выставили максимально допустимые ширину и/или высоту,
        // вам надо по ним и по текущим высоте и ширине вычислить новые высоту
        // и ширину.
        // Соблюдение пропорций означает, что вы должны уменьшать ширину и высоту должны
        // в одинаковое количество раз.
        // Пример 1: макс. допустимые 100x100, а картинка 500x200. Новый размер
        // будет 100x40 (в 5 раз меньше).
        // Пример 2: макс. допустимые 100x30, а картинка 150x15. Новый размер
        // будет 100x10 (в 1.5 раза меньше).
        // Подумайте, какими действиями можно вычислить новые размеры.
        // Не получается? Спросите вашего руководителя по курсовой, поможем!
        double dividerWidth = calcDividerDimension(imgWidth, maxWidth);
        double dividerHeight = calcDividerDimension(imgHeight, maxHeight);
        double divider = (dividerWidth > dividerHeight) ? dividerWidth : dividerHeight;
        int newWidth = (int) (imgWidth / divider);
        int newHeight = (int) (imgHeight / divider);

        // Теперь нам надо попросить картинку изменить свои размеры на новые.
        // Последний параметр означает, что мы просим картинку плавно сузиться
        // на новые размеры. В результате мы получаем ссылку на новую картинку, которая
        // представляет собой суженную старую.
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // Теперь сделаем её чёрно-белой. Для этого поступим так:
        // Создадим новую пустую картинку нужных размеров, заранее указав последним
        // параметром чёрно-белую цветовую палитру:
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        // Попросим у этой картинки инструмент для рисования на ней:
        Graphics2D graphics = bwImg.createGraphics();
        // А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженной картинки:
        graphics.drawImage(scaledImage, 0, 0, null);

        // Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.
        // Вы можете отслеживать каждый из этапов, просто в любом удобном для
        // вас моменте сохранив промежуточную картинку в файл через:
        // ImageIO.write(imageObject, "png", new File("out.png"));
        // После вызова этой инструкции у вас в проекте появится файл картинки out.png

        // Теперь давайте пройдёмся по пикселям нашего изображения.
        // Если для рисования мы просили у картинки .createGraphics(),
        // то для прохода по пикселям нам нужен будет этот инструмент:
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
