package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ExportDirUnit {

    public static String getExportDir(){
        String executedJarLocation = ExportDirUnit.class.getProtectionDomain().getCodeSource()
                .getLocation().getFile();
        int index = executedJarLocation.indexOf("build");
        File projDirFile = null;
        if(index != -1){
            String projectDir = executedJarLocation.substring(0, index);
            projDirFile = new File(projectDir + "export");
            if(!projDirFile.exists()){
                boolean mkdir = projDirFile.mkdir();
                log.info("Export dir {} was created: ", projDirFile, mkdir);
            }
        }else{
            index = executedJarLocation.lastIndexOf("/");
            String projectDir = executedJarLocation.substring(0, index + 1);
            projDirFile = new File(projectDir + "export");
            if(!projDirFile.exists()){
                boolean mkdir = projDirFile.mkdir();
                log.info("Export dir {} was created: ", projDirFile, mkdir);
            }
        }
        return projDirFile.getAbsolutePath();
    }
}
