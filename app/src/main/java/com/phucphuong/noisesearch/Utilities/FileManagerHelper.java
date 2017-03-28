package com.phucphuong.noisesearch.Utilities;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.phucphuong.noisesearch.Fragments.UnsentFragment;
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
    View view;
    Button btn_send, btn_deleteFile, btn_openFile;


    public FileManagerHelper(String directory, View view){
        this.context = view.getContext();
        this.listView = (ListView)view.findViewById(R.id.listView);
        this.directory = directory;
        this.view = view;
        this.btn_deleteFile = (Button) view.findViewById(R.id.btn_delete);
        this.btn_send = (Button) view.findViewById(R.id.btn_send);
        this.btn_openFile = (Button)view.findViewById(R.id.btn_open);
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
            if (btn_send != null){
                btn_send.setEnabled(false);
            }
            if (btn_openFile != null){
                btn_openFile.setEnabled(false);
            }

        }else {
            setAdapter(listFiles);
            btn_deleteFile.setEnabled(true);
            if (btn_send != null){
                btn_send.setEnabled(true);
            }
            if (btn_openFile != null){
                btn_openFile.setEnabled(true);
            }
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
