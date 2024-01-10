import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.dotenv
import java.text.DecimalFormat
import kotlin.math.ceil

fun getToken(): String {
    val dotenv = dotenv()
    val token = dotenv.get("DISCORD_TOKEN", "")
    val env = System.getenv("DISCORD_TOKEN") ?: ""
    return if (token == "") env else token
}

fun onReady(e: ReadyEvent) {
    val user = e.self
    @Suppress("DEPRECATION")
    println("Ready. Logged in as %s#%s.".format(user.username, user.discriminator))
}

suspend fun onMessage(e: MessageCreateEvent) {
    val provider = listOf(
        VCBProvider(), BIDVProvider(), VTBProvider(), ACBProvider(), TPBProvider()
    )
    val message = e.message
    val content = message.content

    if (content.trim() == "money") {
        message.addReaction(ReactionEmoji.Unicode("⏳"))

        var final: String

        try {
            val lines = provider
                .map {
                    val name = it.getProviderName()
                    val rate = it.getExchangeRate("USD")
                    val fee = it.getFee("USD")
                    val minimum = it.getMinimumFee("USD")

                    val total = ceil((rate * fee + rate).toDouble()).toInt()
                    val format = DecimalFormat("##,###")
                    var line = "**`$name`** : **`${format.format(rate)}`**  /  **`${format.format(total)}`**"
                    if (minimum != 0) {
                        line += " `(!)`"
                    }
                    Pair(line, total)
                }
                .sortedBy { pair -> pair.second }
                .map { pair -> pair.first }
            final = lines.joinToString("\n")
        } catch (e: Exception) {
            final = e.message ?: "An exception was thrown, but without a message"
            println(e)
            println(e.stackTraceToString())
        }
        message.deleteReaction(ReactionEmoji.Unicode("⏳"))
        message.channel.createMessage(final)
    }
}

suspend fun main() {
    val token = getToken()
    val kord = Kord(token)

    kord.on<ReadyEvent> { onReady(this) }

    kord.on<MessageCreateEvent> {
        try { onMessage(this) } catch (e: Exception) {
            println(e)
            println(e.stackTraceToString())
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}