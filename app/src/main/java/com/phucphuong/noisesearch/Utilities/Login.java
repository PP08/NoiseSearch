package com.phucphuong.noisesearch.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phucphuong.noisesearch.Activities.MainActivity;
import com.phucphuong.noisesearch.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by phucphuong on 4/20/17.
 */

public class Login {

    String username, password;
    View view, parentView;
    OkHttpClient client;
    Request request;
    Response response;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;

    public Login(View view, View parentView, AlertDialog alertDialog, String username, String password) {
        this.view = view;
        this.username = username;
        this.password = password;
        this.parentView = parentView;
        this.alertDialog = alertDialog;

    }

    public void loginToServer() {

        Log.e("username and password", String.valueOf(username) + ", " + String.valueOf(password));

        client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://noisesearch.herokuapp.com/api/token-auth/").newBuilder(); // 192.168.1.43
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.1.43/api/token-auth/").newBuilder(); // 192.168.1.43
        urlBuilder.addQueryParameter("username", username);
        urlBuilder.addQueryParameter("password", password);

        String url = urlBuilder.build().toString();

        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
        loginAsyncTask.execute();

    }

    private class LoginAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (username.length() > 0 && password.length() > 0) {
                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    if (responseData.contains("token")) {

                        JSONObject jsonObject = new JSONObject(responseData);
                        String token = jsonObject.getString("token");

//                        String encrypted_token = Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
//                        String encrypted_username = Base64.encodeToString(username.getBytes(), Base64.DEFAULT);

//                        TODO: write token to sharedpreference file
                        SharedPreferences sharedPrefSettings = view.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefSettings.edit();
                        editor.putString("token", token);
                        editor.putString("username", username);
                        editor.apply();

                        return "success";
                    } else {
                        return "notmatch";
                    }
                } else {
                    return "badrequest";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "failed";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            switch (result) {
                case "success": {
                    Toast.makeText(view.getContext(), "login successfully!", Toast.LENGTH_SHORT).show();

                    Button btn_login_logout = (Button) parentView.findViewById(R.id.btn_login);
                    btn_login_logout.setText("Logout (login as " + username + ")");
                    SwitchCompat sw_private = (SwitchCompat) parentView.findViewById(R.id.sw_private);
                    sw_private.setEnabled(true);
                    TextView tv_private = (TextView) parentView.findViewById(R.id.tv_private_mode);
                    tv_private.setEnabled(true);

                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    ((Activity) view.getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    alertDialog.dismiss();
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    break;
                }
                case "notmatch": {
                    Toast.makeText(view.getContext(), "username or password doesn't match!", Toast.LENGTH_SHORT).show();
                    break;
                }
                case "failed": {
                    Toast.makeText(view.getContext(), "cannot connect to the server!", Toast.LENGTH_SHORT).show();
                    break;
                }
                case "badrequest": {
                    Toast.makeText(view.getContext(), "cannot login", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}

//TODO: create a progress dialog when waiting login to the server