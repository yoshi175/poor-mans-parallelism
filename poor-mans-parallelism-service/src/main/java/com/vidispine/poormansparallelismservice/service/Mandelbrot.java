package com.vidispine.poormansparallelismservice.service;


import org.apache.commons.math.complex.Complex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Mandelbrot {

    private static final Logger log = LoggerFactory.getLogger(Mandelbrot.class);
    private static final int L = 200;

    /**
     * Method that calculate the Mandelbrot image, and store each pixel value in a 2 dimensional array.
     */
    public static short[][] calculateTo2DimensionalArray(double min_c_re, double min_c_im, double max_c_re, double max_c_im, int width, int height, int inf_n) {
        double rSpan = max_c_re - min_c_re;
        double iSpan = max_c_im - min_c_im;
        double rInc = rSpan / width;
        double iInc = iSpan / height;

        double real;
        double imaginary;
        Complex c;
        Complex z;

        short[][] image = new short[width][height];
        for (int i = 1, row = 0; i <= height; i++, row++) {
            for (int j = 1, col = 0; j <= width; j++, col++) {
                real = min_c_re + (rInc * i);
                imaginary = min_c_im + (iInc * j);
                c = new Complex(real, imaginary);
                z = new Complex(0D, 0D);
                image[row][col] = modulus256(
                        iterationsOfZnSquarePlusCWhenConditionIsMet(z, c, 0, inf_n));
            }
        }

        return image;
    }

    /**
     * Method that calculate the Mandelbrot image, and store each pixel values in a binary array.
     */
    public static byte[] calculateToByteArray(double min_c_re, double min_c_im, double max_c_re, double max_c_im, int width, int height, int inf_n) {
        double rSpan = max_c_re - min_c_re;
        double iSpan = max_c_im - min_c_im;
        double rInc = rSpan / width;
        double iInc = iSpan / height;

        double real;
        double imaginary;
        Complex c;
        Complex z;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 1, row = 0; i <= height; i++, row++) {
            for (int j = 1, col = 0; j <= width; j++, col++) {
                real = min_c_re + (rInc * i);
                imaginary = min_c_im + (iInc * j);
                c = new Complex(real, imaginary);
                z = new Complex(0D, 0D);
                short rgb = modulus256(iterationsOfZnSquarePlusCWhenConditionIsMet(z, c, 0, inf_n));
                bufferedImage.setRGB(col, row, new Color(rgb, rgb, rgb).getRGB());
            }
        }

        return toByteArray(bufferedImage);
    }

    private static byte[] toByteArray(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", baos);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return baos.toByteArray();
    }

    private static int iterationsOfZnSquarePlusCWhenConditionIsMet(Complex z, Complex c, int iteration, int inf_n) {
        if (iteration >= inf_n || getVector(z) > L)
            return iteration;
        z = z.multiply(z).add(c);
        return iterationsOfZnSquarePlusCWhenConditionIsMet(z, c, ++iteration, inf_n);
    }

    private static Double getVector(Complex z) {
        return z != null ? Math.sqrt(Math.pow(z.getReal(), 2) + Math.pow(z.getImaginary(), 2)) : null;
    }

    private static short modulus256(int vector) {
        return (short) (vector % 256);
    }

}
