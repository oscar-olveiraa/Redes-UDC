package es.udc.redes.webserver;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResource {
    private final File file;
    private final String content_type;

    public HttpResource(File file) {
        this.file = file;
        content_type = this.getType(file);
    }
    public int writeHead(PrintWriter output, boolean was_mod) {
        int code;
        if (was_mod) {
            output.println("HTTP/1.0 200 OK");
            code = 200;
        } else {
            output.println("HTTP/1.0 304 Not Modified");
            code = 304;
        }
        output.write(HttpRequest.getHttpDate());
        output.println("Server: WebServer_120");
        output.println("Content-Type: " + content_type);
        try {
            output.println("Content-Length: " + Files.size(file.toPath()));
        } catch (IOException ex) {
            Logger.getLogger(HttpResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.write(getLastModified());
        return code;
    }

    public int writeBody(OutputStream output, PrintWriter output_writer) throws FileNotFoundException {
        switch (this.getExtension()) {
            case ("html"):
            case ("txt"):
                return writeText(output_writer);
            default:
                return writeBinary(new PrintWriter(output, true));
        }
    }

    private int writeText(PrintWriter output) throws FileNotFoundException {
        FileReader reader = new FileReader(this.file);
        int c;
        int counter = 0;
        try {
            while ((c = reader.read()) != -1) {
                if (c > 0xff)
                    counter += 2;
                else
                    counter++;
                output.write(c);
            }
        } catch (IOException ex) {
            Logger.getLogger(HttpResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.flush();
        return counter;
    }

    private int writeBinary(PrintWriter output) throws FileNotFoundException {
        //FileInputStream input = new FileInputStream("index.html");
        FileInputStream input = new FileInputStream(this.file);
        int c;
        int counter = 0;
        try {
            while ((c = input.read()) != -1) {
                counter++;
                output.write(c);
            }
        } catch (IOException ex) {
            Logger.getLogger(HttpResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return counter;
    }

    public static String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }
        return extension;
    }

    private String getExtension() {
        return getExtension(this.file.getPath());
    }

    private String getType(File fileRequest) {
        String requested = fileRequest.getName();
        String type = "application/octet-stream";

        if (requested.endsWith(".htm") || requested.endsWith(".html")) {
            type = "text/html";
        }
        if (requested.endsWith(".log") || requested.endsWith(".txt")) {
            type = "text/plain";
        }
        if (requested.endsWith(".gif")) {
            type = "image/gif";
        }
        if (requested.endsWith(".png")) {
            type = "image/png";
        }
        return type;
    }

    public String getLastModified() {
        StringBuilder sb = new StringBuilder("Last-Modified: ");
        sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.file.lastModified()), ZoneOffset.UTC)));
        sb.append("\n");
        return sb.toString();
    }
}