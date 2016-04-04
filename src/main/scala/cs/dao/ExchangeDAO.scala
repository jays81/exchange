package cs.dao

import cs.Order


trait ExchangeDAO {

  def addNewOrder(order:Order)

  def addExecutedOrder(order:Order, matchedOrder:Order)

  def getOpenOrders:Seq[Order]

  def getExecutedOrders:Seq[Order]
}
