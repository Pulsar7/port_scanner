package port_scanner;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;


class ServiceFileError extends Exception {
    public ServiceFileError(String message) {
        super(message);
    }
}

class ReadFile extends Logger {
    public static HashMap<Integer,String> get_file_content(String filepath) throws ServiceFileError {
        HashMap<Integer,String> file_content = new HashMap<>();
        try (FileReader fileReader = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            int line_counter = 0;
            while ((line = bufferedReader.readLine()) != null) {
                line_counter++;
                line = line.strip();
                if (line.contains(":")) {
                    // check line-arguments
                    String[] line_args = line.split(":");
                    // example: 22:SSH-2.0-MyBannerGrabber -> '22' is the port number and 'SSH' is the service-banner-msg.
                    if (line_args.length > 1) {
                        // check if first argument is an integer (a port)
                        int port_value;
                        try {
                            port_value = Integer.parseInt(line_args[0]);
                        } catch (Exception e) {
                            throw new ServiceFileError("First argument in line "+line_counter+" has to be the port!");
                        }
                        file_content.put(port_value,line_args[1]);
                    } else {
                        throw new ServiceFileError("Invalid or missing elements in line "+line_counter);
                    }
                } else {
                    throw new ServiceFileError("Invalid or missing elements in line "+line_counter);
                }
            }

        } catch (Exception e) {
            error(e.getMessage());
            file_content.clear();
            file_content.put(-1,"0");
        }
        return file_content;
    }
}

public class ServiceGrabber extends ReadFile {
    public static HashMap<Integer,String> services = new HashMap<>();

    public static Boolean get_all_services() throws ServiceFileError {
        services = get_file_content("port_scanner/src/services.txt");
        if (services.containsKey(-1)) {
            return false;
        }
        return true;
    }

}
