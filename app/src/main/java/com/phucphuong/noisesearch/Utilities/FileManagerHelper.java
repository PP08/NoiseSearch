package com.phucphuong.noisesearch.Utilities;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.phucphuong.noisesearch.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by phucphuong on 3/25/17.
 */

public class FileManagerHelper {

    Context context;
    ListView listView;
    String directory;
    Button btn_deleteFile;


    public FileManagerHelper(Context context, ListView listView, String directory, Button btn_delete){
        this.context = context;
        this.listView = listView;
        this.directory = directory;
        this.btn_deleteFile = btn_delete;
    }


    public File[] getFiles(String DirectoryPath) {
        File f = new File(DirectoryPath);
        f.mkdirs();
        File[] file = f.listFiles();
        return file;
    }

    public ArrayList<String> getFileNames(File[] file){
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length == 0)
            return null;
        else {
            for (int i=0; i<file.length; i++)
                arrayFiles.add(file[i].getName());
        }

        return arrayFiles;
    }

    public void refreshFileList(){
        File[] files = getFiles(directory);
        final List<String> listFiles = getFileNames(files);

        if (listFiles == null){
            final List<String> noItem = Arrays.asList("No Log Files");
            setAdapter(noItem);
            btn_deleteFile.setEnabled(false);
        }else {
            setAdapter(listFiles);
            btn_deleteFile.setEnabled(true);
        }
    }

    private void setAdapter(List<String> list){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simle_list_item_multiple_choice, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
