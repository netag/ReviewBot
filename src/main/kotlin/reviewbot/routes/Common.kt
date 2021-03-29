package reviewbot.routes

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun err(msg: String) = buildJsonObject { put("error", msg) }
