# VICARE App B2C

Ứng dụng Android cho sản phẩm Y Nhãn, được xây dựng bằng Kotlin và Jetpack Compose. Giao diện và dữ liệu bám theo VICARE-Web-B2C; cấu trúc Compose và trình phát video tham khảo VICARE Manager Mobile.

## Chạy trên Android

1. Mở thư mục `VICARE-APP-B2C` trong Android Studio và đồng bộ Gradle.
2. Chọn cấu hình `app`, sau đó chạy trên emulator hoặc thiết bị Android 8.0 trở lên.
3. Hoặc tạo APK debug bằng `./gradlew assembleDebug`. APK được tạo tại `app/build/outputs/apk/debug/app-debug.apk`.

## Tài khoản

- `admin1` đến `admin5`
- Mật khẩu: `123`

Mỗi tài khoản hiển thị hồ sơ, chỉ số sinh tồn, cảnh báo và camera của một gia đình riêng. Màn hình đăng nhập điền sẵn `admin1` và `123`.

Ứng dụng có dashboard, chỉ số thay đổi nhẹ theo thời gian, camera IMVIA-NIR và ảnh false-color, trung tâm cảnh báo, video sự kiện té ngã, hồ sơ gia đình và cài đặt tài khoản. Dữ liệu đăng nhập và sức khỏe hiện được lưu cục bộ trong ứng dụng; chưa kết nối backend.
