package org.example.util;

import java.util.List;

public interface CodeGenerator {
    public boolean generateCode(List<ServiceInfo> serviceInfoList, String directory);
}
