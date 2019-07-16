package com.ka09solutions.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MultiFileReader
{
    private static final Logger LOG = LogManager.getLogger(MultiFileReader.class);

    private List<String> files;
    private List<String> filesData;
    private ResultHandler resultHandler;

    public MultiFileReader(List<String> files, ResultHandler resultHandler)
    {
        this.files = files;
        this.resultHandler = resultHandler;
    }

    public void read()
    {
        if (this.files == null || this.files.isEmpty())
        {
            this.resultHandler.onSuccess(this.files, Collections.emptyList());
            return;
        }

        Deque<String> filesQueue = new ArrayDeque<>();
        int size = this.files.size();
        for (int i = (size -1); i>=0; i--)
        {
            filesQueue.push(this.files.get(i));
        }

        this.filesData = new ArrayList<>();
        this.readFile(filesQueue);
    }

    private void readFile(Deque<String> filesQueue)
    {
        if (filesQueue.isEmpty())
        {
            LOG.info("All files loaded.");
            this.resultHandler.onSuccess(this.files, this.filesData);
            return;
        }

        String fileToRead = filesQueue.pop();
        LOG.info("File to read : " + fileToRead);
        VertxInstance.get().fileSystem().readFile(fileToRead, result -> {
            if (result.succeeded())
            {
                this.filesData.add(result.result().toString());
                readFile(filesQueue);
            }
            else
            {
                String message = "Could not read file : " + fileToRead + ". Cause:" + result.cause();
                LOG.error(message, result.cause());
                this.resultHandler.onError(fileToRead, result.cause());
            }
        });

    }


    public interface ResultHandler
    {
        void onSuccess(List<String> files, List<String> filesData);

        void onError(String filename, Throwable cause);
    }
}

