package org.loveroo.emojigen;

public class Main {

    public static void main(String[] args) {
        final var font = new Font();

        final var effects = new BitmapProvider("effects");
        
        effects.imagePath("assets/effect");
        
        effects.height(24);
        effects.ascent(16);

        final var bars = new BitmapProvider("bars");

        bars.imagePath("assets/bar");

        bars.height(24);
        bars.ascent(16);
        
        final var armor = new BitmapProvider("armor");
        final var hearts = new BitmapProvider("hearts");

        armor.imagePath("assets/armor");
        armor.height(9);

        hearts.imagePath("assets/hp");
        hearts.height(8);

        font.addProvider(effects);
        font.addProvider(bars);

        font.addProvider(armor);
        font.addProvider(hearts);

        final var writer = new PackWriter();
        writer.build("test", font);
    }
}