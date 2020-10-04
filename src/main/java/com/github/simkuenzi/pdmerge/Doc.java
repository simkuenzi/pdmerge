package com.github.simkuenzi.pdmerge;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.IOException;

interface Doc {
    void addAsSource(PDFMergerUtility ut) throws IOException;
}
