package com.example.mits5002_assignment3.data.model.payment

import com.example.mits5002_assignment3.data.model.common.PaymentType

/**
 * Inheritable payment abstract class that represents different types of payments
 * @param paymentId The id of the payment
 * @param rideId The id of the ride associated with this payment
 * @param amount Ride payment amount in local currency
 * @param type Type of payment (cash, card, e-wallet)
 */
abstract class Payment (
    private val paymentId: Int,
    private val rideId: Int,
    private val amount: Double,
    private val type: PaymentType
) {
    fun getPaymentType(): PaymentType { return type }
}