package com.example.messengerapp.Model.ModelClasses

/**
 * Clase encargada de definir el esquema del objeto Users en la aplicacion
 * de mensajeria instananea
 *
 * @author Ismael Martinez - Kevin Sanipatin
 * @version 01/08/2020 v1
 */
class Users {

    /**
     * Atributos de la clase Users
     */
    private var uid : String = ""
    private var username : String = ""
    private var profile : String = ""
    private var cover : String = ""
    private var status : String = ""
    private var search : String = ""
    private var facebook : String = ""
    private var instagram : String = ""
    private var website : String = ""

    /**
     * Constructor de la clase Users sin parametros
     */
    constructor()

    /**
     * Constructor de la clase Users con todos los parametros
     */
    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    /**
     * Metodo get del atributo UID
     */
    fun getUID(): String?{
        return uid
    }

    /**
     * Metodo set del atributo UID
     */
    fun setUID(uid : String){
        this.uid = uid
    }

    /**
     * Metodo get del atributo userName
     */
    fun getUserName(): String?{
        return username
    }

    /**
     * Metodo set del atributo userName
     */
    fun setUserName(username : String){
        this.username = username
    }

    /**
     * Metodo get del atributo profile
     */
    fun getProfile(): String?{
        return profile
    }

    /**
     * Metodo set del atributo profile
     */
    fun setProfile(profile : String){
        this.profile = profile
    }

    /**
     * Metodo get del atributo cover
     */
    fun getCover(): String?{
        return cover
    }

    /**
     * Metodo set del atributo cover
     */
    fun setCover(cover : String){
        this.cover = cover
    }

    /**
     * Metodo get del atributo status
     */
    fun getStatus(): String?{
        return status
    }

    /**
     * Metodo set del atributo status
     */
    fun setStatus(status : String){
        this.status = status
    }

    /**
     * Metodo get del atributo search
     */
    fun getSearch(): String?{
        return search
    }

    /**
     * Metodo set del atributo search
     */
    fun setSearch(search : String){
        this.search = search
    }

    /**
     * Metodo get del atributo facebook
     */
    fun getFacebook(): String?{
        return facebook
    }

    /**
     * Metodo set del atributo facebook
     */
    fun setFacebook(facebook : String){
        this.facebook = facebook
    }

    /**
     * Metodo get del atributo instagram
     */
    fun getInstagram(): String?{
        return instagram
    }

    /**
     * Metodo set del atributo instagram
     */
    fun setInstagram(instagram : String){
        this.instagram = instagram
    }

    /**
     * Metodo get del atributo website
     */
    fun getWebsite(): String?{
        return website
    }

    /**
     * Metodo set del atributo website
     */
    fun setWebsite(uid : String){
        this.website = website
    }
}