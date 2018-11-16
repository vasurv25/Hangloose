package com.hangloose.listener

interface ResponseListener {
    /**
     * Notify response data.
     * @param response data.
     */
    fun onSuccess(response: Any)

    /**
     * Notify error.
     * @param errorMessage Error message.
     */
    fun onError(errorMessage: String?, ex: Throwable)
}