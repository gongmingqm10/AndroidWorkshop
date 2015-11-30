package com.thoughtworks.workshop.book.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import com.thoughtworks.workshop.book.R;
import com.thoughtworks.workshop.book.util.AppUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import libcore.io.DiskLruCache;

public class ImageLoader {

    private static final int IMAGE_LOAD_SUCCESS = 200;
    private static final int IMAGE_LOAD_ERROR = 400;

    private static ImageLoader instance;

    private static LruCache<String, Bitmap> memoryCache;

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private ImageLoader(Context context) {
        initMemoryCache();
        initDiskCache(context);
    }

    private void initDiskCache(Context context) {
        final File cacheDir = AppUtils.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mDiskCacheLock) {
                    try {
                        mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mDiskCacheStarting = false; // Finished initialization
                    mDiskCacheLock.notifyAll(); // Wake any waiting threads
                }
            }
        }).start();
    }

    private void initMemoryCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static ImageLoader getInstance() {
        try {
            if (instance == null) {
                throw new Exception("ImageLoader.init(Context) should be called in when App starts");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new ImageLoader(context);
        }
    }

    public void loadImage(final ImageView imageView, final String urlString) {

        if (TextUtils.isEmpty(urlString) || imageView == null) return;

        Bitmap memoryBitmap = getBitmap(AppUtils.hashKeyForDisk(urlString));
        if (memoryBitmap == null) {
            loadImageFromURL(imageView, urlString);
        } else {
            imageView.setImageBitmap(memoryBitmap);
        }
    }

    private void loadImageFromURL(final ImageView imageView, final String urlString) {
        final String uniqueKey = AppUtils.hashKeyForDisk(urlString);
        imageView.setTag(uniqueKey);
        final Handler handler = new ImageHandler(imageView);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(urlString);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                    if (imageBitmap == null) {
                        throw new IOException();
                    } else {
                        addBitmap(uniqueKey, imageBitmap);
                    }
                    Message message = new Message();
                    message.what = IMAGE_LOAD_SUCCESS;
                    message.obj = uniqueKey;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(IMAGE_LOAD_ERROR);
                }
            }
        }).start();
    }

    private static class ImageHandler extends Handler {
        private final ImageView imageView;
        public ImageHandler(final ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void handleMessage(Message msg) {
            String key = (String) msg.obj;
            if (msg.what == IMAGE_LOAD_SUCCESS && key.equals(imageView.getTag())) {
                Bitmap targetBitmap = memoryCache.get(key);
                imageView.setImageBitmap(targetBitmap);
            } else if (msg.what == IMAGE_LOAD_ERROR) {
                imageView.setImageResource(R.drawable.ic_default_cover);
            }
        }
    }



    private Bitmap getBitmap(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                }
            }
            if (mDiskLruCache != null) {
                return getBitmapFromDisk(key);
            }
        }
        return null;
    }

    private Bitmap getBitmapFromDisk(String key) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskLruCache.get(key);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream(in, IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return bitmap;

    }

    private void addBitmap(String key, Bitmap bitmap) {
        // Add to memory cache as before
        if (memoryCache.get(key) == null) {
            memoryCache.put(key, bitmap);
        }

        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            try {
                if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                    addCacheToDisk(key, bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCacheToDisk(String key, Bitmap data) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskLruCache.flush();
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (IOException e) {
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            return bitmap.compress(Bitmap.CompressFormat.JPEG, 72, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


}
