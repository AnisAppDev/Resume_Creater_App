package com.example.resumecreaterapp.ResumeModel

data class ResumeData(
    var id: String? = null,
    var tempId: Int? = null,
    var image: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var mail: String? = null,
    var whatsapp: String? = null,
    var bio: String? = null,
    var language: String? = null,
    var degree: String? = null,
    var degreeDate: String? = null,
    var institudeName: String? = null,
    var cgpa: String? = null,
    var percentage: String? = null,
    var companyName: String? = null,
    var companyRole: String? = null,
    var companyWork: String? = null,
    var skills: String? = null,
    var hobby: String? = null,
    var techSkills: String? = null
)
//{
//    // 🔑 Empty constructor required by Firebase
//    constructor() : this(
//        id = null,
//        tempId = null,
//        image = null,
//        name = null,
//        phone = null,
//        mail = null,
//        whatsapp = null,
//        bio = null,
//        language = null,
//        degree = null,
//        degreeDate = null,
//        institudeName = null,
//        cgpa = null,
//        percentage = null,
//        companyName = null,
//        companyRole = null,
//        companyWork = null,
//        skills = null,
//        hobby = null,
//        techSkills = null
//    )
//}