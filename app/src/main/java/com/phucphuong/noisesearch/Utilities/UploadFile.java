package com.phucphuong.noisesearch.Utilities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import okhttp3.FormBody;
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

        SharedPreferences sharedPrefSettings = view.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String token = sharedPrefSettings.getString("token", "");
        if (token.length() != 0){
            token = "token " + token;
        }
        boolean private_mode = sharedPrefSettings.getBoolean("private_mode", false);

        RequestBody file_body = RequestBody.create(MediaType.parse(content_type), src);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                .build();

        String url;

        if (private_mode){
            url = "https://noisesearch.herokuapp.com/api/private_" + file_name + "/"; //192.168.1.43:80/
//            url = "http://192.168.1.43/api/private_" + file_name + "/"; //192.168.1.43:80/
        }
        else {
            url = "https://noisesearch.herokuapp.com/api/public_" + file_name + "/"; //192.168.1.43:80/
//            url = "http://192.168.1.43/api/public_" + file_name + "/"; //192.168.1.43:80/
        }

        upload_task(url, requestBody, token, file_path);
    }

    private void upload_task(String url, RequestBody requestBody, String token, String file_path){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .post(requestBody)
                .build();
        UploadTask uploadTask = new UploadTask(client, request, file_path);
        uploadTask.execute();
    }

    private String getMineType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    private class UploadTask extends AsyncTask<Void, Void, Void> {
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
        protected Void doInBackground(Void... params) {
            try {
                response = client.newCall(request).execute();

                Log.e("sanity check", Boolean.toString(response.isSuccessful()));

                if (response.isSuccessful()){

                    Log.e("response", response.body().toString());

                    if (response.body().string().contains("uploaded_at")){
                        success = true;
                    }
                    else {
                        success = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (success){
                try {
                    fileManagerHelper.copy(src, dst);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File delFile = new File(path);
                delFile.delete();
            }
            finish = true;
        }
    }
}


/**     TODO: - add private upload file to the server
 *            - if user is not login or private mode is off, upload to the public database
 *            - rewrite asyncTask when upload (handle response statuses like BADREQUEST, etc..)
 */
