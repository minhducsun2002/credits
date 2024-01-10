interface ICurrencyProvider {
    suspend fun getProviderName() : String
    suspend fun getExchangeRate(currency: String) : Int
    suspend fun getFee(currency: String) : Float
    suspend fun getMinimumFee(currency: String) : Int
}