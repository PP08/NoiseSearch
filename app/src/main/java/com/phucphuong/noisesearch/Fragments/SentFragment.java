package com.phucphuong.noisesearch.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.FileManagerHelper;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentFragment extends Fragment {


    private ListView listView;
    private Button btn_openFile, btn_deleteFile;

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
//        btn_openFile = (Button)view.findViewById(R.id.btn_open);
        btn_deleteFile = (Button)view.findViewById(R.id.btn_delete);


        directory = getContext().getFilesDir().toString() + "/Sent Files";
        fileManagerHelper = new FileManagerHelper(directory, view);
        fileManagerHelper.refreshFileList();

//        btn_openFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
//
//                for (int i = 0; i < listView.getCount(); i++){
//                    if (sparseBooleanArray.get(i)){
//                        source = getContext().getFilesDir().toString() + "/Sent Files/" + listView.getItemAtPosition(i).toString();
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(source));
//                        intent.setDataAndType(Uri.parse(source), "application/pdf");
//                        getContext().startActivity(intent);
//                    }
//                }
//
//                fileManagerHelper.refreshFileList();
//            }
//        });

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
}
