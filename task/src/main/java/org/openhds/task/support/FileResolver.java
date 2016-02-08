package org.openhds.task.support;

import java.io.File;

public interface FileResolver {

    File resolveMobileDBFile();

    File getFileForTask(String taskName);
}
