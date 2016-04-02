package cs.service

import cs.Order

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
    * @param order
    * @return
    */
  def addOrder(order: Order): Boolean = {
    false
  }

  /**
    * Two orders match if they have opposing directions, matching RICs and quantities, and if the
    * sell price is less than or equal to the buy price
    */
  private def matchOrder(orderToMatch:Order, openOrder:Order):Boolean = {
    //val doesOrderMatch = match
    false
  }

}
