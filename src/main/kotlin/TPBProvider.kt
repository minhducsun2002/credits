import fuel.Fuel
import fuel.get
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TPBProvider : ICurrencyProvider {
    override suspend fun getProviderName(): String {
        return "TPB "
    }

    override suspend fun getExchangeRate(currency: String): Int {
        val time = Clock.System.todayIn(TimeZone.of("UTC+7"))
        val d = time.dayOfMonth.toString().padStart(2, '0')
        val m = time.monthNumber.toString().padStart(2, '0')
        val y = time.year
        val link = "https://tpb.vn/CMCWPCoreAPI/api/public-service/get-currency-rate?filename=$y$m$d"

        val string = Fuel.get(link).body
        var exchRate = 1
        val rateObj = Json.parseToJsonElement(string).jsonObject["rate_currency"] ?: return 0
        rateObj.jsonArray
            .forEach {
                val obj = it.jsonObject
                val c = obj["kieu"]!!.jsonPrimitive.content
                if (c == currency) {
                    val rate = obj["saleck"]!!.jsonPrimitive.content
                    val rateInt = rate.replace(",", "").toInt()
                    exchRate = rateInt
                }
            }

        return exchRate
    }

    override suspend fun getFee(currency: String): Float {
        return 0.03f
    }

    override suspend fun getMinimumFee(currency: String): Int {
        return 0
    }
}