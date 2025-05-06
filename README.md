# MyHOU

<p align="center">
  <img src="https://cdn-img.upanhlaylink.com/img/image_20250506bd849f0af16151ee2d59fc69a2064438.jpg" alt="MyHOU" width="120"/>
</p>


## 📱 Giới thiệu

MyHOU là ứng dụng di động dành cho sinh viên trường Đại Học Mở Hà Nội giúp tra cứu lịch học một cách nhanh chóng và thuận tiện. Được phát triển hoàn toàn bằng Kotlin, ứng dụng cung cấp trải nghiệm người dùng mượt mà và thân thiện trên các thiết bị Android.

### Tác giả:
1. **Trần Anh Quân** - FullStack Dev  
2. **Trần Trường Giang** - Mobile Dev  

---

### Tổng quan hệ thống

```plaintext
📁 your_app/
│
├── 📁 data/                         # Tầng dữ liệu
│   ├── 📁 remote/                   # Retrofit API (network)
│   │   ├── ApiService.kt
│   │   └── RetrofitInstance.kt
│   │
│   ├── 📁 local/                    # Room database
│   │   ├── AppDatabase.kt
│   │   └── dao/
│   │       └── YourDao.kt
│   │
│   ├── 📁 repository/               # Kết nối giữa data và domain
│   │   └── YourRepositoryImpl.kt
│   │
│   └── 📁 model/                    # Model dùng cho network/db
│       └── YourDto.kt
│
├── 📁 domain/                       # Tầng logic nghiệp vụ
│   ├── 📁 model/                    # Domain model
│   │   └── YourModel.kt
│   ├── 📁 repository/               # Interface của repository
│   │   └── YourRepository.kt
│   └── 📁 usecase/                  # Các use case (tác vụ chính)
│       └── GetSomethingUseCase.kt
│
├── 📁 presentation/                 # Tầng UI
│   ├── 📁 screen/                   # Mỗi màn hình là 1 package
│   │   └── home/
│   │       ├── HomeScreen.kt        # Compose UI
│   │       ├── HomeViewModel.kt     # ViewModel
│   │       └── HomeUiState.kt       # State class (sealed class / data class)
│   │
│   ├── 📁 components/               # Các thành phần UI tái sử dụng
│   │   └── CustomButton.kt
│   └── 📁 navigation/               # Điều hướng
│       └── NavGraph.kt
│
├── 📁 di/                           # Dependency Injection (Hilt)
│   └── AppModule.kt
│
├── 📁 utils/                        # Tiện ích, extension functions
│   └── NetworkUtils.kt
│
├── MainActivity.kt
└── YourApp.kt                       # Application class (nếu cần Hilt)
```


## 🌐 Backend Server 🖧
Do website chính thức của trường Đại học Mở Hà Nội không cung cấp sẵn API để lấy dữ liệu trực tiếp nên Server `SinhVienHOUAPI` được ra mắt nhằm thu thập dữ liệu từ Cổng sinh viên rồi trả về cho ứng dụng MyHOU.

