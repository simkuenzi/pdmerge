package com.github.simkuenzi.pdmerge;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;

class ImageFileDoc implements Doc {
    private final Path file;
    private final Scaler scaler;

    ImageFileDoc(Path file, Scaler scaler) {
        this.file = file;
        this.scaler = scaler;
    }

    @Override
    public void addAsSource(PDFMergerUtility ut) throws IOException {
        ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            BufferedImage bi;
            try (InputStream in = new FileInputStream(file.toFile())) {
                bi = ImageIO.read(in);
            }
            float width = scaler.width(bi.getWidth(), bi.getHeight());
            float height = scaler.height(bi.getWidth(), bi.getHeight());
            PDPage page = new PDPage(new PDRectangle(width, height));
            doc.addPage(page);

            ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", imageBuffer);

            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, imageBuffer.toByteArray(), null);
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.drawImage(pdImage, 0, 0, width, height);
            }

            doc.save(pdfBuffer);
        }

        ut.addSource(new ByteArrayInputStream(pdfBuffer.toByteArray()));
    }
}
