package com.example.messengerapp.Model.ModelClasses

/**
 * Clase encargada de definir el esquema del objeto ChatList en la aplicacion
 * de mensajeria instananea
 *
 * @author Ismael Martinez - Kevin Sanipatin
 * @version 01/08/2020 v1
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