package cs.dao

import cs.Order

import scala.collection.mutable.ArrayBuffer

/**
  * Simple in memory implementation of a persistence layer
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

  override def updateOrderToExecuted(order: Order, matchedOrder:Order): Unit = {
    lock.synchronized {
      openOrders -= matchedOrder
      openOrders -= order

      //now add executed and matched orders
      executedOrders += order.copy(executed = true)
      executedOrders += matchedOrder
    }
  }

  override def getOpenOrders(): Seq[Order] = {
    lock.synchronized{
      openOrders
    }
  }

  override def getExecutedOrders(): Seq[Order] = {
    lock.synchronized{
      executedOrders
    }
  }
}
