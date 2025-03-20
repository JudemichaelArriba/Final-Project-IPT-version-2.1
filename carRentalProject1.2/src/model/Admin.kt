package model

class Admin {

    private val adminName: String = "admin"
    private val adminPassword: String = "123"

    fun login(inputEmail: String, inputPassword: String): Boolean {
        return inputEmail == adminName && inputPassword == adminPassword
    }


}