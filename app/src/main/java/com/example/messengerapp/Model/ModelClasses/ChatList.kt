package com.example.messengerapp.Model.ModelClasses

/**
 * Clase [ChatList] donde se define la lista de Chats segun Id
 */
class ChatList {

    /**
     * Atributo que especifica el Id de los Chats
     */
    private var id:String =""

    /**
     * Constructor de la clase ChatList sin parametros
     */
    constructor(){
    }

    /**
     * Constructor de la clase ChatList con parametro id
     * parameter [id] corresponde a la identificacion del chat
     */
    constructor(id: String) {
        this.id = id
    }

    /**
     * Metodo get del atributo Id
     */
    fun getId(): String? {
        return id
    }

    /**
     * Metodo set del atributo Id
     */
    fun setId(id: String) {
        this.id = id
    }
}