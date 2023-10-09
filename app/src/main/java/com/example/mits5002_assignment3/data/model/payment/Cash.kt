package com.example.mits5002_assignment3.data.model.payment

import com.example.mits5002_assignment3.data.model.common.PaymentType

/**
 * An object representing cash payment
 */
class Cash (
    paymentInt: Int,
    rideId: Int,
    amount: Double,
    type: PaymentType
) : Payment(paymentInt, rideId, amount, type) {
    // a single property which represents the cash tendered by the rider
    var cashTendered: Double = 0.0
}