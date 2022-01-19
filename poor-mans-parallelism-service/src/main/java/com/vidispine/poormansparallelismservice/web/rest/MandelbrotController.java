package com.vidispine.poormansparallelismservice.web.rest;

import com.vidispine.poormansparallelismservice.service.Mandelbrot;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mandelbrot")
public class MandelbrotController {

    @GetMapping(path = "/{min_c_re}/{min_c_im}/{max_c_re}/{max_c_im}/{width}/{height}/{inf_n}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> mandelbrot(@PathVariable(name = "min_c_re") double min_c_re,
                                             @PathVariable(name = "min_c_im") double min_c_im,
                                             @PathVariable(name = "max_c_re") double max_c_re,
                                             @PathVariable(name = "max_c_im") double max_c_im,
                                             @PathVariable(name = "width") int width,
                                             @PathVariable(name = "height") int height,
                                             @PathVariable(name = "inf_n") int inf_n) {
        byte[] mandelbrotImage = Mandelbrot.calculateToByteArray(min_c_re, min_c_im, max_c_re, max_c_im, width, height, inf_n);
        return new ResponseEntity<>(mandelbrotImage, HttpStatus.OK);
    }

}
