package com.tws.network.module;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.tws.network.api.ApiBase;
import com.tws.network.define.Cache;

import java.io.File;
import java.io.InputStream;

/**
 * Created by jonychoi on 15. 1. 29..
 */
public class TwsVolleyGlideModule implements GlideModule
{
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Do nothing.\

        Log.i("jony", "TwsVolleyGlideModule applyOptions");

        builder.setDiskCache(DiskLruCacheWrapper.get(getDiskCacheDir(context, Cache.DISK_CACHE_PATH), Cache.DISK_CACHE_SIZE));
        builder.setMemoryCache(new LruResourceCache(Cache.MEMORY_CACHE_SIZE));
        builder.setBitmapPool(new LruBitmapPool(Cache.MEMORY_CACHE_SIZE));


    }

    @Override
    public void registerComponents(Context context, Glide glide) {

        Log.i("jony", "TwsVolleyGlideModule registerComponents");

        glide.register(GlideUrl.class, InputStream.class, new VolleyUrlLoader.Factory(ApiBase.getInstance(context).getRequestQueue()));

    }


    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ?
                        getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

}

