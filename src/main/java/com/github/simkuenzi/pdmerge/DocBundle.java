package com.github.simkuenzi.pdmerge;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class DocBundle {

    private final List<Doc> docs;

    DocBundle(Doc... docs) {
        this(List.of(docs));
    }

    public DocBundle(List<Doc> docs) {
        this.docs = List.copyOf(docs);
    }

    void write(OutputStream out) throws IOException {
        PDFMergerUtility ut = new PDFMergerUtility();
        for (Doc doc : docs) {
            doc.addAsSource(ut);
        }
        ut.setDestinationStream(out);
        ut.mergeDocuments(MemoryUsageSetting.setupMixed(5_242_880, 21_474_836_480L));
    }
}
