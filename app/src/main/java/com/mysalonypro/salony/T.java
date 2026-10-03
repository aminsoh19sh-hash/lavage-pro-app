package com.mysalonypro.salony;

import java.util.HashMap;
import java.util.Map;

/** كل النصوص (عربي / English) — نفس قاموس L في صفحة الهتمل. */
final class T {
    static String lang = "ar";

    static boolean isAr() { return "ar".equals(lang); }

    private static final Map<String, String> AR = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();

    private static void a(String k, String ar, String en) { AR.put(k, ar); EN.put(k, en); }

    static String t(String k) {
        String v = (isAr() ? AR : EN).get(k);
        return v != null ? v : k;
    }

    static String svcN(int n) {
        return isAr() ? n + " خدمة" : n + (n == 1 ? " service" : " services");
    }

    static {
        a("b1", "لافاش", "Lavage");
        a("b2", "برو", "Pro");
        a("tag", "احجز لمعان سيارتك", "Book your perfect shine");
        a("home", "الرئيسية", "Home");
        a("mine", "مواعيدي", "My bookings");
        a("more", "المزيد", "More");
        a("about", "حول التطبيق", "About the app");
        a("privacy", "سياسة الخصوصية", "Privacy policy");
        a("lang", "اللغة", "Language");
        a("dark", "الوضع الداكن", "Dark mode");
        a("light", "الوضع الفاتح", "Light mode");
        a("heroT", "سيارتك تستحق لمعانا يليق بها", "Give your ride the shine it deserves");
        a("heroS", "اختر محلك المفضل، حدّد الخدمة، واحجز موعدك في أقل من دقيقة.", "Pick your favorite shop, choose a service, and book in under a minute.");
        a("search", "ابحث عن محل أو مدينة", "Search shop or city");
        a("s1", "اختر المحل", "Pick a shop");
        a("s2", "حدّد الخدمة", "Choose service");
        a("s3", "أكّد الموعد", "Confirm");
        a("shops", "المحلات المتاحة", "Available shops");
        a("nores", "لا توجد محلات مطابقة", "No matching shops");
        a("fail", "تعذر الاتصال بالخادم. تأكد من تشغيله.", "Cannot reach the server. Make sure it is running.");
        a("book", "احجز الآن", "Book now");
        a("bookT", "حجز موعد", "Book appointment");
        a("editT", "تعديل الموعد", "Edit appointment");
        a("vehicle", "نوع المركبة", "Vehicle type");
        a("service", "الخدمة", "Service");
        a("allv", "كل المركبات", "All vehicles");
        a("day", "اليوم", "Day");
        a("time", "الوقت", "Time");
        a("yourInfo", "بياناتك", "Your details");
        a("name", "الاسم", "Name");
        a("phone", "رقم الهاتف", "Phone number");
        a("notes", "ملاحظات (اختياري)", "Notes (optional)");
        a("total", "الإجمالي", "Total");
        a("mad", "د.م", "MAD");
        a("confirm", "تأكيد الحجز", "Confirm booking");
        a("saveEdit", "حفظ التعديل", "Save changes");
        a("nosvc", "لا توجد خدمات في هذا المحل بعد.", "This shop has no services yet.");
        a("needs", "أكمل الاسم والهاتف، واختر الخدمة والوقت", "Fill in name and phone, and pick a service and time");
        a("sent", "تم إرسال طلبك للمحل ✔", "Your request was sent to the shop ✔");
        a("saved", "تم حفظ التعديل ✔", "Changes saved ✔");
        a("deleted", "تم حذف الموعد", "Appointment deleted");
        a("mineT", "مواعيدي", "My bookings");
        a("noap", "لا توجد مواعيد بعد", "No bookings yet");
        a("noapS", "اختر محلا واحجز موعدك الأول.", "Pick a shop and book your first appointment.");
        a("browse", "تصفح المحلات", "Browse shops");
        a("delQ", "هل تريد حذف هذا الموعد؟", "Delete this appointment?");
        a("editNote", "بعد التعديل يعود الموعد إلى حالة «بانتظار التأكيد».", "After editing, the booking returns to \"Awaiting confirmation\".");
        a("st_pending", "بانتظار التأكيد", "Awaiting confirmation");
        a("st_confirmed", "مؤكد", "Confirmed");
        a("st_done", "منتهي", "Completed");
        a("st_cancelled", "ملغي", "Cancelled");
        a("oss", "مفتوح المصدر · MIT", "Open source · MIT");
        a("aboutP", "لافاش برو تطبيق يربطك بمحلات غسيل وتنظيف وصيانة المركبات. تحجز موعدك من هاتفك، وتتابع حالته، وتعدّله أو تحذفه متى شئت.",
                "LavagePro connects you with vehicle wash, cleaning and service shops. Book from your phone, track the status, and edit or delete anytime.");
        a("privT", "سياسة الخصوصية", "Privacy policy");
        a("privU", "آخر تحديث: أكتوبر 2026", "Last updated: October 2026");
        a("yes", "نعم", "Delete");
        a("no", "إلغاء", "Cancel");
    }

