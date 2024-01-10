import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import fuel.Fuel
import fuel.get

class VCBProvider : ICurrencyProvider {
    override suspend fun getProviderName(): String {
        return "VCB "
    }

    override suspend fun getExchangeRate(currency: String): Int {
        val string = Fuel.get("https://www.vietcombank.com.vn/KHCN/Cong-cu-tien-ich/Ty-gia").body
        var exchRate = 1
        val handler = KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                if (name == "li" && attributes.get("data-code") == currency) {
                    exchRate = attributes.get("data-sell-rate")!!.toFloat().toInt()
                }
            }
            .build()
        val ksoupHtmlParser = KsoupHtmlParser(handler = handler)
        ksoupHtmlParser.write(string)
        ksoupHtmlParser.end()

        return exchRate
    }

    override suspend fun getFee(currency: String): Float {
        return 0.0227f
    }

    override suspend fun getMinimumFee(currency: String): Int {
        return 0
    }
}