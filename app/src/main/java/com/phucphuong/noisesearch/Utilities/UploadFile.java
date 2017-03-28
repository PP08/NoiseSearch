package com.phucphuong.noisesearch.Utilities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;

import com.phucphuong.noisesearch.Activities.FileManager;
import com.phucphuong.noisesearch.Fragments.UnsentFragment;
import com.phucphuong.noisesearch.R;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by phucphuong on 3/27/17.
 */

public class UploadFile {

    File src, dst;
    View view;
    ListView listView;
    String file_name;
    String directory;
    FileManagerHelper fileManagerHelper;
    final ProgressDialog[] progressDialog = new ProgressDialog[1];

    public UploadFile(File src, File dst, View view, String file_name){

        this.src = src;
        this.dst = dst;
        this.view = view;
        this.listView = (ListView)view.findViewById(R.id.listView);
        this.file_name = file_name;

        this.directory = view.getContext().getFilesDir().toString() + "/Unsent Files";
        this.fileManagerHelper = new FileManagerHelper(directory, view);

    }

    public void uploadFileToserver(){
        String content_type = getMineType(src.getPath());
        String file_path = src.getAbsolutePath();
        OkHttpClient client = new OkHttpClient();
        RequestBody file_body = RequestBody.create(MediaType.parse(content_type), src);

        RequestBody request_body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", content_type)
                .addFormDataPart(file_name,file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.43/upload/")
                .post(request_body)
                .build();
        UploadTask uploadTask = new UploadTask(client, request, file_path);
        uploadTask.execute();
    }

    private String getMineType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    private class UploadTask extends AsyncTask<Void, Void, Boolean> {
        OkHttpClient client;
        Request request;
        Response response;
        String path;

        private UploadTask(OkHttpClient client, Request request, String path){
            this.client = client;
            this.request = request;
            this.path = path;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog[0] = new ProgressDialog(view.getContext());
            progressDialog[0].setTitle("Uploading");
            progressDialog[0].setMessage("Please wait...");
            progressDialog[0].show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){

                if (listView != null){
                    fileManagerHelper.refreshFileList();
                }
                try {
                    fileManagerHelper.copy(src, dst);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File delFile = new File(path);
                delFile.delete();
                progressDialog[0].dismiss();
            }else {
                progressDialog[0].dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Cannot upload files");
                alert.setMessage("Cannot connect to the server at this moment, please try again next time");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        }
    }
}



//TODO: change url when upload 2 types log