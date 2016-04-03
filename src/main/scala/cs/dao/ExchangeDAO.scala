package cs.dao

import cs.Order

/**
 * Created by jay on 03/04/16.
 */
trait ExchangeDAO {

  def addNewOrder(order:Order)

  def addExecutedOrder(order:Order, matchedOrder:Order)

  def getOpenOrders():Seq[Order]
}
