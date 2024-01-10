import fuel.Fuel
import fuel.get
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*

class ACBProvider : ICurrencyProvider {
    override suspend fun getProviderName(): String {
        return "ACB "
    }

    override suspend fun getExchangeRate(currency: String): Int {
        val time = Clock.System.now().toString()
        val url = "https://acb.com.vn/api/front/v1/currency?currency=VND&effectiveDateTime=$time"

        val string = Fuel.get(url).body
        var exchRate = 1
        Json.parseToJsonElement(string).jsonArray.forEach out@{
            if (exchRate != 1) {
                return@out
            }

            val obj = it.jsonObject
            val dealType = obj["dealType"]!!.jsonPrimitive.content
            val c = obj["exchangeCurrency"]!!.jsonPrimitive.content
            if (dealType == "ASK" && c == currency) {
                val rate = obj["exchangeRate"]!!.jsonPrimitive.int
                exchRate = rate
            }
        }

        return exchRate
    }

    override suspend fun getFee(currency: String): Float {
        return 0.036f
    }

    override suspend fun getMinimumFee(currency: String): Int {
        return 10000
    }
}