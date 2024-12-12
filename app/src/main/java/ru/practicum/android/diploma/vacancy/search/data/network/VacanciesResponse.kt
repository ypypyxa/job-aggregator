package ru.practicum.android.diploma.vacancy.search.data.network


import com.google.gson.annotations.SerializedName

data class VacanciesResponse(
    @SerializedName("items")
    val items: List<Vacancy>,
    @SerializedName("found")
    val total: Int,
    @SerializedName("pages")
    val pages: Int,
    @SerializedName("per_page")
    val perPage: Int
)

data class Vacancy(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("area")
    val area: Area,
    @SerializedName("salary")
    val salary: Salary?,
    @SerializedName("employer")
    val employer: Employer,
    @SerializedName("contacts")
val contacts: Contacts?

)

data class Area(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

data class Salary(
    @SerializedName("from")
    val from: Int?,
    @SerializedName("to")
    val to: Int?,
    @SerializedName("currency")
    val currency: String
)

data class Employer(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

data class Contacts(
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("email")
    val email: String?
)
