package cs.service

import cs.{ExecutionResult, Direction, Order}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by jay on 02/04/16.
  */
class ExchangeService {
  val openOrders = new ArrayBuffer[Order]
  val executedOrders = new ArrayBuffer[Order]

  /**
    * Add an order, this will first compare the order
    * against existing orders to check if it can be matched
    * If orders match they are said to be executed
    * returns true if an order has been executed
    *
    * @param order
    * @return
    */
  def addOrder(order: Order): ExecutionResult = {
    openOrders += order
    val ordersFound = openOrders.find(openOrder => matchOrder(order, openOrder))
    println(ordersFound)

    val executionResult = ordersFound match {
      case Some(openOrder) => ExecutionResult(true,order)
      case None => ExecutionResult(false, order)
    }

    executionResult
  }

  private def checkPrice(orderToMatch: Order, openOrder: Order): Boolean = {
    val checkPrice = orderToMatch.direction match {
        case Direction.sell => orderToMatch.price <= openOrder.price
        case _  => openOrder.price <= orderToMatch.price
    }
    checkPrice
  }

/**
    * Two orders match if they have opposing directions, matching RICs and quantities, and if the
    * sell price is less than or equal to the buy price
    */
  private def matchOrder(orderToMatch:Order, openOrder:Order):Boolean = {
    (orderToMatch.direction != openOrder.direction &&
      orderToMatch.ric == openOrder.ric &&
      orderToMatch.quantity == openOrder.quantity &&
      checkPrice(orderToMatch, openOrder))

  }

}
