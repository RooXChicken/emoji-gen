package org.loveroo.emojigen;

import org.loveroo.emojigen.data.Character;
import org.loveroo.emojigen.providers.BitmapProvider;
import org.loveroo.emojigen.providers.SpaceProvider;
import org.loveroo.emojigen.providers.SpaceProvider.Space;

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
        armor.imagePath("assets/armor");
        armor.height(9);

        final var hearts = new BitmapProvider("hearts");
        hearts.imagePath("assets/hp");
        hearts.height(8);

        final var spacing = new SpaceProvider();
        spacing.addSpace(new Space(new Character(0xDFFF), 1));

        final var spacingTexture = new BitmapProvider("spacing");
        spacingTexture.imagePath("assets/spacing");
                
        font.addProvider(effects);
        font.addProvider(bars);
        
        font.addProvider(armor);
        font.addProvider(hearts);
        
        font.addProvider(spacingTexture);
        font.addProvider(spacing);

        final var writer = new PackWriter(
            "test",
            "§5§lInfuse SMP Pack\n§r§8[ by §4Arcn §r§8& §droo§r §8]"
        );
        
        writer.addFont(font);
        writer.build();
    }
}