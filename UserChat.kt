package com.example.museodelokal

class UserChat {
    var bio: String? = null
    var buyerR: Int? = null
    var email: String? = null
    var fname: String? = null
    var lname: String? = null
    var password: String? = null
    var sellerR: Int? = null
    var username: String? = null
    var uid: String? =null

    constructor(){}

    constructor(bio: String?,buyerR: Int?,email: String?,fname: String?,lname: String?,password: String?,sellerR: Int?,username: String?,uid: String?){
        this.bio = bio
        this.buyerR = buyerR
        this.email = email
        this.fname = fname
        this.lname = lname
        this.password = password
        this.username = username
        this.sellerR = sellerR
        this.uid = uid
    }
}