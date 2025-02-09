import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public final class ServidorWeb {
    private static final int PUERTO = 6789;
    private static final int NUMERO_HILOS = 10;

    public static void main(String argv[]) throws Exception {
        // Crear un ThreadPool con un número fijo de hilos
        ExecutorService threadPool = Executors.newFixedThreadPool(NUMERO_HILOS);
        
        // Crear un socket de servidor para escuchar conexiones
        ServerSocket socketServidor = new ServerSocket(PUERTO);
        System.out.println("Servidor Web en espera de conexiones en el puerto " + PUERTO + "...");

        // Bucle infinito para aceptar solicitudes
        while (true) {
            // Espera una conexión de un cliente
            Socket socketConexion = socketServidor.accept();
            
            // Enviar la tarea al ThreadPool en lugar de crear un hilo manualmente
            threadPool.execute(new SolicitudHttp(socketConexion));
        }
    }
}

// Clase que maneja cada solicitud HTTP
final class SolicitudHttp implements Runnable {
    final static String CRLF = "\r\n";
    private Socket socket;

    // Constructor
    public SolicitudHttp(Socket socket) {
        this.socket = socket;
    }

    // Método ejecutado en el nuevo hilo del ThreadPool
    public void run() {
        try {
            procesarSolicitud();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Método que procesa la solicitud HTTP
    private void procesarSolicitud() throws Exception {
        // Referencia al stream de salida
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Stream de entrada envuelto en BufferedReader
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Leer la línea de solicitud HTTP
        String lineaDeSolicitud = br.readLine();
        System.out.println("Solicitud recibida: " + lineaDeSolicitud);
        
        String lineaDelHeader;
        while ((lineaDelHeader = br.readLine()).length() != 0) {
            System.out.println(lineaDelHeader);
        }

        // Extraer el nombre del archivo solicitado
        StringTokenizer partesLinea = new StringTokenizer(lineaDeSolicitud);
        partesLinea.nextToken(); // Omitir el método GET
        String nombreArchivo = "." + partesLinea.nextToken();

        // Intentar abrir el archivo
        FileInputStream fis = null;
        boolean existeArchivo = true;
        try {
            fis = new FileInputStream(nombreArchivo);
        } catch (FileNotFoundException e) {
            existeArchivo = false;
        }

        // Construir la respuesta HTTP
        String lineaDeEstado;
        String lineaDeTipoContenido;
        String cuerpoMensaje = null;

        if (existeArchivo) {
            lineaDeEstado = "HTTP/1.0 200 OK" + CRLF;
            lineaDeTipoContenido = "Content-Type: " + contentType(nombreArchivo) + CRLF;
        } else {
            lineaDeEstado = "HTTP/1.0 404 Not Found" + CRLF;
            lineaDeTipoContenido = "Content-Type: text/html" + CRLF;
            cuerpoMensaje = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                            "<BODY><h1>404 Not Found</h1></BODY></HTML>";
        }

        // Enviar encabezados HTTP
        os.writeBytes(lineaDeEstado);
        os.writeBytes(lineaDeTipoContenido);
        os.writeBytes(CRLF);

        // Enviar el contenido del archivo o mensaje de error
        if (existeArchivo) {
            enviarBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(cuerpoMensaje);
        }

        // Cerrar conexiones
        os.close();
        br.close();
        socket.close();
    }

    // Método para enviar bytes del archivo al cliente
    private static void enviarBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    // Método para determinar el tipo MIME
    private static String contentType(String nombreArchivo) {
        if (nombreArchivo.endsWith(".htm") || nombreArchivo.endsWith(".html")) {
            return "text/html";
        }
        if (nombreArchivo.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if (nombreArchivo.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
}