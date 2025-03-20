package service

import model.Cars
import model.RentInfo
import model.Users
import java.io.File
import java.time.LocalDate


import kotlin.random.Random


class CarService {


    private fun generateRandomIdNumber(): String {
        val rand = Random
        return (1..6)
            .map { rand.nextInt(0, 10) }
            .joinToString("")
    }


    fun addCar(carList: MutableList<Cars>, availableCar: MutableList<Cars>) {
        println("Enter a Car Name: ")
        val carName = readlnOrNull()?.trim()

        val locateCar = carList.find { it.getCarName() == carName }
        if (locateCar != null) {
            println("Car already exist!")
            return addCar(carList, availableCar)
        }
        if (carName.isNullOrEmpty()) {
            println("Please enter a valid car name!")
            println()
            return addCar(carList, availableCar)
        }

        var carBrand: String?
        while (true) {
            println("Enter the Car Brand: ")
            carBrand = readlnOrNull()?.trim()

            if (!carBrand.isNullOrEmpty()) {
                break
            }

            println("Please enter a valid car brand!")
            println()
        }

        val carId = generateRandomIdNumber().toInt()
        val cars = Cars(carName, carBrand, carId)
        carList.add(cars)
        availableCar.add(cars)
        println("The car $carName has been added")

        textFileCarList(carList)
        textFileAvailCar(availableCar)
    }


    fun showAllCars(carList: MutableList<Cars>) {
        if (carList.isNotEmpty()) {
            println("All Cars: ")

            for (cars in carList) {
                println("Car Name : ${cars.getCarName()}")
                println("Car Brand: ${cars.getCarBrand()}")
                println("Car Id   : ${cars.getCarId()}")
                println()
            }
        } else {
            println("There are no Cars")
            println()
        }
    }


    fun deleteCar(
        carList: MutableList<Cars>,
        availableCarList: MutableList<Cars>,
        notAvailableCarList: MutableList<Cars>
    ) {
        if (carList.isNotEmpty()) {
            showAllCars(carList)
            println("Enter the Car Name: ")
            val carDelete = readlnOrNull()?.trim()
            if (carDelete.isNullOrEmpty()) {
                println("Please enter a valid car name!")
                println()
                return deleteCar(carList, availableCarList, notAvailableCarList)
            }

            val carRemove = carList.find { it.getCarName() == carDelete }

            if (carRemove != null) {

                if (notAvailableCarList.contains(carRemove)) {
                    println("The car $carDelete is currently rented and cannot be deleted.")
                    println()
                    return
                }

                println(
                    """
                Are you sure you want to delete?
                1. Yes
                2. No
                Enter choice: 
            """.trimIndent()
                )

                val choice = readln().toIntOrNull()
                if (choice == null) {
                    println("Please enter a valid choice")
                    println()
                    return deleteCar(carList, availableCarList, notAvailableCarList)
                }

                when (choice) {
                    1 -> {
                        carList.remove(carRemove)
                        availableCarList.remove(carRemove)
                        textFileCarList(carList)
                        textFileAvailCar(availableCarList)

                        println("The car $carDelete has been removed.")
                    }

                    2 -> println("Cancelled")

                    else -> {
                        println("Invalid choice, please enter a valid choice")
                        return deleteCar(carList, availableCarList, notAvailableCarList)
                    }
                }
            } else {
                println("The car $carDelete is not on the list.")
                println()
                return deleteCar(carList, availableCarList, notAvailableCarList)
            }
        } else {
            println("There are no cars to delete, please add a car first")
            println()
        }
    }

    fun showAvailableCars(availableCarList: MutableList<Cars>) {
        if (availableCarList.isNotEmpty()) {
            println("Available Cars: ")
            for (cars in availableCarList) {
                println("Car Name: ${cars.getCarName()}")
                println("Car Brand: ${cars.getCarBrand()}")
                println("Car id: ${cars.getCarId()}")
                println()
            }
        } else {
            println("There are no available car")
            println()
        }
    }

