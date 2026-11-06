package com.vicare.family.data

enum class AlertGroup { VITAL, ACTIVITY }
enum class AlertSeverity { CRITICAL, WARNING, INFO }

data class FamilyVitals(
    val systolic: Int,
    val diastolic: Int,
    val heartRate: Int,
    val spo2: Int,
    val temperature: Double,
    val respiratoryRate: Int,
)

data class FamilyMember(
    val name: String,
    val gender: String,
    val birthDate: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val bloodType: String,
    val conditions: List<String>,
    val medications: List<String>,
    val emergencyContact: String,
    val note: String,
)

data class FamilyAlert(
    val id: String,
    val group: AlertGroup,
    val title: String,
    val value: String,
    val condition: String,
    val occurredAt: String,
    val severity: AlertSeverity,
    val initialResolved: Boolean = false,
    val camera: String? = null,
    val hasVideo: Boolean = false,
)

data class FamilyAccount(
    val id: String,
    val name: String,
    val cameraSubject: String,
    val username: String,
    val password: String,
    val member: FamilyMember,
    val vitals: FamilyVitals,
    val alerts: List<FamilyAlert>,
)

object FamilyData {
    val families = listOf(
        FamilyAccount(
            id = "family-01", name = "Family 01", cameraSubject = "02", username = "admin1", password = "123",
            member = FamilyMember("Phạm Văn An", "Nam", "12/04/1957", 69, 165, 68, "O+", listOf("Thoái hóa khớp gối"), listOf("Vitamin D3 · buổi sáng"), "Phạm Minh Tuấn · Con trai · 0903 100 201", "Duy trì theo dõi định kỳ tại nhà. Dữ liệu sinh tồn đang ổn định."),
            vitals = FamilyVitals(126, 78, 74, 98, 36.6, 17), alerts = emptyList(),
        ),
        FamilyAccount(
            id = "family-02", name = "Family 02", cameraSubject = "01", username = "admin2", password = "123",
            member = FamilyMember("Nguyễn Thị Bình", "Nữ", "08/11/1951", 75, 158, 56, "A+", listOf("Tăng huyết áp", "Rối loạn mỡ máu"), listOf("Amlodipine 5 mg · buổi sáng", "Atorvastatin 10 mg · buổi tối"), "Nguyễn Hoàng Long · Con trai · 0903 100 202", "Huyết áp hiện cao hơn ngưỡng theo dõi. Nên nghỉ ngơi và đo lại theo hướng dẫn của nhân viên y tế."),
            vitals = FamilyVitals(151, 94, 82, 97, 36.7, 18),
            alerts = listOf(
                FamilyAlert("f2-bp-high", AlertGroup.VITAL, "Huyết áp cao", "151/94 mmHg", "Tâm thu ≥ 140 hoặc tâm trương ≥ 90 mmHg", "09:40", AlertSeverity.WARNING),
                FamilyAlert("f2-bp-low-history", AlertGroup.VITAL, "Huyết áp thấp · đã xử lý", "92/58 mmHg", "Tâm thu < 90 hoặc tâm trương < 60 mmHg", "Hôm qua, 18:12", AlertSeverity.INFO, initialResolved = true),
            ),
        ),
        FamilyAccount(
            id = "family-03", name = "Family 03", cameraSubject = "03", username = "admin3", password = "123",
            member = FamilyMember("Trần Minh Châu", "Nam", "24/02/1948", 78, 170, 72, "B+", listOf("Đái tháo đường type 2"), listOf("Metformin 500 mg · theo đơn"), "Trần Thu Hà · Con gái · 0903 100 203", "Có sự kiện phát hiện té ngã trong phòng khách. Hãy kiểm tra tình trạng người thân."),
            vitals = FamilyVitals(132, 80, 79, 97, 36.8, 18),
            alerts = listOf(FamilyAlert("f3-fall", AlertGroup.ACTIVITY, "Phát hiện sự kiện té ngã", "Phòng khách · Camera hồng ngoại", "Phát hiện tư thế nằm bất thường", "09:36", AlertSeverity.CRITICAL, camera = "Camera hồng ngoại · Phòng khách", hasVideo = true)),
        ),
        FamilyAccount(
            id = "family-04", name = "Family 04", cameraSubject = "05", username = "admin4", password = "123",
            member = FamilyMember("Lê Thu Dung", "Nữ", "19/07/1960", 66, 160, 61, "AB+", listOf("Hen phế quản", "Rối loạn nhịp tim"), listOf("Thuốc xịt duy trì · theo đơn", "Bisoprolol 2.5 mg · buổi sáng"), "Lê Quốc Bảo · Chồng · 0903 100 204", "SpO₂ thấp, cần liên hệ người thân và theo dõi sát tình trạng."),
            vitals = FamilyVitals(118, 72, 112, 89, 36.9, 24),
            alerts = listOf(
                FamilyAlert("f4-spo2", AlertGroup.VITAL, "SpO₂ thấp", "89%", "Dưới ngưỡng theo dõi 92%", "09:41", AlertSeverity.CRITICAL),
                FamilyAlert("f4-hr", AlertGroup.VITAL, "Nhịp tim cao", "112 nhịp/phút", "Vượt ngưỡng theo dõi 100 nhịp/phút", "09:39", AlertSeverity.WARNING),
            ),
        ),
        FamilyAccount(
            id = "family-05", name = "Family 05", cameraSubject = "04", username = "admin5", password = "123",
            member = FamilyMember("Hoàng Văn Em", "Nam", "02/09/1944", 82, 168, 79, "O-", listOf("Tăng huyết áp", "Bệnh mạch vành"), listOf("Losartan 50 mg · buổi sáng", "Aspirin 81 mg · theo đơn"), "Hoàng Minh Đức · Con trai · 0903 100 205", "Có cảnh báo huyết áp và sự kiện té ngã trong ngày. Ưu tiên kiểm tra trực tiếp người thân."),
            vitals = FamilyVitals(146, 92, 98, 94, 37.1, 20),
            alerts = listOf(
                FamilyAlert("f5-bp", AlertGroup.VITAL, "Huyết áp cao", "146/92 mmHg", "Tâm thu ≥ 140 hoặc tâm trương ≥ 90 mmHg", "09:38", AlertSeverity.WARNING),
                FamilyAlert("f5-fall", AlertGroup.ACTIVITY, "Phát hiện sự kiện té ngã", "Phòng ngủ · Camera nhiệt", "Phát hiện tư thế nằm bất thường", "09:34", AlertSeverity.CRITICAL, camera = "Camera nhiệt · Phòng ngủ"),
            ),
        ),
    )

    fun familyFor(username: String, password: String): FamilyAccount? =
        families.firstOrNull { it.username == username && it.password == password }
}
