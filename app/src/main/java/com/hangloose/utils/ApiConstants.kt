package com.hangloose.utils

enum class AUTH_TYPE(val type: Int) {
    EMAIL(1),
    GOOGLE(2),
    MOBILE(3),
    FACEBOOK(4),
    LINKEDIN(5)
}

val STATUS_OK = 200
val STATUS_FORBIDDEN = 403

val MESSAGE_KEY = "message"
val REASON_KEY = "reason"

enum class OTP_RECOGNIZE(val type: Int) {
    REGISTER_OTP(1),
    RESET_OTP(2)
}