    fun rentCar(
        carList: MutableList<Cars>,
        availableCarList: MutableList<Cars>,
        notAvailableCarList: MutableList<Cars>,
        rentalHistory: HashMap<String, MutableList<RentInfo>>,
        username: String
    ) {
        if (availableCarList.isEmpty()) {
            println("There are no available cars.")
            return
        }

        showAvailableCars(availableCarList)
        println("Enter the name of the car: ")
        val carName = readlnOrNull()?.trim()

        if (carName.isNullOrEmpty()) {
            println("Please enter a valid car name!")
            return rentCar(carList, availableCarList, notAvailableCarList, rentalHistory, username)
        }

        val locateCar = availableCarList.find { it.getCarName() == carName }
        if (locateCar == null) {
            println("The $carName is not available or not on the car list.")
            return rentCar(carList, availableCarList, notAvailableCarList, rentalHistory, username)
        }
        val (rentDuration, totalPrice) = durationAndPayment() ?: return


        val currentDate = LocalDate.now()
        val returnDate = currentDate.plusDays(rentDuration.toLong()).toString()
        val rentalRecord = RentInfo(
            car = locateCar,
            payment = totalPrice,
            rentDate = currentDate.toString(),
            durationDays = rentDuration,
            returnDate = returnDate

        )

        rentalHistory.putIfAbsent(username, mutableListOf())
        rentalHistory[username]?.add(rentalRecord)

        notAvailableCarList.add(locateCar)
        availableCarList.remove(locateCar)

        textFileAvailCar(availableCarList)
        textFileNotAvail(notAvailableCarList)
        saveRentalHistoryToFile(rentalHistory)
        println("The $carName has been successfully rented.")
        println()
        val rentedCarsCount = rentalHistory[username]?.size ?: 0

        if (rentedCarsCount > 3) {
            return

        }
        if (availableCarList.isEmpty()) {
            return
        }


        var select: Int?
        while (true) {
            val maxRentable = 3 - rentedCarsCount
            val remainingRentals = minOf(maxRentable, availableCarList.size)
            println(
                """
                 You can rent $remainingRentals cars again!
                 Do you  want to rent again?
                            
                 1. Yes
                 2. No
                 Select a number: 
                """.trimIndent()
            )
            select = readln().toIntOrNull()

            if (select != null) {

                when (select) {
                    1 -> {
                        return rentCar(carList, availableCarList, notAvailableCarList, rentalHistory, username)
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


    fun showNotAvailableCar(notAvailableCarList: MutableList<Cars>) {

        if (notAvailableCarList.isNotEmpty()) {
            println("Not Available Cars: ")
            for (cars in notAvailableCarList) {
                println("Car Name: ${cars.getCarName()}")
                println("Car Brand: ${cars.getCarBrand()}")
                println("Car id: ${cars.getCarId()}")
                println()
            }
        } else {
            println("There are no not available car yet")
            println()
        }


    }


    private fun durationAndPayment(): Pair<Int, Double>? {
        println(
            """
        Choose Duration:
        
        1.  1 Day    --> ₱1,500
        2.  2 Days   --> ₱3,000
        3.  3 Days   --> ₱4,500
        4.  4 Days   --> ₱6,000
        5.  5 Days   --> ₱7,500
        6.  6 Days   --> ₱9,000
        7.  1 Week   --> ₱9,000
        8.  2 Weeks  --> ₱18,000
        9.  3 Weeks  --> ₱27,000
        10. 4 Weeks  --> ₱36,000
        
        Enter Selection: 
    """.trimIndent()
        )

        val choice = readln().toIntOrNull()
        val priceMap = mapOf(
            1 to 1500.0, 2 to 3000.0, 3 to 4500.0, 4 to 6000.0, 5 to 7500.0, 6 to 9000.0,
            7 to 9000.0, 8 to 18000.0, 9 to 27000.0, 10 to 36000.0
        )

        if (choice == null || choice !in priceMap.keys) {
            println("Invalid choice. Please select a valid number.")
            return durationAndPayment()
        }

        val totalPrice = priceMap[choice]!!
        val durationDays = getDurationDays(choice)

        println("Total cost: ₱$totalPrice")

        var confirm: Int?
        while (true) {
            println(
                """
        Proceed with payment?
        1. Yes
        2. No/Cancel
    """.trimIndent()
            )
            confirm = readln().toIntOrNull()
            if (confirm == null || confirm !in 1..2) {
                println("Invalid choice. Please enter 1 or 2.")
                continue
            }

            if (confirm == 2) {
                println("Rental cancelled.")
                return null
            }

            var payment: Double?
            while (true) {
                print("Enter amount: ")
                payment = readln().toDoubleOrNull()

                if (payment == null) {
                    println("Invalid input. Please enter a valid number.")
                    continue
                }

                if (payment < totalPrice) {
                    println("Insufficient payment! You need at least ₱$totalPrice.")
                    continue
                }

                val change = payment - totalPrice
                println("Payment successful! You paid ₱$payment.")
                println()
                if (change > 0) {
                    println("Your change: ₱$change")
                    println()
                }

                return Pair(durationDays, totalPrice)
            }

        }

//
    }


    private fun textFileCarList(carList: MutableList<Cars>, filename: String = "cars.txt") {
        val file = File(filename)
        file.printWriter().use { writer ->
            carList.forEach { car ->
                writer.println("${car.getCarId()},${car.getCarName()},${car.getCarBrand()}")
            }
        }
    }

    private fun textFileAvailCar(availableCarList: MutableList<Cars>, filename: String = "available_cars.txt") {
        val file = File(filename)
        file.printWriter().use { writer ->
            availableCarList.forEach { car ->
                writer.println("${car.getCarId()},${car.getCarName()},${car.getCarBrand()}")
            }
        }
    }

    private fun textFileNotAvail(notAvailableCarList: MutableList<Cars>, filename: String = "not_available_cars.txt") {
        val file = File(filename)
        file.printWriter().use { writer ->
            notAvailableCarList.forEach { car ->
                writer.println("${car.getCarId()},${car.getCarName()},${car.getCarBrand()}")
            }
        }
    }


    fun loadNotAvailableCarsFromFile(filename: String = "not_available_cars.txt"): MutableList<Cars> {
        val file = File(filename)
        val notAvailableCarList = mutableListOf<Cars>()

        if (!file.exists()) return notAvailableCarList

        file.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 3) {
                val carId = parts[0].toInt()
                val carName = parts[1]
                val carBrand = parts[2]
                notAvailableCarList.add(Cars(carName, carBrand, carId))
            }
        }
        return notAvailableCarList
    }


    fun loadAvailableCarsFromFile(filename: String = "available_cars.txt"): MutableList<Cars> {
        val file = File(filename)
        val availableCarList = mutableListOf<Cars>()

        if (!file.exists()) return availableCarList

        file.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 3) {
                val carId = parts[0].toInt()
                val carName = parts[1]
                val carBrand = parts[2]
                availableCarList.add(Cars(carName, carBrand, carId))
            }
        }
        return availableCarList
    }


    fun loadAllCarsFromFile(filename: String = "cars.txt"): MutableList<Cars> {
        val file = File(filename)
        val carList = mutableListOf<Cars>()

        if (!file.exists()) {

            return carList
        }

        file.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 3) {
                val carId = parts[0].toIntOrNull()
                val carName = parts[1]
                val carBrand = parts[2]

                if (carId != null) {
                    carList.add(Cars(carName, carBrand, carId))
                }
            }
        }

