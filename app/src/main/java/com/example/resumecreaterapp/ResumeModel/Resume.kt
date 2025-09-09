package com.example.resumecreaterapp.ResumeModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Resume(
    var id: Int? = null,
    var tempId: Int?,
    var image: String?,
    var name: String?,
    var phone: String?,
    var mail: String?,
    var whatsapp: String?,
    var bio: String?,
    var language: String?,
    var degree: String?,
    var degreeDate: String?,
    var institudeName: String?,
    var cgpa: String?,
    var percentage: String?,
    var companyName: String?,
    var companyRole: String?,
    var companyWork: String?,
    var skills: String?,
    var hobby: String?,
    var techSkills: String?
) : Parcelable