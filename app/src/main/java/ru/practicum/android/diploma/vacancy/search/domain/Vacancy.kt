package ru.practicum.android.diploma.vacancy.search.domain

import ru.practicum.android.diploma.common.data.dto.AreasDto
import ru.practicum.android.diploma.common.data.dto.EmployerDto
import ru.practicum.android.diploma.common.data.dto.SalaryDto

class Vacancy(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val area: AreasDto,
    val employer: EmployerDto
)
