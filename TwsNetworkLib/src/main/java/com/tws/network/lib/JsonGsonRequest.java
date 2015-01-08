package com.tws.network.lib;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonychoi on 14. 11. 17..
 */
public class JsonGsonRequest<T> extends JsonRequest<T> {

    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> header;
    private final Response.Listener<T> listener;

    public JsonGsonRequest(int method, String url, Class<T> clazz, Map<String, String> header, String params,
                           Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, params, listener,  errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampDeserializer());
        this.gson = gsonBuilder.create();
        this.clazz = clazz;
        this.header = header;
        this.listener = listener;

        setShouldCache(false);

        setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }



    /**
     * Passing some request headers
     * */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();

        // common header
        //headers.put("Content-Type", "application/x-www-form-urlencoded");
        //headers.put("auth_key", "3SRwJYNT72UmBPKJNLpYQVKgnLHn0AYlFQ/fM1JtRij2+oLs4VSapqOqMgr6iC+P");
        //headers.put("source", "WEB");

        // set header
        if (header != null)
            headers.putAll(header);

        return headers != null ? headers : super.getHeaders();
    }


    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
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
