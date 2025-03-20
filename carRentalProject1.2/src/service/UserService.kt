package service


import main
import model.Cars
import model.RentInfo
import model.Users
import java.io.File
import java.time.LocalDate

class UserService(
    private val userAccounts: MutableList<Users>,
    private val carService: CarService,
    private val carList: MutableList<Cars>,
    private val availableCarList: MutableList<Cars>,
    private val rentalHistory: HashMap<String, MutableList<RentInfo>>,
    private val notAvailableCarList: MutableList<Cars>
) {

    private var choice: Int? = null


    fun userSignup() {

        var username: String?
        while (true) {
            println("Enter the username: ")
            username = readlnOrNull()?.trim()

            if (!username.isNullOrEmpty()) {
                break
            }

            println("Invalid name, Please enter a valid name")
            println()
        }

        var password: String?
        while (true) {
            println("Enter the password: ")
            password = readlnOrNull()?.trim()

            if (!password.isNullOrEmpty()) {
                break
            }

            println("Invalid password, Please enter a valid password")
            println()
        }





        val itContains = userAccounts.find { it.getUsername() == username }
        val itContains1 = userAccounts.find { it.getPassword() == password }
        if (itContains == null && itContains1 == null) {
            val user = Users(username, password)
            userAccounts.add(user)
            saveUsersToFile()
            println("Successfully Signed up")
        } else {
            println("Username or Password is already taken")
        }
    }


    fun userChoice(username: String) {
        rentalHistory.putIfAbsent(username, mutableListOf())
        while (true) {
            println(
                """
                1. Show Available Cars
                2. Rent a Car
                3. Show rented Car
                4. Return a Car
                5. Logout
                """.trimIndent()
            )
            val enter = readlnOrNull()
            choice = enter?.toIntOrNull()

            if (choice == null || choice !in 1..5) {
                println("Invalid input! Please enter a number between 1 and 5.")
                return userChoice(username)
            } else {

                when (choice) {
                    1 -> {
                        carService.showAvailableCars(availableCarList)

                    }

                    2 -> {
                        val rentedCarsCount = rentalHistory[username]?.size ?: 0

                        if (rentedCarsCount >= 1) {
                            println("You have ongoing rent, Please return the car to be able to rent again")
                            println()
                            return userChoice(username)
                        }

                        carService.rentCar(carList, availableCarList, notAvailableCarList, rentalHistory, username)

                    }

                    3 -> {
                        showRentalHistory(username)

                    }

                    4 -> {
                        showRentalHistory(username)
                        carService.returnCar(carList, notAvailableCarList, rentalHistory, username, availableCarList)
                    }

                    5 -> {

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
                                        return userChoice(username)
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
                        println("Invalid input! Please enter a number between 1 and 4.")
                        println()
                        return userChoice(username)
                    }
                }
            }

        }

    }

    private fun showRentalHistory(username: String) {
        val history = rentalHistory[username]

        if (history.isNullOrEmpty()) {
            println("No rental history found.")
            println()
            return
        }

        println("Rental History for $username:")
        history.forEach { rentInfo ->

            val rentDateStr = rentInfo.getRentDate()?.trim()
            val rentDate = if (rentDateStr?.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) == true) {
                LocalDate.parse(rentDateStr)
            } else {
                println("Invalid or missing rent date '$rentDateStr'. Using today's date.")
                LocalDate.now()
            }


            val durationDays = rentInfo.getDuration() ?: 0
            val returnDate = rentDate.plusDays(durationDays.toLong())

            println(
                """
            Car      : ${rentInfo.getCar()?.getCarName()}
            Brand    : ${rentInfo.getCar()?.getCarBrand()}
            ID       : ${rentInfo.getCar()?.getCarId()}
            Payment  : ₱${rentInfo.getPayment()}
            Rent Date: $rentDate
            Duration : $durationDays days
            Expected Return Date: $returnDate
            """.trimIndent()
            )
            println("---------------------------------------")
        }
        println()
    }


    fun showUser() {
        if (userAccounts.isEmpty()) {
            println("There are no Users")
            println()
            return
        }

        println("List of Users:")
        userAccounts.forEach { user ->
            println("--------------------------------")
            println("Username: ${user.getUsername()}")
            println("Rent History:")

            val history = rentalHistory.getOrDefault(user.getUsername(), mutableListOf())

            if (history.isEmpty()) {
                println("No rental history found.")
            } else {
                history.forEach { rentInfo ->
                    val rentDateStr = rentInfo.getRentDate()?.trim() ?: LocalDate.now().toString()
                    val rentDate = LocalDate.parse(rentDateStr)
                    val returnDate = rentDate.plusDays(rentInfo.getDuration()?.toLong() ?: 0L)

                    println(
                        """
                    Car      : ${rentInfo.getCar()?.getCarName()}
                    Brand    : ${rentInfo.getCar()?.getCarBrand()}
                    ID       : ${rentInfo.getCar()?.getCarId()}
                    Payment  : ₱${rentInfo.getPayment()}
                    Rent Date: $rentDate
                    Duration : ${rentInfo.getDuration()} days
                    Expected Return Date: $returnDate
                    """.trimIndent()
                    )
                    println()
                }
            }
        }
        println()
    }


    private fun saveUsersToFile(filename: String = "users.txt") {
        val file = File(filename)
        file.printWriter().use { writer ->
            userAccounts.forEach { user ->
                writer.println("${user.getUsername()},${user.getPassword()}")
            }
        }
    }


    fun loadUsersFromFile(filename: String = "users.txt"): MutableList<Users> {
        val file = File(filename)
        val loadedUsers = mutableListOf<Users>()

        if (!file.exists()) return loadedUsers

        file.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 2) {
                val username = parts[0]
                val password = parts[1]
                loadedUsers.add(Users(username, password))
            }
        }
        return loadedUsers
    }


    fun loadRentalHistoryFromFile(
        rentalHistory: HashMap<String, MutableList<RentInfo>>,
        carList: MutableList<Cars>,
        filename: String = "rental_history.txt"
    ) {
        val file = File(filename)
        if (!file.exists()) return

        file.forEachLine { line ->
            val parts = line.split(":")
            if (parts.size == 2) {
                val username = parts[0]
                val rentInfos = parts[1].split("|").mapNotNull { record ->
                    val data = record.split(",")
                    if (data.size == 5) {
                        val carId = data[0].toIntOrNull()
                        val payment = data[1].toDoubleOrNull() ?: 0.0
                        val rentDate = data[2]
                        val durationDays = data[3].toIntOrNull() ?: 0
                        val returnDate = data[4]

                        val car = carId?.let { id -> carList.find { it.getCarId() == id } }
                        car?.let { RentInfo(it, payment, rentDate, durationDays, returnDate) }
                    } else {
                        null
                    }
                }

                rentalHistory[username] = rentInfos.toMutableList()
            }
        }
    }


}