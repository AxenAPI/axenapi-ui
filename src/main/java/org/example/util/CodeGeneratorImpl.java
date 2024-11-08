package org.example.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public enum CodeGeneratorImpl implements CodeGenerator {
    INSTANCE;
    static public final String OPENAPI_GENERATOR_CLI_JAR_PATH = "openapi-generator-cli.jar";
    static public final String AXENAPI_GENERATOR_JAR_PATH = "axenapi-generator-2.0.0.jar";
    private static final String CMD_PREFIX = "java -cp ";

    private String openapiGeneratorCliPath = OPENAPI_GENERATOR_CLI_JAR_PATH;
    private String axenapiGeneratorPath = AXENAPI_GENERATOR_JAR_PATH;

    @Override
    public boolean generateCode(List<ServiceInfo> serviceInfoList, String directory) {
        //$ java -cp "axenapi-generator-2.0.0.jar;openapi-generator-cli.jar" org.openapitools.codegen.OpenAPIGenerator generate -g messageBroker -o out/ -i api-docs.json --additional-properties=kafkaBootstrap=localhost:29092
        serviceInfoList.forEach(serviceInfo -> {
            directory.trim();
            String outputDir;
            if(directory.endsWith("\\") || directory.endsWith("/")) {
                outputDir = directory + serviceInfo.getName();
            } else {
                outputDir = directory + "\\" + serviceInfo.getName();
            }


            boolean mkdirs = new File(outputDir).mkdirs();
            StringBuilder cmd = new StringBuilder();
            cmd.append(CMD_PREFIX).append("\"")
                    .append(axenapiGeneratorPath + ";")
                    .append(openapiGeneratorCliPath)
                    .append("\"")
                    .append(" org.openapitools.codegen.OpenAPIGenerator generate -g messageBroker ")
                    .append("-o " + outputDir + " -i ")
                    .append(serviceInfo.getSpecificationPath() + " ")
                    .append("--additional-properties=kafkaBootstrap=")
                    .append(serviceInfo.getBrokerAddress())
                    .append(",port=")
                    .append(serviceInfo.getPort())
                    .append(",useGradle=true")
                    .append(",artifactId=").append(serviceInfo.getName());
            String cmdStr = cmd.toString();
            System.out.println(cmdStr);
            BufferedReader reader;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("cmd.exe", "/c", cmdStr);
                Process exec = processBuilder.start();
                InputStream inputStream = exec.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                // wait for the process to finish
//                System.out.println("Process finished with exit code: " + exec.exitValue());
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        return false;
    }

}
