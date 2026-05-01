package com.tech.tucalle.domain.model

data class User (

    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val celular:  String = "",
    val tipo: String = "", //"USUARIO","HUARIQUE","ti"
    val direcciom: String? = null


)