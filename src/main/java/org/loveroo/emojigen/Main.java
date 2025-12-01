package org.loveroo.emojigen;

public class Main {

    private static final int UNICODE_START = 0xE000;

    public static void main(String[] args) {
        var font = new Font();
        
        var armor = new BitmapProvider();
        armor.imagePath("");

        font.addProvider(armor);
    }
}