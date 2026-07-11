package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class HeaderCardBatchLoadResponse {
    private int filesSeen;
    private int filesLoaded;
    private int filesSkipped;
    private List<String> loadedFiles = new ArrayList<>();
    private List<String> skippedFiles = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public int getFilesSeen() { return filesSeen; }
    public void setFilesSeen(int filesSeen) { this.filesSeen = filesSeen; }
    public int getFilesLoaded() { return filesLoaded; }
    public void setFilesLoaded(int filesLoaded) { this.filesLoaded = filesLoaded; }
    public int getFilesSkipped() { return filesSkipped; }
    public void setFilesSkipped(int filesSkipped) { this.filesSkipped = filesSkipped; }
    public List<String> getLoadedFiles() { return loadedFiles; }
    public void setLoadedFiles(List<String> loadedFiles) { this.loadedFiles = loadedFiles; }
    public List<String> getSkippedFiles() { return skippedFiles; }
    public void setSkippedFiles(List<String> skippedFiles) { this.skippedFiles = skippedFiles; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
