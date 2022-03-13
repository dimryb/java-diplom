package ru.netology.graphics.image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindTextColorSchema implements TextColorSchema{
    @Override
    public char convert(int color) {
        List<Character> schema = new ArrayList<>(Arrays.asList('#', '$', '@', '%', '*', '+', '-', '\''));
        int schemaStep = 256/schema.size();
        int symbolIndex = color/schemaStep;
        char symbol = schema.get(symbolIndex);
        return symbol;
    }
}
