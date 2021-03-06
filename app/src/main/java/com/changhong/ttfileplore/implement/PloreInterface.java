package com.changhong.ttfileplore.implement;

import java.io.File;

public interface PloreInterface {

    /**
     * 加载本地文件夹和文件数据
     *
     * @param folder 父文件夹
     */
    void loadData(File folder, int sorttype);

}
