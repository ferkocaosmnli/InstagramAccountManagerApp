# InstaTracker

**InstaTracker**, Java Swing ile geliştirilmiş bir masaüstü uygulamasıdır.  
Kullanıcıların Instagram hesaplarını takip etmelerini, günlük istatistiklerini ve aksiyonlarını yönetmelerini sağlar.

## Özellikler

- **Hesap Bilgileri Yönetimi (`AccountInfo`)**  
  Kullanıcı adı, şifre, e-posta, telefon ve not bilgilerini saklar.

- **Günlük İstatistikler (`AccountDailyStats`)**  
  Gönderi, takipçi artışı/azalışı, takip edilenler, beğeni ve yorum sayısı gibi günlük verileri kaydeder ve raporlar.

- **Genel Hesap İstatistikleri (`AccountStats`)**  
  Toplam gönderi, takipçi ve takip edilen sayıları ile istatistikleri gösterir.

- **Hesap Aksiyonları (`AccountOps`)**  
  Follow/unfollow, like, comment, DM, post, story gibi işlemleri kaydeder; planlanan veya tamamlanan aksiyonları takip eder.

- **Filtreleme ve Görselleştirme**  
  Tarih aralığı, hesap seçimi ve “bugün” filtresi ile tabloları görüntüleyebilirsiniz.

- **Veritabanı ile Entegrasyon**  
  MySQL kullanarak kayıt ekleme, güncelleme ve silme işlemleri yapılır (DAO pattern ile).

## Kullanılan Teknolojiler

- Java 11+  
- Java Swing (GUI)  
- MySQL (veritabanı)  
- JDBC (DB bağlantısı)  
- MVC / DAO tasarım deseni

## Arayüz Görselleri

![Ana Ekran](https://github.com/user-attachments/assets/b5e10880-e254-480f-ba67-956490ffe15d)
![Arayüz2](https://github.com/user-attachments/assets/0a0dbddd-8b88-4833-9743-62234e0fcf05)  
![Arayüz3]([https://github.com/user-attachments/assets/9784df1a-4060-49ee-adf3-e9d44fd106e3](https://github.com/user-attachments/assets/e51ee938-c029-4db9-9942-cc73ce015e9e))  


> Not: Kendi resim linklerinle değiştir.

---
## Kurulum

1. MySQL’de `insta_tracker` veritabanını oluşturun.  
2. `DBConnection` sınıfında kullanıcı adı ve şifreyi kendi veritabanınıza göre düzenleyin.  
3. IDE’de projeyi açın ve `InstaTrackerApp` sınıfını çalıştırın.

## Lisans
- Bu proje Ferhat Kocaosmanli tarafindan geliştirilmiştir.
