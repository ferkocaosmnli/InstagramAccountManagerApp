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

## Kurulum

1. MySQL’de `insta_tracker` veritabanını oluşturun.  
2. `DBConnection` sınıfında kullanıcı adı ve şifreyi kendi veritabanınıza göre düzenleyin.  
3. IDE’de projeyi açın ve `InstaTrackerApp` sınıfını çalıştırın.

## Lisans
- Bu proje Ferhat Kocaosmanli tarafindan geliştirilmiştir.
