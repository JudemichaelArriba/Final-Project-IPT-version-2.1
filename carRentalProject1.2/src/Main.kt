import model.Cars
import model.RentInfo
import model.Users

import service.AuthService
import service.CarService
import service.UserService
import kotlin.system.exitProcess


fun main() {
    val carService = CarService()
    val rentalHistory = HashMap<String, MutableList<RentInfo>>()
    val carList = mutableListOf<Cars>()

    val availableCarList = mutableListOf<Cars>()
    val notAvailableCarList = mutableListOf<Cars>()
    val userAccounts = mutableListOf<Users>()

    val userService = UserService(
        userAccounts, carService, carList, availableCarList, rentalHistory, notAvailableCarList
    )
    var choice: Int?
    var choice2: Int?
    val authService = AuthService(
        carService, carList, availableCarList, userAccounts, rentalHistory, notAvailableCarList, userService
    )




    carList.addAll(carService.loadAllCarsFromFile())
    availableCarList.addAll(carService.loadAvailableCarsFromFile())
    notAvailableCarList.addAll(carService.loadNotAvailableCarsFromFile())
    userAccounts.addAll(userService.loadUsersFromFile())
    userService.loadRentalHistoryFromFile(rentalHistory, carList)
    println("Welcome to the CarRental Hub!")
    while (true) {
        println(
            """
        1. Admin
        2. Customer
        3. Exit
    """.trimIndent()
        )
        print("Enter a Number: ")
        val enter = readlnOrNull()
        choice = enter?.toIntOrNull()

        if (choice != null) {
            when (choice) {
                1 -> authService.adminLogin()

                2 -> {
                    while (true) {
                        println(
                            """
                        1. Login
                        2. Signup
                        3. Return
                    """.trimIndent()
                        )
                        print("Enter a Number: ")
                        val enter1 = readlnOrNull()
                        choice2 = enter1?.toIntOrNull()

                        when (choice2) {
                            1 -> authService.userLogin(userAccounts)
                            2 -> userService.userSignup()
                            3 -> break
                            else -> {
                                println("Invalid input! Please enter a number between 1 and 3.")
                                println()
                                continue
                            }
                        }
                    }
                }

                3 -> {
                    var select: Int?
                    while (true) {
                        println(
                            """
                            Do you really want to Exit?
                            
                            1. Yes
                            2. No
                            Select a number: 
                        """.trimIndent()
                        )
                        select = readln().toIntOrNull()

                        if (select != null) {

                            when (select) {
                                1 -> {
                                    println("Successfully Exited!")
                                    println()
                                    exitProcess(0)
                                }

                                2 -> {
                                    println("Cancelled!")
                                    println()
                                    break
                                }

                                else -> {
                                    println("Invalid number, Please enter a valid number")
                                }
                            }


                        }

                        println("Invalid number, Please enter a valid number")
                        println()


                    }

                }

                else -> {
                    println("Invalid input! Please enter a number from 1 to 3.")
                    println()
                }
            }
        } else {
            println("Invalid input! Please enter a valid number.")
            println()
        }
    }

}