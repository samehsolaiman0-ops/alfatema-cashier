[README.md](https://github.com/user-attachments/files/28820748/README.md)
# الفاطمة نيو — تطبيق الكاشير
### Al-Fatema New — Android POS Cashier App

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-green)
![Android](https://img.shields.io/badge/Android-8.0%2B-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-blue)
![License](https://img.shields.io/badge/license-Private-red)

</div>

---

## 📱 نبذة عن التطبيق

تطبيق أندرويد احترافي لنقاط البيع (POS) مرتبط ببرنامج **الفاطمة نيو للمبيعات** عبر API محلي على الشبكة.  
يتيح للكاشير إجراء عمليات البيع الكاملة من الهاتف أو الجهاز اللوحي.

---

## ✨ المميزات

- 🔍 بحث المنتجات بالباركود أو الاسم
- 🧾 إنشاء فواتير مرتبطة مباشرة بقاعدة البيانات
- ⏸ تعليق الفواتير واستردادها
- 👤 بحث العملاء وربطهم بالفاتورة
- 💳 دعم طرق دفع متعددة (كاش / شبكة / آجل)
- 🖨 طباعة إيصال على طابعة حرارية Bluetooth (58mm)
- 🌙 وضع ليلي (Dark Mode)
- 🌐 دعم العربية والإنجليزية (RTL/LTR)

---

## 🔗 الـ API المستخدم

يتصل التطبيق بالسيرفر المحلي عبر:

| Method | Endpoint | الوصف |
|--------|----------|-------|
| GET | `/api/invoice/newnumber` | رقم فاتورة جديد |
| POST | `/api/invoice/save` | حفظ الفاتورة |
| GET | `/api/product/{barcode}` | جلب منتج بالباركود |
| GET | `/api/product/search?name=` | بحث منتجات |
| GET | `/api/product/categories` | قائمة التصنيفات |
| GET | `/api/customer/all` | كل العملاء |
| GET | `/api/customer/search?q=` | بحث عميل |

---

## ⚙️ الإعداد

### المتطلبات
- Android 8.0 (API 26) أو أعلى
- الاتصال بنفس شبكة WiFi الخاصة بالسيرفر
- تشغيل برنامج **الفاطمة نيو للمبيعات** على السيرفر

### إعداد الاتصال
1. افتح التطبيق → **الإعدادات**
2. أدخل IP السيرفر (مثال: `192.168.1.10:54345`)
3. اضغط **اختبار الاتصال**
4. عند ظهور ✅ التطبيق جاهز للعمل

---

## 🏗️ بناء المشروع

```bash
# استنساخ المستودع
git clone https://github.com/your-username/alfatema-cashier.git

# فتح في Android Studio
# File → Open → اختر المجلد

# بناء APK
./gradlew assembleDebug
```

أو استخدم **GitHub Actions** — كل push يبني APK تلقائياً في تبويب Actions.

---

## 📦 التقنيات المستخدمة

| التقنية | الاستخدام |
|---------|-----------|
| Kotlin | لغة البرمجة الأساسية |
| Jetpack Compose | واجهة المستخدم |
| Material Design 3 | تصميم الواجهة |
| Retrofit2 + OkHttp3 | الاتصال بالـ API |
| Hilt | Dependency Injection |
| DataStore | التخزين المحلي |
| ML Kit | قراءة الباركود |
| MVVM + Repository | هيكل المشروع |

---

## 📁 هيكل المشروع

```
app/
├── data/
│   ├── model/          ← نماذج البيانات
│   ├── api/            ← Retrofit Service
│   └── repository/     ← مستودعات البيانات
├── ui/
│   ├── screens/        ← شاشات التطبيق
│   ├── components/     ← مكونات قابلة لإعادة الاستخدام
│   ├── theme/          ← الألوان والخطوط
│   └── viewmodel/      ← ViewModels
├── di/                 ← Hilt Modules
├── util/               ← أدوات مساعدة
└── MainActivity.kt
```

---

## 🔒 الأمان

- لا يوجد بيانات حساسة مخزنة في الكود
- عنوان السيرفر محفوظ في DataStore فقط
- الاتصال يعمل على الشبكة المحلية فقط (LAN)

---

## 👨‍💻 المطور

**أبو فاطمة** — مطور برنامج الفاطمة نيو للمبيعات  
Cairo, Egypt 🇪🇬

---

## 📄 الترخيص

هذا المشروع خاص ومرتبط ببرنامج الفاطمة نيو للمبيعات.  
جميع الحقوق محفوظة © 2024
