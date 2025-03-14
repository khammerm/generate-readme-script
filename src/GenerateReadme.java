import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class GenerateReadme {
    public static void main(String[] args) throws IOException {
        String projectName = getAppName();
        String description = "test desc";
        String restDirectory = "C:\\Projects\\spring-boot-3-rest-api-example\\src\\main\\java\\com\\bezkoder\\spring\\restapi\\controller";

        List<String> endpoints = extractEndpoints(restDirectory);
        generateReadme(projectName, description, endpoints);
        System.out.println("README.md generated successfully!");
    }

    private static String getAppName() {
        // app name passed as var in pipeline
        String appName = System.getenv("APP_NAME");
        if (appName != null) {
            return appName; // Remove the .jar extension if necessary
        }
        // Fallback to the previous logic if environment variable is not set
        Path currentPath = Paths.get("").toAbsolutePath();
        Path projectRoot = currentPath.getParent();
        return projectRoot.getFileName().toString();
    }

    private static List<String> extractEndpoints(String directory) throws IOException {
        List<String> endpoints = new ArrayList<>();
        Pattern pattern = Pattern.compile("@RequestMapping\\(.*?value\\s*=\\s*\"(.*?)\"", Pattern.DOTALL);


        Files.walk(Paths.get(directory)).filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        String content = new String(Files.readAllBytes(path));
                        Matcher matcher = pattern.matcher(content);
                        while (matcher.find()) {
                            endpoints.add(matcher.group(1));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        return endpoints;
    }

    private static void generateReadme(String projectName, String description, List<String> endpoints) throws IOException {
        StringBuilder readmeContent = new StringBuilder();
        readmeContent.append("# ").append(projectName).append("\n\n")
                .append("## Description\n").append(description).append("\n\n")
                .append("## Endpoints\n");

        for (String endpoint : endpoints) {
            readmeContent.append("- `").append(endpoint).append("`\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("README.md"))) {
            writer.write(readmeContent.toString());
        }
    }
}