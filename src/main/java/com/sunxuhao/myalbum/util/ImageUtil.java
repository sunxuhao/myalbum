package com.sunxuhao.myalbum.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

    public static BufferedImage change2jpg(Image i) {
        try {
            PixelGrabber pg = new PixelGrabber(i, 0, 0, -1, -1, true);
            pg.grabPixels();
            int width = pg.getWidth(), height = pg.getHeight();
            final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
            final ColorModel RGB_OPAQUE = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);
            DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            BufferedImage img = new BufferedImage(RGB_OPAQUE, raster, false, null);
            return img;
        } catch (InterruptedException | IllegalArgumentException e) {
            return null;
        }
    }

    public static BufferedImage change2jpg(File f) {
        Image i = Toolkit.getDefaultToolkit().createImage(f.getAbsolutePath());
        return change2jpg(i);
    }

    public static BufferedImage change2jpg(byte[] data) {
        Image i = Toolkit.getDefaultToolkit().createImage(data);
        return change2jpg(i);
    }

    public static void resizeImage(File srcFile, int width, int height, File destFile) {
        try {
            destFile.getParentFile().mkdirs();
            Image i = ImageIO.read(srcFile);
            resizeImage(i, height, width, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resizeImage(Image i, int width, int height, File destFile) {
        try {
            i = resizeImage(i, width, height);
            ImageIO.write((RenderedImage) i, "jpg", destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image resizeImage(Image srcImage, int width, int height) {
        try {

            BufferedImage buffImg = null;
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            buffImg.getGraphics().drawImage(srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            return buffImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
