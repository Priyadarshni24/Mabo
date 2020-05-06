package com.advengers.mabo.ServerCall;

import android.util.Base64;
import android.util.Log;

import com.advengers.mabo.Utils.LogUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.advengers.mabo.Cometchat.constants.AppConfig.AppDetails.API_KEY;
import static com.advengers.mabo.Cometchat.constants.AppConfig.AppDetails.APP_ID;
import static com.advengers.mabo.Interfaces.Keys.AUTHPASSWORD;
import static com.advengers.mabo.Interfaces.Keys.AUTHUSERNAME;

public class MyVolleyRequestManager {

    //Request volley time out (Millisec * seconds * minutes)
    private static  int request_time = 1000*60*5;

    public static JsonRequest createJsonRequest(int method, String serverURL, HashMap<Object, Object> payload, Response.Listener responseListener, Response.ErrorListener errorListener) {
        return MyVolleyRequestManager.jsonRequest(method, serverURL, payload, responseListener, errorListener);
    }

    public static JsonRequest jsonRequest(int method, String serverURL, final HashMap<Object, Object> payload, Response.Listener listener, Response.ErrorListener errorListener) {

        Log.e("Servver Url",serverURL);
        if(errorListener == null){
            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   /* if(DeviceBandwidthSampler.getInstance().isSampling()) {
                        DeviceBandwidthSampler.getInstance().stopSampling();
                    }*/
                    Log.e("VolleyError", String.valueOf(error));
                }
            };
        }
        Gson jsonObj = new Gson();
        String jsonRepresentation = jsonObj.toJson(payload);
        JsonRequest jsonRequest = new JsonRequest(method, serverURL, jsonRepresentation, listener, errorListener) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                //DeviceBandwidthSampler.getInstance().stopSampling();
                Object result = null;
                try {
                    String responseBody = new String(response.data,"UTF-8");
                    Log.e("Resposne:",responseBody);
                    Object json = new JSONTokener(responseBody).nextValue();
                    if(json instanceof JSONArray){
                        result = new JSONArray(responseBody);
                    }
                    else if(json instanceof JSONObject) {
                        result = new JSONObject(responseBody);
                    }
                }
                catch (JSONException parseException) {
                    return  Response.error(new VolleyError(parseException.getLocalizedMessage()));
                }
                catch(UnsupportedEncodingException encodingException){
                    return  Response.error(new VolleyError(encodingException.getLocalizedMessage()));
                }
                if(result != null) {
                    return Response.success(result, null);
                }
                return  Response.error(new VolleyError("Unable to determine JSON format"));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("x-api-key","CODEX@123");
                String credentials = AUTHUSERNAME+":"+AUTHPASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;

            }




        };


        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                request_time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return  jsonRequest;
    }


    public static StringRequest createStringRequest(int method, String serverURL, final HashMap<String, String> payload, Response.Listener responseListener, Response.ErrorListener errorListener) {

        Log.e("Server Url",serverURL);


        StringRequest stringRequest = new StringRequest(method, serverURL, responseListener, errorListener)
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                Log.e("Server Response",response.toString());
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {
                    return Response.error(new VolleyError(e.getLocalizedMessage()));

                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("x-api-key","CODEX@123");
                String credentials = AUTHUSERNAME+":"+AUTHPASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;

            }

         /*   public String getBodyContentType()
            {
                return "application/form-data";
            }*/

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
             //   LogUtils.e(payload.toString());
                return payload;
            }
        };



        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                request_time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return  stringRequest;
    }

    public static JsonObjectRequest  getStringRequest(int method, final String serverURL, Response.Listener responseListener, Response.ErrorListener errorListener)
    {
       LogUtils.e(serverURL);

        JsonObjectRequest stringRequest = new JsonObjectRequest(method, serverURL,null, responseListener, errorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-key","CODEX@123");
                String credentials = AUTHUSERNAME+":"+AUTHPASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                params.put("Authorization", auth);
                LogUtils.e(params.toString());
                return params;

            }

        };
        return stringRequest;
    }


    public static JsonObjectRequest  getCometStringRequest(int method, final String serverURL, Response.Listener responseListener, Response.ErrorListener errorListener)
    {
        LogUtils.e(serverURL);

        JsonObjectRequest stringRequest = new JsonObjectRequest(method, serverURL,null, responseListener, errorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("appId",APP_ID);
                params.put("apiKey",API_KEY);

                LogUtils.e(params.toString());
                return params;

            }


        };
        return stringRequest;
    }
    public static StringRequest createCometStringRequest(int method, String serverURL, final HashMap<String, String> payload, Response.Listener responseListener, Response.ErrorListener errorListener) {

        Log.e("Server Url",serverURL);


        StringRequest stringRequest = new StringRequest(method, serverURL, responseListener, errorListener)
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                Log.e("Server Response",response.toString());
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {
                    return Response.error(new VolleyError(e.getLocalizedMessage()));

                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
            //    params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("appId",APP_ID);
                params.put("apiKey",API_KEY);

                LogUtils.e(params.toString());
                return params;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return payload;
            }
        };



        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                request_time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return  stringRequest;
    }
}
