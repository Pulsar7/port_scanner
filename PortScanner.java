/*
 * Simple Port-Scanner in Java
 * 
 *  - Banner Grabber
 *  - Fuzzing
 */
package port_scanner;
import java.io.*;
import java.net.*;


public class PortScanner extends Logger {
    public static void main(String[] args) throws ServiceFileError {
        if (args.length < 1) {
            error("Too few arguments! You have to enter the host-IP!");
            return;
        }
        String host_ip = args[0], service_name = "";
        int host_port = 0, open_ports = 0;
        Boolean status = ServiceGrabber.get_all_services();
        if (status == false) {
            return;
        }
        info("Started scan on "+host_ip);
        while (host_port < 65535) {
            host_port++;
            try {
                Socket socket = new Socket(host_ip, host_port);
                open_ports++;
                service_name = get_service(socket,host_port,host_ip);
                info("Open Port at "+host_port+" => "+service_name);
                socket.close();
            } catch (IOException e) {
                // error("An exception occured: "+e.getMessage());
                // break;
            }
        }
        System.out.println("");
        if (open_ports > 0) {
            info("All information are not 100% valid and could be manipulated by the service.");
        }
        info("Found "+open_ports+" open ports at "+host_ip);
        info("Scan finished.");
    }

    public static String get_service(Socket socket, int current_port, String current_host) {
        String service_description = "unknown"; // default: hostname of device
        if (socket.getInetAddress().getHostName() != current_host) {
            service_description = socket.getInetAddress().getHostName();
        }
        String receivedMessage = "";
        try {
            socket.setSoTimeout(10000);
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            String message = "Hello world";
            if (ServiceGrabber.services.containsKey(current_port)) {
                message = ServiceGrabber.services.get(current_port);
            }
            writer.write(message);
            writer.flush();
            InputStream inputStream = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            char[] buffer = new char[65507];
            int bytesRead = reader.read(buffer);
            if (bytesRead > 0) {
                receivedMessage = new String(buffer, 0, bytesRead).toLowerCase();
            }
            inputStream.close();
            reader.close();
            if (receivedMessage.length() > 0) {
                // checks if http-content may be included in response
                if (receivedMessage.contains("http/")) {
                    service_description = "HTTP-Service";
                    // checks if version of http-service is included
                    if (receivedMessage.contains("version")) {
                        String[] args = receivedMessage.split("version");
                        if (args.length > 1) {
                            service_description = service_description+"; Version:" +args[1];
                        }
                    }
                // checks if ssh-keyword is in content included
                } else if (receivedMessage.contains("ssh")) {
                    service_description = "SSH-Service";
                }

            }
        } catch (IOException e) {
            String error_msg = e.getMessage();
            if (error_msg.contains("Read timed out") == false) {
                error(error_msg);
            }
        }
        return service_description;
    }
}
