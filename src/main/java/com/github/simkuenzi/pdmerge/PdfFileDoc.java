package com.github.simkuenzi.pdmerge;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.FileNotFoundException;
import java.nio.file.Path;

class PdfFileDoc implements Doc {
    private final Path file;

    PdfFileDoc(Path file) {
        this.file = file;
    }

    @Override
    public void addAsSource(PDFMergerUtility ut) throws FileNotFoundException {
        ut.addSource(file.toFile());
    }
}
