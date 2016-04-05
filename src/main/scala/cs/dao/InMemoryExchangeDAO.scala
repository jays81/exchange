package cs.dao

import java.util.concurrent.CopyOnWriteArrayList

import scala.collection.JavaConversions._

import cs.Order


/**
  * Simple in memory implementation of a persistence layer
  */
class InMemoryExchangeDAO extends ExchangeDAO {

  private val openOrders = new CopyOnWriteArrayList[Order]
  private val executedOrders = new CopyOnWriteArrayList[Order]
  private val lock = new Object

  override def addNewOrder(order: Order): Unit = {
    openOrders += order
  }

  override def updateOrderToExecuted(order: Order, matchedOrder:Order): Unit = {
    //this operation needs to be atomic
    lock.synchronized {
      openOrders -= matchedOrder
      openOrders -= order

      //now add executed and matched orders
      executedOrders += order.copy(executed = true)
      executedOrders += matchedOrder
    }
  }

  override def getOpenOrders(): Seq[Order] = {
    openOrders
  }

  override def getExecutedOrders(): Seq[Order] = {
    executedOrders
  }
}
