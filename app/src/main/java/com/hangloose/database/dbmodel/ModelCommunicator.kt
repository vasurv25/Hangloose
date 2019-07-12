/*
 * Copyright (c) 2018. Vungle, All rights reserved.
 */

package com.hangloose.database.dbmodel

interface ModelCommunicator<out T : Any> {
    fun get(): T
}
