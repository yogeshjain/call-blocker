package com.example.callblocker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.callblocker.model.BlockRepo
import com.example.callblocker.model.BlockedNumber

class CallBlockerViewModel @ViewModelInject constructor(
    private val repo: BlockRepo
): ViewModel() {
    /**
     * Get LiveData for block list
     */
    fun getBlackList() = repo.getBlackList()

    /**
     * Add provided number to block list
     */
    fun blockNumber(number: String, name: String?) {
        repo.blockNumber(BlockedNumber(number, name))
    }

    /**
     * Unblock provided number
     */
    fun unblockNumber(blockedNumber: BlockedNumber) {
        repo.unblockNumber(blockedNumber)
    }
}