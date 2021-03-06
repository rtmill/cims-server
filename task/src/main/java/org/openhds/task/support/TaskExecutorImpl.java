package org.openhds.task.support;

import org.openhds.task.SyncFileTask;
import org.openhds.task.TaskContext;
import org.openhds.task.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javax.annotation.Resource;

@Component("openhdsTaskExecutor")
public class TaskExecutorImpl implements TaskExecutor {

    private FileResolver fileResolver;
    private AsyncTaskService asyncTaskService;
    private SyncFileTask mobileDBWriter;

    @Autowired
    public TaskExecutorImpl(AsyncTaskService asyncTaskService, FileResolver fileResolver) {
        this.asyncTaskService = asyncTaskService;
        this.fileResolver = fileResolver;
    }

    @Override
    public void executeMobileDBTask() {
        if (asyncTaskService.taskShouldRun(AsyncTaskService.MOBILEDB_TASK_NAME)) {
            asyncTaskService.startTask(AsyncTaskService.MOBILEDB_TASK_NAME);
            File dbFile = fileResolver.resolveMobileDBFile();
            mobileDBWriter.run(new TaskContext(dbFile));
        }
    }


    @Resource(name = "mobileDBWriter")
    public void setMobileDBWriter(SyncFileTask mobileDBWriter) {
        this.mobileDBWriter = mobileDBWriter;
    }
}
