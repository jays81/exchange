package cs.dao

import cs.Order

import scala.collection.mutable.ArrayBuffer

/**
 * Created by jay on 03/04/16.
 */
class InMemoryExchangeDAO extends ExchangeDAO {

  private val openOrders = new ArrayBuffer[Order]
  private val executedOrders = new ArrayBuffer[Order]
  private val lock = new Object()

  override def addNewOrder(order: Order): Unit = {
    lock.synchronized {
      openOrders += order
    }
  }

  override def addExecutedOrder(order: Order, matchedOrder:Order): Unit = {
    lock.synchronized {
      openOrders -= matchedOrder
      openOrders -= order

      //now add executed orders
      executedOrders += order
      executedOrders += matchedOrder
    }
  }

  override def getOpenOrders(): Seq[Order] = {
    lock.synchronized{
      openOrders.toSeq
    }
  }
}
