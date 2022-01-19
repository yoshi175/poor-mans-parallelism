package com.example.poormansparallelismclient;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MandelbrotImage {

    public static BufferedImage getImage(double min_c_re, double min_c_im, double max_c_re, double max_c_im, int width, int height, int inf_n, int serverPort) {
        String url = String.format("http://localhost:%s/mandelbrot/%s/%s/%s/%s/%s/%s/%s",
                serverPort, min_c_re, min_c_im, max_c_re, max_c_im, width, height, inf_n);
        BufferedImage bufferedImage = null;
        try {
            HttpResponse<InputStream> apiResponse = Unirest.get(url).asBinary();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(apiResponse.getBody());
            bufferedImage = ImageIO.read(bufferedInputStream);
        } catch (UnirestException | IOException ignore) {

        }
        return bufferedImage;
    }

}
