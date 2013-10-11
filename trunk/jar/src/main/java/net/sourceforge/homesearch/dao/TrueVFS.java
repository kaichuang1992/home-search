package net.sourceforge.homesearch.dao;

import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileWriter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 11/10/13
 * Time: 16:10
 */
public class TrueVFS {
    public static void main(String... args){
        TrueVFS trueVFS = new TrueVFS();
        try {
            trueVFS.writeFileToArchive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFileToArchive() throws IOException {
        File entry = new TFile("archive.zip/dir/HälloWörld.txt");
        Writer writer = new TFileWriter(entry);
        try {
            writer.write("Hello world!\n");
        } finally {
            writer.close();
        }
    }
}
