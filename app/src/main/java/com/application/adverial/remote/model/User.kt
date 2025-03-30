package com.application.adverial.remote.model

data class User(var status: Boolean, var data: UserData)

// User class specific for reviews to avoid conflicts
data class ReviewUser(val id: Int, val name: String, val email: String)
