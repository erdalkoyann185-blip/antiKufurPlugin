
# 🛡️ IzmAntiKufur

**IzmAntiKufur**, Paper 1.21.11 sunucuları için geliştirilmiş gelişmiş bir anti-küfür pluginidir.  
Config destekli, GUI panelli ve ceza sistemli yapısıyla sohbeti temiz tutmak için tasarlanmıştır.

## ✨ Özellikler

- 🧠 Gelişmiş küfür algılama sistemi
- 🔡 Büyük/küçük harf bypass koruması
- 🔢 Leet yazım algılama: `@`, `4`, `1`, `!`, `0` vb.
- 🧩 Boşluklu ve noktalı yazım denemelerini yakalama
- 🇹🇷 Türkçe karakter normalizasyonu
- ⚙️ Tamamen config üzerinden ayarlanabilir yapı
- 🖥️ Oyun içi yönetim GUI’si
- 🔇 Geçici mute sistemi
- ⚠️ Kademeli uyarı ve ceza sistemi
- 🚪 Kick desteği
- 📢 Broadcast mesajları
- 🧾 Console command çalıştırma desteği
- 🧪 Filtre test komutu
- 🛡️ Yetki ile bypass desteği

## 📦 Desteklenen Sürüm

- Minecraft: **1.21.11**
- Platform: **Paper**
- Java: **21 önerilir**

## 🔧 Komutlar

| Komut | Açıklama |
|---|---|
| `/antikufur gui` | Yönetim panelini açar |
| `/antikufur reload` | Config dosyasını yeniler |
| `/antikufur test <mesaj>` | Bir mesajı filtrede test eder |
| `/antikufur list` | Yasaklı kelime ve whitelist listesini gösterir |
| `/antikufur add <kelime>` | Yasaklı kelime ekler |
| `/antikufur remove <kelime>` | Yasaklı kelime siler |
| `/antikufur stats` | İhlal istatistiklerini gösterir |
| `/antikufur clear <oyuncu>` | Oyuncunun ceza geçmişini temizler |

## 🔐 Yetkiler

| Yetki | Açıklama |
|---|---|
| `izmantikufur.admin` | Admin komutlarını ve GUI’yi kullanır |
| `izmantikufur.bypass` | Küfür filtresinden etkilenmez |

## ⚙️ Config

Plugin ilk çalıştırmada otomatik olarak `config.yml` oluşturur.  
Buradan yasaklı kelimeleri, whitelist listesini, ceza eşiklerini, mute süresini, mesajları ve GUI yazılarını düzenleyebilirsin.

## 🚀 Kurulum

1. Plugin `.jar` dosyasını `plugins` klasörüne at.
2. Sunucuyu başlat.
3. `plugins/IzmAntiKufur/config.yml` dosyasını düzenle.
4. `/antikufur reload` komutuyla ayarları yenile.

## 🧪 Test

Bir mesajı test etmek için:
```bash
/antikufur test mesaj
```
## 📌 Not

OP oyuncular varsayılan olarak bypass almaz.  
Bypass vermek istersen oyuncuya şu yetkiyi tanımlamalısın:
```text
izmantikufur.bypass
```
## 👤 Geliştirici

Developed by **MuroİZM**
