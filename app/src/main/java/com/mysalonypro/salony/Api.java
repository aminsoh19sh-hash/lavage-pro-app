package com.mysalonypro.salony;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** الاتصال بالخادم — نفس مسارات صفحة الهتمل. */
final class Api {
    // ⚙️ عنوان الخادم (غيّر الـ IP أو المنفذ هنا فقط)
    static final String BASE = "http://192.168.43.24:3000/api";

    interface Cb<R> {
        void ok(R r);

        void err(String msg);
    }

    /** خطأ قادم من الخادم (يحمل رسالة d.error). */
    static class ApiError extends Exception {
        ApiError(String m) { super(m); }
    }

    private static final ExecutorService EX = Executors.newFixedThreadPool(3);
    private static final Handler UI = new Handler(Looper.getMainLooper());

    private static String req(String path, String method, JSONObject body) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + path).openConnection();
        try {
            c.setRequestMethod(method);
            c.setConnectTimeout(10000);
            c.setReadTimeout(15000);
            c.setRequestProperty("Accept", "application/json");
            if (body != null) {
                c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                c.setDoOutput(true);
                try (OutputStream os = c.getOutputStream()) {
                    os.write(body.toString().getBytes("UTF-8"));
                }
            }
            int code = c.getResponseCode();
            InputStream is = code >= 400 ? c.getErrorStream() : c.getInputStream();
            String txt = is == null ? "" : read(is);
            if (code < 200 || code >= 300) {
                String m = "Error";
                try { m = new JSONObject(txt).optString("error", "Error"); } catch (Exception ignore) { }
                throw new ApiError(m.isEmpty() ? "Error" : m);
            }
            return txt;
        } finally {
            c.disconnect();
        }
    }

    private static String read(InputStream is) throws Exception {
        try (InputStream in = is; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) > 0) out.write(b, 0, n);
            return out.toString("UTF-8");
        }
    }

    private static <R> void run(final Callable<R> job, final Cb<R> cb) {
        EX.execute(() -> {
            try {
                final R r = job.call();
                UI.post(() -> cb.ok(r));
            } catch (Exception e) {
                final String m = e instanceof ApiError ? e.getMessage() : T.t("fail");
                UI.post(() -> cb.err(m));
            }
        });
    }

    static void shops(Cb<List<Models.Shop>> cb) {
        run(() -> Models.list(req("/shops", "GET", null), Models.Shop::from), cb);
    }

    static void services(String shopId, Cb<List<Models.Service>> cb) {
        run(() -> Models.list(req("/shops/" + Uri.encode(shopId) + "/services", "GET", null), Models.Service::from), cb);
    }

    static void appointments(String dev, Cb<List<Models.Appointment>> cb) {
        run(() -> Models.list(req("/public/appointments?deviceId=" + Uri.encode(dev), "GET", null), Models.Appointment::from), cb);
    }

    static void createAppt(JSONObject body, Cb<Boolean> cb) {
        run(() -> { req("/public/appointments", "POST", body); return Boolean.TRUE; }, cb);
    }

    static void updateAppt(String id, JSONObject body, Cb<Boolean> cb) {
        run(() -> { req("/public/appointments/" + Uri.encode(id), "PUT", body); return Boolean.TRUE; }, cb);
    }

    static void deleteAppt(String id, String dev, Cb<Boolean> cb) {
        run(() -> { req("/public/appointments/" + Uri.encode(id) + "?deviceId=" + Uri.encode(dev), "DELETE", null); return Boolean.TRUE; }, cb);
    }
}
