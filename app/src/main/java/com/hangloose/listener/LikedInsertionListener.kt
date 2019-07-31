package com.hangloose.listener

interface LikedInsertionListener {
    fun onLikedRecordInserted(id: Long)
}

interface SavedInsertionListener {
    fun onSavedRecordInserted(id: Long)
}