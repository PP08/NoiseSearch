package com.phucphuong.noisesearch.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.FileManagerHelper;
import com.phucphuong.noisesearch.Utilities.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 */
public class UnsentFragment extends Fragment {

    private ListView listView;
    private Button btn_sendFile, btn_deleteFile;
    //
    private String directory, source;
    public FileManagerHelper fileManagerHelper;

    //
    String prefix;

    public UnsentFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_unsent, container, false);

        listView = (ListView)view.findViewById(R.id.listView);
        btn_sendFile = (Button)view.findViewById(R.id.btn_send);
        btn_deleteFile = (Button)view.findViewById(R.id.btn_delete);

        final List<String> data = new ArrayList<>();

        directory = getContext().getFilesDir().toString() + "/Unsent Files";

        fileManagerHelper = new FileManagerHelper(directory, view);

        fileManagerHelper.refreshFileList();

        //for progress dialog

        btn_sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                File fileDirUnsent = new File(getContext().getFilesDir() + "/Unsent Files");
                fileDirUnsent.mkdirs();
                File fileDirSent = new File(getContext().getFilesDir() + "/Sent Files");
                fileDirSent.mkdirs();

                for (int i = 0; i < listView.getCount(); i++){
                    if (sparseBooleanArray.get(i)){

                        File src = new File(fileDirUnsent, listView.getItemAtPosition(i).toString());
                        File dst = new File(fileDirSent, listView.getItemAtPosition(i).toString());
                        String file_path = src.getAbsolutePath();
                        String file_name = file_path.substring(file_path.lastIndexOf("/")+1);
                        if (file_name.contains("single")){
                            prefix = "single";
                        }else{
                            prefix = "multiple";
                        }
                        UploadFile uploadFile = new UploadFile(src, dst, view, prefix);
                        uploadFile.uploadFileToserver();
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
                                source = getContext().getFilesDir().toString() + "/Unsent Files/" + listView.getItemAtPosition(i).toString();

                                Log.e("path pref", source);

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
}