        return carList
    }


    fun returnAdmin(
        carList: MutableList<Cars>,
        notAvailableCarList: MutableList<Cars>,
        rentalHistory: HashMap<String, MutableList<RentInfo>>,
        availableCarList: MutableList<Cars>,
        userAccounts: MutableList<Users>
    ) {


        if (notAvailableCarList.isNotEmpty()) {

            println("Enter Client name: ")
            val nameUser = readlnOrNull()?.trim()
            if (nameUser.isNullOrEmpty()) {
                println("Invalid name please enter a valid name!")
                println()
                return returnAdmin(carList, notAvailableCarList, rentalHistory, availableCarList, userAccounts)
            }

            val locateUser = userAccounts.find { it.getUsername()?.trim() == nameUser.trim() }
            if (locateUser == null) {
                println("There are no $nameUser client exist")
                println()
                return returnAdmin(carList, notAvailableCarList, rentalHistory, availableCarList, userAccounts)
            }

            if (!rentalHistory[nameUser].isNullOrEmpty()) {

                var name: String?
                while (true) {
                    println("Enter the Car name: ")
                    name = readlnOrNull()?.trim()

                    if (name.isNullOrEmpty()) {

                        println("Please enter a valid car name!")
                        println()
                        continue
                    }
                    val locateCar = notAvailableCarList.find { it.getCarName() == name }
                    val locateIndex = notAvailableCarList.indexOfFirst { it.getCarName() == name }
                    val locateCar2 = carList.find { it.getCarName() == name }

                    if (locateCar2 == null) {
                        println("The car $name is not on the system, please enter a car from your history")
                        println()
                        continue
                    }
                    if (locateCar == null) {
                        println("No history of renting $name")
                        println()
                        continue
                    }
                    if (locateIndex == -1) {
                        println("No history of renting $name")
                        println()
                        continue
                    }
                    break


                }


                val locateIndex = notAvailableCarList.indexOfFirst { it.getCarName() == name }
                val car = notAvailableCarList.removeAt(locateIndex)

                availableCarList.add(car)

                if (!carList.contains(car)) {
                    carList.add(car)
                }
                rentalHistory[nameUser]?.removeIf { it.getCar()?.getCarName() == name }


                if (rentalHistory[nameUser].isNullOrEmpty()) {
                    rentalHistory.remove(name)
                }
                textFileAvailCar(availableCarList)
                textFileNotAvail(notAvailableCarList)
                saveRentalHistoryToFile(rentalHistory)
                println("The car $name has successfully returned")
                println()
            } else {
                println("The client $nameUser has no transaction")
                println()
            }


        } else {
            println("There are no not available cars")
            println()
        }


    }


    fun clear(availableCarList: MutableList<Cars>, carList: MutableList<Cars>, notAvailableCarList: MutableList<Cars>) {
        if (carList.isNotEmpty()) {
            println(
                """
                    Do you really want to delete?
                    1. Yes
                    2. No
                """.trimIndent()
            )
            val enter = readln().toIntOrNull()
            if (enter == null) {
                println("Invalid choice please enter a valid choice")
                println()
                return clear(availableCarList, carList, notAvailableCarList)
            }
            when (enter) {
                1 -> {


                    carList.retainAll(notAvailableCarList)
                    availableCarList.retainAll(notAvailableCarList)

                    textFileAvailCar(availableCarList)
                    textFileCarList(carList)

                    println("Successfully cleared available cars, except rented ones.")
                    println()

                    textFileAvailCar(availableCarList)
                    textFileCarList(carList)

                }

                2 -> {
                    return
                }

                else -> {
                    println("Invalid choice please enter a valid choice")
                    println()
                }
            }


        } else {
            println("There are no car to clear, please add a car first!")
            println()

        }


    }


    fun returnCar(
        carList: MutableList<Cars>,
        notAvailableCarList: MutableList<Cars>,
        rentalHistory: HashMap<String, MutableList<RentInfo>>,
        username: String, availableCarList: MutableList<Cars>
    ) {
        if (carList.isNotEmpty()) {
            if (!rentalHistory[username].isNullOrEmpty()) {

                println("Enter a car to return")
                val name = readlnOrNull()?.trim()
                if (name.isNullOrEmpty()) {
                    println("Invalid car name, please enter a valid car name to return")
                    println()
                    return returnCar(carList, notAvailableCarList, rentalHistory, username, availableCarList)
                }
                val locateCar = notAvailableCarList.find { it.getCarName() == name }
                val locateIndex = notAvailableCarList.indexOfFirst { it.getCarName() == name }
                val locateCar2 = carList.find { it.getCarName() == name }

                if (locateCar2 == null) {
                    println("The car $name is not on the system, please enter a car from your history")
                    println()
                    return returnCar(carList, notAvailableCarList, rentalHistory, username, availableCarList)
                }
                if (locateCar == null) {
                    println("You have no history of renting $name")
                    println()
                    return returnCar(carList, notAvailableCarList, rentalHistory, username, availableCarList)
                }
                if (locateIndex == -1) {
                    println("You have no history of renting $name")
                    println()
                    return returnCar(carList, notAvailableCarList, rentalHistory, username, availableCarList)
                }
                val car = notAvailableCarList.removeAt(locateIndex)

                availableCarList.add(car)

                if (!carList.contains(car)) {
                    carList.add(car)
                }
                rentalHistory[username]?.removeIf { it.getCar()?.getCarName() == name }


                if (rentalHistory[username].isNullOrEmpty()) {
                    rentalHistory.remove(username)
                }
                textFileAvailCar(availableCarList)
                textFileNotAvail(notAvailableCarList)
                saveRentalHistoryToFile(rentalHistory)
                println("The car $name has successfully returned")
                println()
            } else {
                println("There are no rented cars")
                println()

            }
        } else {
            println("There are no car to return!")
            println()
        }


    }


    private fun getDurationDays(choice: Int): Int {
        val durationMap = mapOf(
            1 to 1, 2 to 2, 3 to 3, 4 to 4, 5 to 5, 6 to 6,
            7 to 7, 8 to 14, 9 to 21, 10 to 28
        )
        return durationMap[choice] ?: 1
    }


    private fun saveRentalHistoryToFile(
        rentalHistory: HashMap<String, MutableList<RentInfo>>,
        filename: String = "rental_history.txt"
    ) {
        val file = File(filename)
        file.printWriter().use { writer ->
            rentalHistory.forEach { (username, rentInfos) ->
                val rentalData = rentInfos.joinToString("|") { rentInfo ->
                    val rentDateStr = rentInfo.getRentDate()?.trim() ?: LocalDate.now().toString()
                    val rentDate = LocalDate.parse(rentDateStr)
                    val returnDate = rentDate.plusDays(rentInfo.getDuration()?.toLong() ?: 0L)

                    "${
                        rentInfo.getCar()?.getCarId()
                    },${rentInfo.getPayment()},$rentDateStr,${rentInfo.getDuration()},$returnDate"
                }
                writer.println("$username:$rentalData")
            }
        }
    }

}