Mã nguồn của Server có thể tìm thấy tại đây: [SinhVienHOUAPI](https://github.com/kedokato-dev/SinhVienHouAPI)


## ✨ Tính năng chính

- 🔍 Tra cứu lịch học và lịch thi của sinh viên
- 📅 Xem lịch học theo ngày, tuần, tháng
- 📊 Thống kê điểm học tập và tín chỉ tích lũy của sinh viên
- 💡 Xem điểm rèn luyện của sinh viên
- 📝 Lưu trữ thông tin môn học
- 📩 Gửi feedback đến đội ngũ phát triển nhằm cải tiến ứng dụng
- 🌙 Hỗ trợ chế độ tối (Dark mode)
- 📱 Giao diện thân thiện, dễ sử dụng

## 📋 Yêu cầu hệ thống

- Android 7.0 (Nougat) trở lên
- Kết nối Internet để cập nhật dữ liệu

## 📲 Cài đặt

1. Tải xuống ứng dụng từ [Releases](https://github.com/kedokato-dev/HouCheck/releases) của repository này.
2. Cài đặt ứng dụng vào thiết bị.
3. Đăng nhập bằng tài khoản sinh viên của bạn.

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ**: Kotlin
- **Framework**: Android SDK
- **Kiến trúc**: MVVM (Model-View-ViewModel)
- **Database**: Room
- **Network**: Retrofit
- **Image**: Coil
- **UI Components**: Jetpack Compose
## 📱 Ảnh chụp màn hình

### Giao diện chính
| Home | Thời khóa biểu | Lịch thi |
|------|----------------|----------|
| <img src="https://cdn-img.upanhlaylink.com/img/image_2025050636ad56128d081619de329ece18137545.jpg" alt="Home" width="200"/> | <img src="https://cdn-img.upanhlaylink.com/img/image_20250506753cab7d440fab1ca6fe8690111c133b.jpg" alt="Thời khóa biểu" width="200"/> | <img src="https://cdn-img.upanhlaylink.com/img/image_202505061bebb28a241ae02ca45ac4fcf3732e4a.jpg" alt="Lịch học" width="200"/> |

### Thống kê và danh sách
| Thống kê điểm | Danh sách điểm học phần | Điểm rèn luyện |
|----------|---------------|-------------------------|
| <img src="https://cdn-img.upanhlaylink.com/img/image_20250506b6008802824964599ecff25c279ede37.jpg" alt="Lịch thi" width="200"/> | <img src="https://cdn-img.upanhlaylink.com/img/image_202505064af6b23db04d04c31ec9315cf33f5f7f.jpg" alt="Thống kê điểm" width="200"/> | <img src="https://cdn-img.upanhlaylink.com/img/image_20250506f7e159fc6a52d59c87f7c653d4847ebd.jpg" alt="Danh sách điểm học phần" width="200"/> |

### Khác
| Đăng nhập | Thông tin sinh viên | Cài đặt |
|-----------------|---------------------|---------|
| <img src="https://cdn-img.upanhlaylink.com/img/image_20250506bd51bf155e7e513914d07cf62d1a764b.jpg" alt="Đăng nhập" width="200"/> | <img src="https://cdn-img.upanhlaylink.com/img/image_20250506ed912a6d2cfb7adbdf20280d2aab2b7b.jpg" alt="Thông tin sinh viên" width="200"/> | <img src="https://cdn-img.upanhlaylink.com/img/image_20250506efdaa47cf38453a3e1fae87b2fd33232.jpg" alt="Cài đặt" width="200"/> |



## 🚀 Hướng dẫn phát triển

### Yêu cầu

- Android Studio Arctic Fox trở lên
- JDK 11 trở lên
- Gradle 7.0.2 trở lên

### Các bước cài đặt

1. Clone repository này:
```
git clone https://github.com/kedokato-dev/HouCheck.git
```

2. Mở project trong Android Studio

3. Sync Gradle và chạy ứng dụng

## 🤝 Đóng góp

Đóng góp luôn được chào đón! Nếu bạn muốn đóng góp vào dự án, vui lòng:

1. Fork repository
2. Tạo nhánh mới (`git checkout -b feature/amazing-feature`)
3. Commit thay đổi của bạn (`git commit -m 'Add some amazing feature'`)
4. Push lên nhánh (`git push origin feature/amazing-feature`)
5. Mở Pull Request

## 📄 Giấy phép

Dự án này được cấp phép theo giấy phép MIT - xem file [LICENSE](LICENSE) để biết thêm chi tiết.

## 👨‍💻 Tác giả

- **kedokato-dev** - [GitHub](https://github.com/kedokato-dev)

## 📞 Liên hệ

Nếu có bất kỳ câu hỏi hoặc góp ý nào, vui lòng liên hệ qua:
- Email: [thocodeanhquan@gmail.com](mailto:your-email@example.com)
- GitHub Issues: [https://github.com/kedokato-dev/HouCheck/issues](https://github.com/kedokato-dev/HouCheck/issues)

---

<p align="center">Developed with ❤️ for students of Hanoi Open University</p>
