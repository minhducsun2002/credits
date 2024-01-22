import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import fuel.Fuel
import fuel.get

class VTBProvider : ICurrencyProvider {
    override suspend fun getProviderName(): String {
        return "VTB "
    }

    override suspend fun getExchangeRate(currency: String): Int {
        val text = Fuel.get("https://www.vietinbank.vn/web/home/vn/ty-gia/").body
        var isCurrency = false
        var count = 1
        var exchRate = 1
        val handler = KsoupHtmlHandler.Builder()
            .onText {
                if (it.trim() == "USD") {
                    isCurrency = true
                }

                if (count == 4)
                {
                    exchRate = it.replace(",", "").toInt()
                }
            }
            .onOpenTag out@{ name, attributes, _ ->
                if (!isCurrency) {
                    return@out
                }
                if (name == "td" && attributes.get("align") == "center") {
                    count++
                }
            }
            .build()
        val parser = KsoupHtmlParser(handler = handler)
        parser.write(text)
        parser.end()

        return exchRate
    }

    override suspend fun getFee(currency: String): Float {
        return 0.0273f
    }

    override suspend fun getMinimumFee(currency: String): Int {
        return 0
    }
}