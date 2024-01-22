import fuel.FuelBuilder
import fuel.Request
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ACBProvider : ICurrencyProvider {
    override suspend fun getProviderName(): String {
        return "ACB "
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
        return builder.build()
    }

    override suspend fun getExchangeRate(currency: String): Int {
        val time = Clock.System.now().toString()
        val url = "https://acb.com.vn/api/front/v1/currency?currency=VND&effectiveDateTime=$time"

        var string = ""
        var exception: Exception? = null
        for (a in 1..3) {
            try {
                val fuel = FuelBuilder().config(buildOkHttpClient()).build()
                val request = Request.Builder().url(url)
                string = fuel.get(request.build()).body
                exception = null
                break
            } catch (e: Exception) {
                exception = e
            }
        }

        if (exception != null) {
            throw exception
        }

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