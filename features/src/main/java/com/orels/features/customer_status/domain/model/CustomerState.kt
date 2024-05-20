package com.orels.features.customer_status.domain.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64
import kotlin.text.Charsets.UTF_8

data class CustomerState(
    @SerializedName("Personal") val personal: PersonalInfo,
    @SerializedName("Finances") val finances: Finances,
    @SerializedName("DOB") val dob: String
)

data class PersonalInfo(
    @SerializedName("casual_customer") val casualCustomer: Boolean,
    @SerializedName("personal") val personalDetails: PersonalDetails,
    @SerializedName("insurance") val insurance: List<Insurance>,
    @SerializedName("last_dive") val lastDive: LastDive,
    @SerializedName("licence_copy") val licenceCopy: LicenceCopy,
    @SerializedName("signed_documents") val signedDocuments: List<Int>
)

data class PersonalDetails(
    @SerializedName("age_under_18") val ageUnder18: Boolean,
    @SerializedName("age_over_44") val ageOver44: Boolean,
    @SerializedName("has_medical_issues") val hasMedicalIssues: Boolean,
    @SerializedName("has_privileged_access") val hasPrivilegedAccess: Boolean,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("gender") val gender: String
)

data class Insurance(
    @SerializedName("Id") val id: Int,
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String,
    @SerializedName("approved") val approved: Int,
    @SerializedName("image") val image: String,
    @SerializedName("program") val program: Int,
    @SerializedName("name") val name: String,
    @SerializedName("is_custom") val isCustom: Boolean,
    @SerializedName("provider") val provider: String,
    @SerializedName("created") val created: String,
    @SerializedName("approval_code") val approvalCode: String,
    @SerializedName("is_imported") val isImported: Boolean
)

data class LastDive(
    @SerializedName("valid_until") val validUntil: String,
    @SerializedName("approved") val approved: Boolean,
    @SerializedName("image") val image: Boolean,
    @SerializedName("was_at") val wasAt: String
)

data class LicenceCopy(
    @SerializedName("approved") val approved: Boolean,
    @SerializedName("imageA") val imageA: Boolean,
    @SerializedName("imageB") val imageB: Boolean
)

data class Finances(
    @SerializedName("Balance") val balance: String,
    @SerializedName("Revenue") val revenue: String,
    @SerializedName("OutstandingAccounts") val outstandingAccounts: Map<String, OutstandingAccount>
)

data class OutstandingAccount(
    @SerializedName("Id") val id: Int,
    @SerializedName("balance") val balance: String,
    @SerializedName("title") val title: String
)

fun Finances.isPositive(): Boolean {
    return balance.toFloat().toInt() <= 0
}

fun Finances.isNegative(): Boolean {
    return balance.toFloat().toInt() > 0
}

fun decrypt(encStr: String, key: String): String {
    val keyChars = key.toCharArray()
    val encryptedData: List<Int> = decodeBase64AndParseJson(encStr)
    if (keyChars.isEmpty() || keyChars.size < encryptedData.size) {
        return ""
    }
    val result = mutableListOf<Char>()
    for (i in encryptedData.indices) {
        val decrValue = encryptedData[i] - (keyChars.getOrNull(i)?.toInt() ?: 0)
        result.add(decrValue.toChar())
    }
    return result.joinToString("")
}

fun decodeBase64AndParseJson(base64Str: String): List<Int> {
    val json = String(Base64.getDecoder().decode(base64Str), UTF_8)
    return Gson().fromJson(json, object : TypeToken<List<Int>>() {}.type)
}

val Finances.revenueFormatted: String
    get() {
        return try {
            revenue.toFloat().toInt().toString()
        } catch (e: Exception) {
            0.toString()
        }
    }

val CustomerState.dateOfBirth: String
    get() {
        return decrypt(dob, "ALX0003-12-LAX%")
    }
val CustomerState.age: Int
    get() {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dob = LocalDate.parse(dateOfBirth, formatter)
            val now = LocalDate.now()
            val month = now.monthValue - dob.monthValue
            if (month < 0 || (month == 0 && now.dayOfMonth < dob.dayOfMonth)) {
                return now.year - dob.year - 1
            }
            return now.year - dob.year
        } catch (e: Exception) {
            return 0
        }
    }

val Finances.balanceFormatted: String
    get() {
        val balanceInt = balance.toFloat().toInt()
        val unsignedBalance = if (balanceInt < 0) balanceInt * -1 else balanceInt
        return if (balance.toFloat().toInt() < 0) {
            "$unsignedBalance"
        } else {
            "-$unsignedBalance"
        }
    }

fun LastDive.isValid(): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val now = LocalDate.now().toEpochDay()
    val lastDiveValidDate = LocalDate.parse(validUntil, formatter).toEpochDay()
    return lastDiveValidDate > now
}

val LastDive.wasAtFormatted: String
    get() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(wasAt, formatter)
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }


fun Insurance.isValid(): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val now = LocalDate.now().toEpochDay()
    val insuranceStart = LocalDate.parse(start, formatter).toEpochDay()
    val insuranceEnd = LocalDate.parse(end, formatter).toEpochDay()
    return now in insuranceStart..insuranceEnd
}

val Insurance.endFormatted: String
    get() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(end, formatter)
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }