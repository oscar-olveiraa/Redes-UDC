package es.udc.redes.webserver;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpRequest {
    private final String[] request_lines;
    private PrintWriter output_writer;
    private File resource_file;
    private int status_code = 100;
    private int bytes_sent = 0;

    public HttpRequest(String req) {
        this.request_lines = req.split(System.lineSeparator());
    }

    public int respond(OutputStream output) {

        //Builds a PrintWriter object to send characters, with AutoFlush enabled
        this.output_writer = new PrintWriter(output, true);

        //Gets the request resource name and parameters
        String resource_name = getResourceName();

        //If the resource name wasn't parsed, it means that the request is not correct
        if (resource_name == null) {
            output_writer.write(badRequest());
            output_writer.println();
            return status_code;
        }

        //Handling dynamic requests
        status_code = handleDynRequest(resource_name);

        //If if wasn't a dynamic request, continue
        if (status_code != 100) {
            return status_code;
        }

        this.resource_file = openFile(resource_name);

        if (status_code != 100)
            return status_code;

        if (!resource_file.exists()) {
            //Checks that the file exists
            output_writer.write(fileNotFound());
            output_writer.println();
            return status_code;
        }

        try {
            status_code = checkIfModified();
        } catch (DateTimeParseException ex) {
            //If the If-Modified-Since line is wrong, then send a 400 Bad Request and return
            System.out.println("If Modified Since line could not be parsed");
            output_writer.write(badRequest());
            output_writer.println();
            return status_code;
        }

        //Creates an Http Resource from the file, to read header and body from it
        HttpResource resource = new HttpResource(resource_file);

        //Status and header lines
        status_code = resource.writeHead(output_writer, (status_code != 304));

        //Writes blank line
        output_writer.println();
        //Output PrintWriter is flushed now to prevent problems when sending the file body
        output_writer.flush();

        //Only if it is a GET request AND the file was modified, then sends body
        if (("GET".equals(this.request_lines[0].split(" ")[0])) && (status_code == 200)) {
            try {
                bytes_sent = resource.writeBody(output, output_writer); //File body
            } catch (FileNotFoundException ex) {
                //Should never happen because its already been checked that the file exists
                System.out.println("File not found exception");
                Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, null, ex);
                return 500;
            }
        }
        return status_code;
    }

    private String getResourceName() {
        String petition_line = request_lines[0];
        String[] petition_words = petition_line.split(" ");
        try {
            if (!petition_words[2].startsWith("HTTP/")) {
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            //Bad request
            return null;
        }

        if ((!"GET".equals(petition_words[0])) && (!"HEAD".equals(petition_words[0]))) {
            //If the method is not GET or HEAD, send 400 badRequest
            return null;
        }

        return petition_words[1].split("\\?")[0].substring(1);
    }


    private int handleDynRequest(String resource_name) {
        if (resource_name.endsWith(".do")) {
            try {
                output_writer.println("HTTP/1.0 200 OK");
                output_writer.write(getHttpDate());
                output_writer.println("Server: WebServer_120");
                output_writer.println("Content-Length:\n".length());
                output_writer.println("Content-Type: text/html");
                //Blank line
                output_writer.println();
                output_writer.flush();
                //Writes body
                //output_writer.println("aqui va a mostrar lo que quiero");
                output_writer.println();
                output_writer.flush();
                return (status_code = 200);
            } catch (Exception ex) {
                Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 100;
    }

    private File openFile(String resource_name) {
        try {
            return new File(resource_name);
        } catch (NullPointerException ex) {
            return new File("");
        }
    }

    private int checkIfModified() throws DateTimeParseException {
        int i;
        for (i = 1; i < request_lines.length; i++) {
            if (request_lines[i].startsWith("If-Modified-Since: ")) {
                if (wasModified(resource_file, request_lines[i].substring(19)))
                    return 100;
                else
                    return (this.status_code = 304);
            }
        }
        return 100;
    }

    public static String getHttpDate() {
        return "Date: " +
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME) +
                System.lineSeparator();
    }

    private String badRequest() {
        String response = "HTTP/1.0 400 Bad Request" +
                System.lineSeparator() +
                getHttpDate();
        this.status_code = 400;
        return response;
    }

    private String fileNotFound() {
        String response = "HTTP/1.0 404 Not Found" + System.lineSeparator() + getHttpDate();
        this.status_code = 404;
        return response;
    }

    public boolean wasModified(File file, String since) throws DateTimeParseException {
        long sincedt = (ZonedDateTime.parse(since, (DateTimeFormatter.RFC_1123_DATE_TIME)).toEpochSecond());
        long file_mod = file.lastModified() / 1000;
        boolean wasmod = (sincedt < file_mod);
        if (!wasmod)
            this.status_code = 304;
        return wasmod;
    }
}