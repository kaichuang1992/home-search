package com.vandale;

import net.java.truevfs.access.*;
import net.java.truevfs.comp.zipdriver.ZipDriver;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 * Date: 2/2/14
 */
public class Preparator {
    private static String DIC_NAME = "wn";
    private static String PATH = "C:\\projects\\hs\\trunk\\" + DIC_NAME + ".zip\\";
    private static String HEADER = "#NAME\t\"Dikke Van Dale\"\n#INDEX_LANGUAGE\t\"Dutch\"\n#CONTENTS_LANGUAGE\t\"Dutch\"\n";

    private static class IndexSetComparator implements Comparator {


        @Override
        public int compare(Object o1, Object o2) {
            IndexSet is1 = (IndexSet) o1;
            IndexSet is2 = (IndexSet) o2;
            return is1.headword.compareTo(is2.headword);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        List<IndexSet> l = readIndex();
        Collections.sort(l, new IndexSetComparator());

        FileOutputStream fos = new FileOutputStream(DIC_NAME + ".xhtml");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        Writer out = new BufferedWriter(osw);

        out.write(HEADER);

        int counter = 0;
        for (IndexSet is : l) {
            String hw = is.headword;
            String art = readFromArchive(is.realIndex);
            out.write(hw);
            out.write("\n\t");
            out.write(art);
            out.write("\n");
            ++counter;
            if (counter % 1000 == 0) {
                System.out.println(counter);
            }
//            if (counter > 1000) {
//                break;
//            }
        }
//        fos.close();
//        osw.close();
        out.close();
    }

    private static List<IndexSet> readIndex() throws Exception {
        String pathToIndex = PATH + DIC_NAME + ".ser";
        File entry = new TFile(pathToIndex);
        InputStream os = new TFileInputStream(entry);
        ObjectInputStream ois = new ObjectInputStream(os);
        List<IndexSet> l = (List<IndexSet>) ois.readObject();
        ois.close();
        os.close();
        return l;
    }

    private static int counter2folder(int counter) {
        return counter / 1000 + 1;
    }

    private static String readFromArchive(int counter) throws IOException {
        String fullPath = PATH + counter2folder(counter) + File.separator + counter + ".xhtml";

        TConfig.current().setArchiveDetector(new TArchiveDetector("zip", new ZipDriver()));

        TFile entry = new TFile(fullPath);
        Reader reader = new TFileReader(entry, Charset.forName("utf-8"));
        BufferedReader in = new BufferedReader(reader);
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        in.close();
        String art = sb.toString();
//        art = art.replaceAll("http://goto", "../../goto");
        return art;
    }
}
