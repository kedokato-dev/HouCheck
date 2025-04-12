
## HOU CHECK 

Ứng dụng tra cứu lịch học cho sinh viên trường Đại Học Mở Hà Nội trên thiết bị di động.

### Tác giả:
1. **Trần Anh Quân** - Technical Leader  
2. **Trần Trường Giang** - Dev  

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