    static String[] aboutL() {
        return isAr()
                ? new String[]{"حجز في أقل من دقيقة", "سيارات ودراجات نارية وغيرها", "بدون ايميل ولا كلمة مرور", "مفتوح المصدر برخصة MIT"}
                : new String[]{"Book in under a minute", "Cars, motorcycles and more", "No email, no password", "Open source under the MIT license"};
    }

    static String[][] priv() {
        if (isAr()) return new String[][]{
                {"ما الذي نجمعه", "الاسم ورقم الهاتف ونوع المركبة وتفاصيل الموعد التي تدخلها بنفسك عند الحجز. لا نطلب الايميل ولا موقعك الجغرافي."},
                {"كيف نستخدمها", "تُرسل بيانات الحجز إلى المحل الذي اخترته فقط، ليؤكد موعدك ويتواصل معك عند الحاجة."},
                {"رمز الجهاز", "نحفظ في جهازك رمزا عشوائيا يتيح لك رؤية مواعيدك وتعديلها. اذا مسحت بيانات التطبيق أو غيّرت الجهاز فلن ترى مواعيدك القديمة."},
                {"حذف بياناتك", "حذف الموعد من صفحة «مواعيدي» يزيله من النظام. أما بطاقة الزبون (الاسم والهاتف) فتبقى لدى المحل، ويمكنك طلب حذفها منه مباشرة."},
                {"المشاركة", "لا نبيع بياناتك ولا نعرض اعلانات، ولا نشاركها مع أي طرف غير المحل الذي حجزت عنده."}};
        return new String[][]{
                {"What we collect", "The name, phone number, vehicle type and appointment details you enter when booking. We do not ask for your email or location."},
                {"How we use it", "Booking data is sent only to the shop you chose, so it can confirm your appointment and contact you if needed."},
                {"Device ID", "A random ID is stored on your device so you can see and edit your bookings. If you clear app data or switch devices, you will not see old bookings."},
                {"Deleting your data", "Deleting a booking from \"My bookings\" removes it from the system. The shop keeps your customer card (name and phone), and you can ask the shop to delete it."},
                {"Sharing", "We do not sell your data or show ads, and we share it with nobody except the shop you booked with."}};
    }

    // ----- المركبات والفئات (القيم العربية هي ما يرسله الخادم) -----
    static final String[] VEH = {"سيارة", "دراجة نارية", "شاحنة", "حافلة"};

    static int vehIcon(String v) {
        switch (v) {
            case "دراجة نارية": return R.drawable.ic_moto;
            case "شاحنة": return R.drawable.ic_truck;
            case "حافلة": return R.drawable.ic_bus;
            default: return R.drawable.ic_car;
        }
    }

    private static String pick(String ar, String en) { return isAr() ? ar : en; }

    static String vl(String v) {
        switch (v) {
            case "سيارة": return pick("سيارة", "Car");
            case "دراجة نارية": return pick("دراجة", "Moto");
            case "شاحنة": return pick("شاحنة", "Truck");
            case "حافلة": return pick("حافلة", "Bus");
            default: return v;
        }
    }

    static String cl(String c) {
        switch (c) {
            case "غسيل": return pick("غسيل", "Washing");
            case "تنظيف داخلي": return pick("تنظيف داخلي", "Interior cleaning");
            case "تلميع": return pick("تلميع", "Polishing");
            case "ميكانيك": return pick("ميكانيك", "Mechanics");
            case "تغيير الزيت": return pick("تغيير الزيت", "Oil change");
            case "أخرى": return pick("أخرى", "Other");
            default: return c;
        }
    }
}
