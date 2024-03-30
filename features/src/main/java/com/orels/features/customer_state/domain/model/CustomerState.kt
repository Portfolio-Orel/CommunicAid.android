package com.orels.features.customer_state.domain.model

import com.google.gson.annotations.SerializedName

data class CustomerState(
    @SerializedName("Personal") val personal: PersonalInfo,
    @SerializedName("Finances") val finances: Finances
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
    @SerializedName("Balance") val balance: Int,
    @SerializedName("Revenue") val revenue: Int,
    @SerializedName("OutstandingAccounts") val outstandingAccounts: Map<String, OutstandingAccount>
)

data class OutstandingAccount(
    @SerializedName("Id") val id: Int,
    @SerializedName("balance") val balance: String,
    @SerializedName("title") val title: String
)
