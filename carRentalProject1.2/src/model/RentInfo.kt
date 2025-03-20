package model

data class RentInfo(
   private val car: Cars?,
   private  val payment: Double?,
   private  val rentDate: String?,
   private  val durationDays: Int?,
    private val returnDate: String?
){
    fun getCar(): Cars? {
        return car
    }

    fun getPayment(): Double? {
        return payment
    }

    fun getRentDate(): String? {
        return rentDate
    }
    fun getDuration(): Int? {
        return durationDays
    }
    fun getReturnDate(): String? {
        return returnDate
    }

}
