package com.mysalonypro.salony;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class Models {
    private static String str(JSONObject o, String k) {
        return o.isNull(k) ? "" : o.optString(k, "");
    }

    static class Shop {
        String id, name, city, description, hours, address, phone;
        List<String> features = new ArrayList<>();
        int services;

        static Shop from(JSONObject o) {
            Shop s = new Shop();
            s.id = str(o, "id");
            s.name = str(o, "name");
            s.city = str(o, "city");
            s.description = str(o, "description");
            s.hours = str(o, "hours");
            s.address = str(o, "address");
            s.phone = str(o, "phone");
            JSONArray f = o.optJSONArray("features");
            if (f != null) for (int i = 0; i < f.length(); i++) s.features.add(f.optString(i));
            s.services = o.optInt("services", 0);
            return s;
        }
    }

    static class Service {
        String id, name, category, vehicle, price;

        static Service from(JSONObject o) {
            Service s = new Service();
            s.id = str(o, "id");
            s.name = str(o, "name");
            s.category = str(o, "category");
            s.vehicle = str(o, "vehicle");
            s.price = str(o, "price");
            return s;
        }
    }

    static class Appointment {
        String id, shopName, serviceName, vehicle, date, time, status, price, customerName, phone, notes;

        static Appointment from(JSONObject o) {
            Appointment a = new Appointment();
            a.id = str(o, "id");
            a.shopName = str(o, "shopName");
            a.serviceName = str(o, "serviceName");
            a.vehicle = str(o, "vehicle");
            a.date = str(o, "date");
            a.time = str(o, "time");
            a.status = str(o, "status");
            a.price = str(o, "price");
            a.customerName = str(o, "customerName");
            a.phone = str(o, "phone");
            a.notes = str(o, "notes");
            return a;
        }
    }

    static <X> List<X> list(String json, java.util.function.Function<JSONObject, X> f) throws Exception {
        JSONArray a = new JSONArray(json);
        List<X> l = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) l.add(f.apply(a.getJSONObject(i)));
        return l;
    }
}
