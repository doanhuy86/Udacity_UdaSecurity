package com.udacity.catpoint.Image;

import java.awt.image.BufferedImage;

public interface ImageService {
    boolean imageContainsCat(BufferedImage image, float confidenceThreshHold);
}
