package com.tws.network.lib;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by jonychoi on 14. 11. 17..
 */
public class AirGsonRequest<T> extends Request<T> {

    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> header;
    private final Response.Listener<T> listener;
    private final String param;

    public AirGsonRequest(int method, String url, Class<T> clazz, Map<String, String> header, String params,
                          Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampDeserializer());
        this.gson = gsonBuilder.create();
        this.clazz = clazz;
        this.header = header;
        this.param = params;
        this.listener = listener;

        setShouldCache(false);

        setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * Passing some request headers
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();

        // common header
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("source", "Android");
        headers.put("device", Build.MODEL);


        // set header
        if (header != null)
            headers.putAll(header);

        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        HashMap<String, String> params = new HashMap<String, String>();

        if (!TextUtils.isEmpty(param)) {
            params.put("request_param", param);
        }

        return params;
    }


    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse
            (NetworkResponse response) {

        String output = "";
        String json = "";

        try {

            String encoding = response.headers.get("Content-Encoding");
            if (encoding != null && encoding.equals("gzip")) {

                GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
                InputStreamReader reader = new InputStreamReader(gStream);
                BufferedReader in = new BufferedReader(reader);

                String read;
                while ((read = in.readLine()) != null) {
                    output += read;
                }

                gStream.close();

                json = output;
            } else {
                json = new String(
                        response.data, HttpHeaderParser.parseCharset(response.headers));
            }

            return Response.success(
                    gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (
                UnsupportedEncodingException e
                )

        {
            return Response.error(new ParseError(e));
        } catch (
                JsonSyntaxException e
                )

        {
            return Response.error(new ParseError(e));

        } catch (
                IOException e
                )

        {
            return Response.error(new ParseError(e));
        }

    }

    public class TimestampDeserializer implements JsonDeserializer<Timestamp> {

        public Timestamp deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
            long time = Long.parseLong(json.getAsString());
            return new Timestamp(time * 1000);
        }
    }


}
