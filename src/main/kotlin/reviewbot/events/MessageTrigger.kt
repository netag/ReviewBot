package reviewbot.events

import kotlinx.serialization.Serializable
import reviewbot.model.Page
import reviewbot.model.Recipient

@Serializable
sealed class Trigger {
    abstract fun content(): String

    @Serializable
    data class PurchaseComplete(val what: String, val status: Boolean, val ts: Long) : Trigger() {
        override fun content(): String = "yay: $what is on its way"
    }
    @Serializable
    object ThankYou : Trigger() {
        override fun content(): String = "thanks!!!"
    }
    @Serializable
    data class SaySomething(val something: String) : Trigger() {
        override fun content(): String = something
    }
}

@Serializable
data class MessageTrigger(
    val to: Recipient,
    val `for`: Page,
    val trigger: Trigger,
)
