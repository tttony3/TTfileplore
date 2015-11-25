package com.changhong.ttfileplore.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PloreData {
    public static int NAME = 1;
    public static int TIME = 2;
    int nDirectory;
    int nFile;
    List<File> files;
    List<File> nfolders;
    List<File> nfiles;

    public PloreData(File folder, boolean showhide) {
        this(folder, showhide, NAME);
    }


    public PloreData(File folder, boolean showhide, int type) {
        files = new ArrayList<>();
        nfolders = new ArrayList<>();
        nfiles = new ArrayList<>();
        lodaData(folder, showhide, type);
    }

    /**
     * @return 子文件列表
     */
    public List<File> getfiles() {
        return files;
    }

    /**
     * @return 子文件夹个数
     */
    public int getnDirectory() {
        return nfolders.size();
    }

    /**
     * @return 子文件个数
     */
    public int getnFile() {
        return nfiles.size();
    }

    /**
     * @param folder   父文件夹
     * @param showhide 是否显示隐藏文件
     */
    private void lodaData(File folder, boolean showhide, int type) {
        File[] names = folder.listFiles();

        for (File tmp :names) {
            if (!showhide) {
                if (tmp.getName().startsWith("."))
                    continue;
            }
            if (tmp.isDirectory()) {
                nfolders.add(tmp);
            } else
                nfiles.add(tmp);
        }

        if (type == NAME) {
            SortByName sortByName = new SortByName();
            Collections.sort(nfolders, sortByName);
            Collections.sort(nfiles, sortByName);
        } else if (type == TIME) {
            SortByTime sortByTime = new SortByTime();
            Collections.sort(nfolders, sortByTime);
            Collections.sort(nfiles, sortByTime);
        }

        files.addAll(nfolders);
        files.addAll(nfiles);

    }


    class SortByName implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            if (file1.getName().charAt(0) > file2.getName().charAt(0)) {
                return 1;
            }
            if (file1.getName().charAt(0) == file2.getName().charAt(0)) {
                return 0;
            }
            if (file1.getName().charAt(0) < file2.getName().charAt(0)) {
                return -1;
            }
            return -1;
        }
    }


    class SortByTime implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() > file2.lastModified()) {
                return -1;
            } else return 1;
        }

    }
}
