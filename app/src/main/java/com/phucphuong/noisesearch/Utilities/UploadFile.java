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
    public boolean finish, success;

    public UploadFile(File src, File dst, View view, String file_name){

        this.src = src;
        this.dst = dst;
        this.view = view;
        this.listView = (ListView)view.findViewById(R.id.listView);
        this.file_name = file_name;
        this.directory = view.getContext().getFilesDir().toString() + "/Unsent Files";
        this.fileManagerHelper = new FileManagerHelper(directory, view);
        this.success = false;
        this.finish = false;
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
                .url("http://192.168.1.43/upload_"+ file_name + "/")
                .post(request_body)
                .build();
        UploadTask uploadTask = new UploadTask(client, request, file_path);
        uploadTask.execute();
    }


    private void private_upload(){

    }

    private void public_upload(){

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
                try {
                    fileManagerHelper.copy(src, dst);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File delFile = new File(path);
                delFile.delete();
                success = true;
            }else {
                success = false;
            }
            finish = true;

        }
    }
}


/**     TODO: - add private upload file to the server
 *            - if user is not login or private mode is off, upload to the public database
 */
