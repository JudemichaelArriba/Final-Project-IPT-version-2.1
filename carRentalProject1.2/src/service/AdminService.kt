package service

import main
import model.Cars
import model.RentInfo
import model.Users


class AdminService(
    private val carService: CarService, private val carList: MutableList<Cars>,
    private val availableCarList: MutableList<Cars>, private val notAvailableCarList: MutableList<Cars>,
    private val userService: UserService, private val rentalHistory: HashMap<String, MutableList<RentInfo>>,
    private val userAccounts: MutableList<Users>
) {

    private var choice: Int? = null


    fun adminChoice() {

        while (true) {
            println(
                """
                    1. Add Car
                    2. Delete Car
                    3. Show all Cars
                    4. Show all Available Cars
                    5. Show all Not Available Cars
                    6. Show Customers
                    7. Clear all Cars
                    8. Return Car
                    9. Log out
                """.trimIndent()
            )
            println("Enter a number: ")
            val enter = readlnOrNull()
            choice = enter?.toIntOrNull()
            if (choice == null || choice !in 1..9) {
                println("Invalid input! Please enter a number between 1 and 8.")
                println()
                continue
            } else {

                when (choice) {
                    1 -> {
                        carService.addCar(carList, availableCarList)
                    }

                    2 -> {
                        carService.deleteCar(carList, availableCarList, notAvailableCarList)

                    }

                    3 -> {
                        carService.showAllCars(carList)

                    }

                    4 -> {

                        carService.showAvailableCars(availableCarList)
                    }

                    5 -> {

                        carService.showNotAvailableCar(notAvailableCarList)
                    }

                    6 -> {

                        userService.showUser()
                    }

                    7 -> {
                        carService.clear(availableCarList, carList, notAvailableCarList)

                    }

                    8 -> {
                        userService.showUser()
                        carService.returnAdmin(
                            carList,
                            notAvailableCarList,
                            rentalHistory,
                            availableCarList,
                            userAccounts
                        )

                    }

                    9 -> {
                        var select: Int?
                        while (true) {

                            println(
                                """
                            Do you really want to logged out?
                            
                            1. Yes
                            2. No
                            Select a number: 
                        """.trimIndent()
                            )
                            select = readln().toIntOrNull()

                            if (select != null) {

                                when (select) {
                                    1 -> {
                                        println("Successfully logged out!")
                                        println()
                                        main()
                                    }

                                    2 -> {
                                        println("Cancelled!")
                                        println()
                                        return adminChoice()
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
                        println("Invalid input! Please enter a number between 1 and 8.")
                        println()
                    }
                }


            }
        }
    }


}