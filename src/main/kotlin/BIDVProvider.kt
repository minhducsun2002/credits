import fuel.Fuel
import fuel.post
import kotlinx.serialization.json.*

class BIDVProvider : ICurrencyProvider {
    override suspend fun getProviderName(): String {
        return "BIDV"
    }

    override suspend fun getExchangeRate(currency: String): Int {
        val list = Fuel.post(
            "https://bidv.com.vn/ServicesBIDV/ExchangeDetailServlet",
            headers = mapOf("Content-Type" to "application/json"),
            body = ""
        ).body

        var exchRate = 1
        Json.parseToJsonElement(list).jsonObject["data"]!!.jsonArray
            .forEach {
                if (it.jsonObject["currency"]!!.jsonPrimitive.content == currency) {
                    val rate = it.jsonObject["ban"]!!.jsonPrimitive.content
                    val replaced = rate.replace(",", "")
                    exchRate = replaced.toInt()
                }
            }

        return exchRate
    }

    override suspend fun getFee(currency: String): Float {
        return 0.01f
    }

    override suspend fun getMinimumFee(currency: String): Int {
        return 0
    }
}