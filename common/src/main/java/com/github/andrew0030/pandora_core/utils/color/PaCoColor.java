package com.github.andrew0030.pandora_core.utils.color;

import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Range;

public class PaCoColor extends FastColor.ARGB32 {

    public static int color (@Range(from = 0, to = 255) int alpha, @Range(from = 0, to = 255) int red, @Range(from = 0, to = 255) int green, @Range(from = 0, to = 255) int blue) {
        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    public static int color(@Range(from = 0, to = 255) int red, @Range(from = 0, to = 255) int green, @Range(from = 0, to = 255) int blue) {
        return color(255, red, green, blue);
    }

    /*(thanks lorenzo): https://www.rapidtables.com/convert/color/hsv-to-rgb.html*/
    /**
     * Returns a Color based on the given Hue.
     * @param hue The Hue of this Color.
     * @param saturation The Saturation of this Color.
     * @param value The Brightness of this Color.
     * @return The created Color.
     */
    public static int colorFromHSV(@Range(from = 0, to = 360) float hue, @Range(from = 0, to = 1) float saturation, @Range(from = 0, to = 1) float value) {
        hue %= 360;
        if (hue < 0) hue += 360;

        float c = value * saturation;
        float x = c * (1 - Math.abs((hue / 60) % 2 - 1));

        float m = value - c;

        float rPrime = 0;
        float gPrime = 0;
        float bPrime = 0;

        float num = (hue / 60f);
        if ((0 <= num) && (num < 2)) {
            rPrime = num < 1 ? c : x;
            gPrime = num < 1 ? x : c;
        } else if ((2 <= num) && (num < 4)) {
            gPrime = num < 3 ? c : x;
            bPrime = num < 3 ? x : c;
        } else if ((4 <= num) && (num < 6)) {
            rPrime = num < 5 ? x : c;
            bPrime = num < 5 ? c : x;
        }

        float r = (rPrime + m) * 255;
        float g = (gPrime + m) * 255;
        float b = (bPrime + m) * 255;
        return color((int) r, (int) g, (int) b);
    }

    public static int add(int color1, int color2) {
        int alpha = Math.min(alpha(color1) + alpha(color2), 255);
        int red = Math.min(red(color1) + red(color2), 255);
        int green = Math.min(green(color1) + green(color2), 255);
        int blue = Math.min(blue(color1) + blue(color2), 255);
        return color(alpha, red, green, blue);
    }

    public static int average(int color1, int color2) {
        int alpha = (alpha(color1) + alpha(color2)) / 2;
        int red = (red(color1) + red(color2)) / 2;
        int green = (green(color1) + green(color2)) / 2;
        int blue = (blue(color1) + blue(color2)) / 2;
        return color(alpha, red, green, blue);
    }

    public static int alphaBlend(int color1, int color2) {
        float alpha1 = alpha(color1) / 255.0f;
        float alpha2 = alpha(color2) / 255.0f;
        float finalAlpha = alpha1 + alpha2 * (1 - alpha1);

        int red = (int)((red(color1) * alpha1 + red(color2) * alpha2 * (1 - alpha1)) / finalAlpha);
        int green = (int)((green(color1) * alpha1 + green(color2) * alpha2 * (1 - alpha1)) / finalAlpha);
        int blue = (int)((blue(color1) * alpha1 + blue(color2) * alpha2 * (1 - alpha1)) / finalAlpha);

        return color((int)(finalAlpha * 255), red, green, blue);
    }
}