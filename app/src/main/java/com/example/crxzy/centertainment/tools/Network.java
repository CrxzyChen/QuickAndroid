package com.example.crxzy.centertainment.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.graphics.BitmapFactory.decodeByteArray;

public class Network {
    public void send(final Request request) {
        new Thread (new Runnable ( ) {
            @Override
            public void run() {
                try {
                    URL url = new URL (request.url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection ( );
                    httpURLConnection.setRequestMethod (request.method);
                    httpURLConnection.connect ( );
                    int status = httpURLConnection.getResponseCode ( );
                    if (status == HttpURLConnection.HTTP_OK) {
                        request.callSuccess (new Response (httpURLConnection));
                    } else {
                        request.callError (new Response (httpURLConnection));
                    }
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
            }
        }).start ( );
    }

    public class Response {
        int code;
        String raw;
        Map <String, List <String>> headers;
        public Object content;


        Response(HttpURLConnection httpURLConnection) {
            try {
                this.code = httpURLConnection.getResponseCode ( );
                this.headers = httpURLConnection.getHeaderFields ( );

                if (this.code == HttpURLConnection.HTTP_OK) {
                    Map <String, String> contentType = getContentType ( );
                    if (Objects.equals (contentType.get ("ext"), "json")) {
                        this.raw = BaseToString (httpURLConnection.getInputStream ( ), contentType);
                        this.content = new JSONArray (this.raw);
                    } else if (Objects.equals (contentType.get ("type"), "image")) {
                        this.content = BitmapFactory.decodeStream (httpURLConnection.getInputStream ( ));
                    } else {
                        this.content = this.raw;
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace ( );
            }
        }


        Map <String, String> getContentType() {
            String contentTypeString = Objects.requireNonNull (this.headers.get ("Content-Type")).get (0);
            Map <String, String> contentType = new HashMap <> ( );
            for (String value : contentTypeString.split (";")) {
                if (value.contains ("/")) {
                    String[] mime = value.split ("/");
                    contentType.put ("type", mime[0]);
                    contentType.put ("ext", mime[1]);
                } else if (value.contains ("=")) {
                    String[] list = value.split ("=");
                    contentType.put (list[0], list[1]);
                }
            }
            return contentType;
        }

        private String BaseToString(InputStream inputStream, Map <String, String> contentType) throws IOException {
            InputStreamReader reader;
            if (null != contentType.get ("charset")) {
                reader = new InputStreamReader (inputStream, contentType.get ("charset"));
            } else {
                reader = new InputStreamReader (inputStream);
            }
            BufferedReader bufferedReader = new BufferedReader (reader);

            StringBuilder buffer = new StringBuilder ( );
            String temp;

            while ((temp = bufferedReader.readLine ( )) != null) {
                buffer.append (temp);
            }
            bufferedReader.close ( );
            reader.close ( );
            inputStream.close ( );
            return buffer.toString ( );
        }
    }

    public class Request {
        String url;
        String method = "GET";
        private Object mInstance;
        private String mSuccess;
        private String mError;

        Request(String url) {
            this.url = url;
        }

        public void setSuccess(Object instance, String success) {
            if (this.mInstance == null) {
                this.mInstance = instance;
            }
            this.mSuccess = success;
        }

        public void setError(Object instance, String error) {
            if (this.mInstance == null) {
                this.mInstance = instance;
            }
            this.mError = error;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        void callSuccess(Response response) {
            if (this.mSuccess != null) {
                try {
                    this.mInstance.getClass ( ).getMethod (this.mSuccess, Response.class).invoke (mInstance, response);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace ( );
                }
            }
        }

        void callError(Response response) {
            if (this.mError != null) {
                try {
                    this.mInstance.getClass ( ).getMethod (this.mError, Response.class).invoke (mInstance, response);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace ( );
                }
            }
        }
    }

    public Request InstanceRequest(String url) {
        return new Request (url);
    }
}