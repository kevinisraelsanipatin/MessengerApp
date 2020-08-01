package com.example.messengerapp.Model.ModelClasses

/**
 * Clase encargada de definir el esquema del objeto Chart en la aplicacion
 * de mensajeria instananea
 *
 * @author Ismael Martinez - Kevin Sanipatin
 * @version 01/08/2020 v1
 */
class Chat {

    /**
     * Atributos de la clase Chat
     */
    private var sender: String? = ""
    private var message: String? = ""
    private var receiver: String? = ""
    private var isseen: Boolean? = false
    private var url: String? = ""
    private var messageId: String? = ""

    /**
     * Constructor de la clase sin parametros
     */
    constructor(){
    }

    /**
     * Cosntructor de la clase con todos los parametros
     */
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }

    /**
     * Metodo get del atributo sender
     */
    fun getSender(): String? {
        return sender
    }

    /**
     * Metodo set del atributo sender
     */
    fun setSender(sender: String) {
        this.sender = sender
    }

    /**
     * Metodo get del atributo message
     */
    fun getMessage(): String? {
        return message
    }

    /**
     * Metodo set del atributo message
     */
    fun setMessage(message: String) {
        this.message = message
    }

    /**
     * Metodo get del atributo receiver
     */
    fun getReceiver(): String? {
        return receiver
    }

    /**
     * Metodo set del atributo receiver
     */
    fun setReceiver(receiver: String) {
        this.receiver = receiver
    }

    /**
     * Metodo para determinar si el mensaje a sido leido
     */
    fun isIsSeen(): Boolean? {
        return isseen
    }

    /**
     * Metodo set del atributo isseen
     */
    fun setIsSeen(isSeen:Boolean) {
        this.isseen = isSeen
    }

    /**
     * Metodo get del atributo url
     */
    fun getUrl(): String? {
        return url
    }

    /**
     * Metodo set del atributo url
     */
    fun setUrl(url: String) {
        this.url = url
    }

    /**
     * Metodo get del atributo messageId
     */
    fun getMessageId(): String? {
        return messageId
    }

    /**
     * Metodo set del atributo messageId
     */
    fun setMessageId(messageId: String) {
        this.messageId = messageId
    }
}