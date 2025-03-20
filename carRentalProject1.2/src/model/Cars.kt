package model

data class Cars(
    private val carName: String?,
    private val carBrand: String?,
    private val carId: Int?,


    ) {
    fun getCarName(): String? {
        return carName
    }

    fun getCarBrand(): String? {
        return carBrand
    }

    fun getCarId(): Int? {
        return carId
    }


}

