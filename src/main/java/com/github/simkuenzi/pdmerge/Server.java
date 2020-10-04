package com.github.simkuenzi.pdmerge;

import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Server {

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port", "9000"));
        String context = System.getProperty("com.github.simkuenzi.http.context", "/pdmerge");
        Path base = Path.of(System.getProperty("user.home"), "pdmerge");
        new Server(port, context, base).start();
    }

    private final int port;
    private final String context;
    private final Path base;

    public Server(int port, String context, Path base) {
        this.port = port;
        this.context = context;
        this.base = base;
    }

    public void start() {

        Javalin.create(config -> {
            config.contextPath = context;
            // Got those errors on the apache proxy with compression enabled. Related to the Issue below?
            // AH01435: Charset null not supported.  Consider aliasing it?, referer: http://pi/one-egg/
            // AH01436: No usable charset information; using configuration default, referer: http://pi/one-egg/
            config.compressionStrategy(CompressionStrategy.NONE);
        })

        // Workaround for https://github.com/tipsy/javalin/issues/1016
        // Aside from mangled up characters the wrong encoding caused apache proxy to fail on style.css.
        // Apache error log: AH01385: Zlib error -2 flushing zlib output buffer ((null))
        .before(ctx -> {
            if (ctx.res.getCharacterEncoding().equals("utf-8")) {
                ctx.res.setCharacterEncoding(StandardCharsets.UTF_8.name());
            }
        })
        .start(port)

        .get("/", ctx -> {
            Properties properties = new Properties();
            try (InputStream in = Files.newInputStream(base.resolve("conf.properties"))) {
                properties.load(in);
            }

            Path resDir = Path.of(properties.getProperty("resDir"));
            Scaler scaler = new ShortEdgeScaler();
            List<Doc> docs = ctx.queryParamMap().keySet().stream()
                    .sorted()
                    .map(k -> ctx.queryParamMap().get(k).get(0))
                    .map(resDir::resolve)
                    .map(f -> f.toString().endsWith(".pdf")
                            ? new PdfFileDoc(f)
                            : new ImageFileDoc(f, scaler))
                    .collect(Collectors.toList());

            DocBundle bundle = new DocBundle(docs);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            bundle.write(buffer);

            ctx.contentType("application/pdf").result(new ByteArrayInputStream(buffer.toByteArray()));
        });
    }
}
