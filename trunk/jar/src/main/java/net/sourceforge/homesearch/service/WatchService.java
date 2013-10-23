package net.sourceforge.homesearch.service;

import net.sourceforge.homesearch.dao.DirWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * User: Vitaly
 * Date: 10/23/13
 * Time: 11:51 PM
 */
public class WatchService {
    private String watchedDir;
    private DirWatcher defaultDirWatcher;
    private boolean recursive;

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public String getWatchedDir() {
        return watchedDir;
    }

    public void setWatchedDir(String watchedDir) {
        this.watchedDir = watchedDir;
    }

    public void init() throws IOException {
        File f = new File(watchedDir);
        f.mkdirs();
        Path dir = Paths.get(watchedDir);
        defaultDirWatcher = new DirWatcher(dir, recursive);
        defaultDirWatcher.processEvents();
    }

    public void destroy(){

    }
}
