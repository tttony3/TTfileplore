package com.changhong.ttfileplore.data;

import android.os.Parcel;
import android.os.Parcelable;


import com.felipecsl.asymmetricgridview.AsymmetricItem;

import java.io.File;

/**
 * Created by tangli on 2015/11/25.
 * Website: https://github.com/tttony3
 */
public class FileImplAsymmeric implements AsymmetricItem {

    private File file;

    private int columnSpan;
    private int rowSpan;
    private int position;

    private FileImplAsymmeric(Parcel in) {
        file = (File) in.readSerializable();
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
    }

    public FileImplAsymmeric(File file, int columnSpan, int rowSpan, int position) {
        this.file = file;
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
    }

    public FileImplAsymmeric(String path, int columnSpan, int rowSpan, int position) {
        this.file = new File(path);
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
    }

    public File getFile() {
        return file;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int getColumnSpan() {
        return columnSpan;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(file);
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);

    }

    public static final Parcelable.Creator<FileImplAsymmeric> CREATOR = new Parcelable.Creator<FileImplAsymmeric>() {
        public FileImplAsymmeric createFromParcel(Parcel in) {
            return new FileImplAsymmeric(in);
        }

        public FileImplAsymmeric[] newArray(int size) {
            return new FileImplAsymmeric[size];
        }
    };
}
