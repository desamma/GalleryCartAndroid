
package com.example.gallerycart.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudinaryUploader {

    public interface UploadCallback {
        void onSuccess(String secureUrl);
        void onError(String errorMessage);
    }

    private final String cloudName;
    private final String uploadPreset;
    private final OkHttpClient client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CloudinaryUploader(String cloudName, String uploadPreset) {
        this.cloudName = cloudName;
        this.uploadPreset = uploadPreset;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void uploadUri(Context ctx, Uri uri, UploadCallback callback) {
        executor.execute(() -> {
            try {
                byte[] bytes = readBytesFromUri(ctx, uri);
                if (bytes == null || bytes.length == 0) {
                    callback.onError("Empty file");
                    return;
                }

                String mime = guessMimeType(ctx.getContentResolver(), uri);
                if (mime == null) mime = "application/octet-stream";

                String url = "https://api.cloudinary.com/v1_1/" + cloudName + "/auto/upload";

                RequestBody fileBody = RequestBody.create(MediaType.parse(mime), bytes);

                MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", "upload", fileBody)
                        .addFormDataPart("upload_preset", uploadPreset);

                RequestBody requestBody = mb.build();
                Request request = new Request.Builder().url(url).post(requestBody).build();

                try (Response resp = client.newCall(request).execute()) {
                    if (!resp.isSuccessful()) {
                        String body = resp.body() != null ? resp.body().string() : "";
                        callback.onError("Upload failed: " + resp.code() + " " + body);
                        return;
                    }

                    String body = resp.body() != null ? resp.body().string() : "";
                    JSONObject j = new JSONObject(body);
                    String secureUrl = j.optString("secure_url", null);
                    if (secureUrl == null || secureUrl.isEmpty()) {
                        callback.onError("Upload succeeded but secure_url not returned");
                    } else {
                        callback.onSuccess(secureUrl);
                    }
                }
            } catch (Exception e) {
                callback.onError("Upload error: " + e.getMessage());
            }
        });
    }

    private static byte[] readBytesFromUri(Context ctx, Uri uri) throws IOException {
        try (InputStream is = ctx.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            if (is == null) return null;
            byte[] tmp = new byte[4096];
            int n;
            while ((n = is.read(tmp)) != -1) {
                buffer.write(tmp, 0, n);
            }
            return buffer.toByteArray();
        }
    }

    private static String guessMimeType(ContentResolver resolver, Uri uri) {
        String type = resolver.getType(uri);
        if (type != null) return type;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension != null && !extension.isEmpty()) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return null;
    }
}
