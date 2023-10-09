package com.example.mits5002_assignment3.data.model.payment

import com.example.mits5002_assignment3.data.model.common.PaymentType

/**
 * An object representing various type of card payment (credit and debit cards)
 */
class Card (
    paymentInt: Int,
    rideId: Int,
    amount: Double,
    type: PaymentType
) : Payment(paymentInt, rideId, amount, type) {
    /**
     * A method representing credit card payment. The method is currently empty
     * due to complications and not within scope of implementation
     */
    fun creditCardPayment(){

    }

    /**
     * A method representing debit card payment. The method is currently empty
     * due to complications and not within scope of implementation
     */
    fun debitCardPayment(){

    }
}