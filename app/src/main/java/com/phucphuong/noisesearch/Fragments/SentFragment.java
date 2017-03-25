package com.phucphuong.noisesearch.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.FileManagerHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentFragment extends Fragment {


    private ListView listView;
    private Button btn_sendFile, btn_deleteFile;

    //
    private String directory, source;



    public FileManagerHelper fileManagerHelper;

    public SentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sent, container, false);

        listView = (ListView)view.findViewById(R.id.listView);
        btn_sendFile = (Button)view.findViewById(R.id.btn_send);
        btn_deleteFile = (Button)view.findViewById(R.id.btn_delete);


        directory = getContext().getFilesDir().toString() + "/Sent Files";
        fileManagerHelper = new FileManagerHelper(getContext(), listView, directory, btn_deleteFile);
        fileManagerHelper.refreshFileList();

        btn_sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();

                for (int i = 0; i < listView.getCount(); i++){
                    if (sparseBooleanArray.get(i)){

                    }
                }

                fileManagerHelper.refreshFileList();
            }
        });

        btn_deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();

                        for (int i = 0; i < listView.getCount(); i++){
                            if (sparseBooleanArray.get(i)){
                                source = getContext().getFilesDir().toString() + "/Sent Files/" + listView.getItemAtPosition(i).toString();
                                File delFile = new File(source);
                                delFile.delete();
                            }
                        }
                        fileManagerHelper.refreshFileList();
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        return view;
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
        File[] files = getFiles(getContext().getFilesDir().toString() + "/Sent Files");
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.simle_list_item_multiple_choice, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }
}
