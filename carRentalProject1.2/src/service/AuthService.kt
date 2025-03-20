package service

import model.Admin
import model.Cars
import model.RentInfo
import model.Users

class AuthService(
    carService: CarService, carList: MutableList<Cars>, availableCarList: MutableList<Cars>,
    userAccounts: MutableList<Users>, rentalHistory: HashMap<String, MutableList<RentInfo>>,
    notAvailableCarList: MutableList<Cars>, userService: UserService
) {

    private val admin = Admin()
    private val adminService = AdminService(
        carService, carList, availableCarList, notAvailableCarList, userService, rentalHistory, userAccounts
    )

    private val userService = UserService(
        userAccounts, carService, carList,
        availableCarList, rentalHistory, notAvailableCarList
    )

    fun adminLogin() {
        println("Enter the Admin Name: ")
        val adminName = readlnOrNull()?.trim()
        if (adminName.isNullOrEmpty()) {
            println("Invalid admin name, Enter a valid admin name")
            println()
            return adminLogin()
        }

        var adminPassword: String?
        while (true) {
            println("Enter the user password: ")
            adminPassword = readlnOrNull()?.trim()

            if (adminPassword.isNullOrEmpty()) {
                println("Please enter a valid admin password!")
                println()
                continue
            }
            if (admin.login(adminName, adminPassword)) {
                println("Welcome back $adminName!")
                println()
                adminService.adminChoice()

            } else {
                println("Wrong Admin name or password!")
                println()
                return adminLogin()

            }

        }





    }


    fun userLogin(userAccounts: MutableList<Users>) {
        println("Enter Username: ")
        val username = readlnOrNull()?.trim()
        if (username.isNullOrEmpty()) {
            println("Enter a valid username")
            println()
            return userLogin(userAccounts)
        }

        var password: String?
        while (true) {
            println("Enter the user password: ")
            password = readlnOrNull()?.trim()

            if (!password.isNullOrEmpty()) {
                break
            }

            println("Please enter a valid user password!")
            println()
        }



        val findingUser = userAccounts.find { it.getUsername() == username && it.getPassword() == password }
        if (findingUser != null) {
            println("Successfully Logged in!")
            println()
            userService.userChoice(username)

        } else {
            println("Wrong username or password")
            println()
        }
    }

}