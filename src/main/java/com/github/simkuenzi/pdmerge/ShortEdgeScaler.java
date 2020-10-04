package com.github.simkuenzi.pdmerge;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

class ShortEdgeScaler implements Scaler {
    private final float mediaWidth;
    private final float mediaHeight;

    ShortEdgeScaler() {
        this(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
    }

    ShortEdgeScaler(float mediaWidth, float mediaHeight) {
        this.mediaWidth = mediaWidth;
        this.mediaHeight = mediaHeight;
    }

    @Override
    public float width(float origWidth, float origHeight) {
        if (origWidth <= origHeight) {
            return mediaShortEdge();
        } else {
            return origWidth * mediaShortEdge() / origHeight;
        }
    }

    @Override
    public float height(float origWidth, float origHeight) {
        if (origWidth <= origHeight) {
            return origHeight * mediaShortEdge() / origWidth;
        } else {
            return mediaShortEdge();
        }
    }

    private float mediaShortEdge() {
        return Math.min(mediaWidth, mediaHeight);
    }
}
