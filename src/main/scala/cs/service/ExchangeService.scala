package cs.service


import java.math.MathContext

import cs.Direction.Direction
import cs.dao.ExchangeDAO
import cs.{Direction, ExecutionResult, OpenInterest, Order}

/**
  * Exchange service allows an order to be add, it will be executed if matching orders
  * are found. Also provides functions to get open interest, average execution price and executed quantity
  */
class ExchangeService(exchangeDao:ExchangeDAO, orderValidator:OrderValidator, orderParser:OrderParser, idGenerator: IdGenerator) {

  /**
    * Add an order, this will first validate and parse the order
    * creating an order, this will go on to check the order against existing orders
    * to check if it can be matched
    * If orders match they are said to be executed
    */
  def addOrder(order:String, user: String):Option[ExecutionResult] = {
    //first validate the order
    val isValid = orderValidator.validateOrder(Some(order))
    val parsedOrder = isValid match {
      case true => orderParser.parseOrder(Some(order), user, idGenerator.generateId)
      case false => None
    }

    parsedOrder.map(order => addOrder(order))

  }

  /**
    * Add an order, this will first compare the order
    * against existing orders to check if it can be matched
    * If orders match they are said to be executed
    */
  private def addOrder(order: Order): ExecutionResult = {
    exchangeDao.addNewOrder(order)
    val matchingOrders = exchangeDao.getOpenOrders.filter(openOrder => matchOrder(order, openOrder))
    val matchedOrder = getPriceFromMultipleMatches(order, matchingOrders)

    val executionResult = matchedOrder match {
      case Some(openOrder) => {
        exchangeDao.updateOrderToExecuted(order, openOrder)
        ExecutionResult(orderId = order.id,
          matchedOrderId = Some(openOrder.id),
          executed = true,
          orderDirection = order.direction,
          matchDirection = getOppositeDirection(order.direction),
          matchPrice = openOrder.price,
          executionPrice = order.price)
      }
      case None => ExecutionResult(orderId = order.id,
                                  executed = false,
                                  orderDirection = order.direction,
                                  executionPrice = order.price)
    }

    executionResult
  }

  //for sell order get the highest price, for buy get the lowest
  private def getPriceFromMultipleMatches(order: Order, filteredOrders: Seq[Order]): Option[Order] = {
    val matchedOrder = order.direction match {
      case Direction.sell => filteredOrders.nonEmpty match {
        case true => {
          val earliestBestOrder = getEarliestBestPrice(filteredOrders, max)
          Some(earliestBestOrder)
        }
        case false => None
      }
      case Direction.buy => filteredOrders.nonEmpty match {
        case true => {
          val earliestBestOrder = getEarliestBestPrice(filteredOrders, min)
          Some(earliestBestOrder)
        }
        case false => None
      }
    }
    matchedOrder
  }

  //if there are multiple best price matches get the earliest one
  private def getEarliestBestPrice(filteredOrders: Seq[Order], minOrMaxFunction: (Order, Order) => Order): Order = {
    //first get best price
    val bestOrderPrice = filteredOrders.reduceLeft(minOrMaxFunction).price
    //now get the earliest best price
    val bestOrders = filteredOrders.filter(filteredOrder => filteredOrder.price == bestOrderPrice)
    bestOrders.reduceLeft(earliestOrder)
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
    orderToMatch.direction != openOrder.direction &&
      orderToMatch.ric == openOrder.ric &&
      orderToMatch.quantity == openOrder.quantity &&
      checkPrice(orderToMatch, openOrder)

  }

  private def max(order1: Order, order2: Order): Order = if (order1.price > order2.price) order1 else order2

  private def min(order1: Order, order2: Order): Order = if (order1.price < order2.price) order1 else order2

  private def earliestOrder(order1: Order, order2: Order): Order = if (order1.id < order2.id) order1 else order2

  private def getOppositeDirection(direction: Direction): Direction = direction match {
    case Direction.buy => Direction.sell
    case Direction.sell => Direction.buy
  }

  /**
    * open interest is the total quantity of all open orders for a given RIC
    * and direction at each price point
    */
  def getOpenInterest(ric: String, direction: Direction):Seq[OpenInterest] = {
    //get open orders ,in descending order
    val filteredOrders = exchangeDao.getOpenOrders.filter(order => (order.ric == ric) && (order.direction == direction))
    filteredOrders.sortBy(_.id).reverse.map(order => OpenInterest(order.quantity, order.price))
  }

  /**
    * get the average execution price per unit of all executions for a gven RIC
    * sum(quantity * execution price) / total quantity
    */
  def getAverageExecutionPrice(ric: String):Option[BigDecimal] = {
    val filteredOrders = exchangeDao.getExecutedOrders.filter(order => (order.ric == ric && order.executed == true))
    val price = filteredOrders.isEmpty match {
      case false =>
        val averagePrice = filteredOrders.map(order => order.price * order.quantity).sum / filteredOrders.map(order => order.quantity).sum
        Some(averagePrice.setScale(4, BigDecimal.RoundingMode.HALF_UP))
      case true => None
    }
    price
  }

  //if sell negate the quantity
  private def getCorrectedQuanity(quantity:Int, order:Order): Int = {
    val correctedQty = order.direction match {
      case Direction.buy => order.quantity
      case Direction.sell => order.quantity * -1
    }
    quantity + correctedQty
  }


  /**
    * Sum of quantities of executed orders for a given RIC and user
    */
  def getExecutedQuantity(ric: String, user: String):Option[Int] = {
    val filteredOrders = exchangeDao.getExecutedOrders.filter(order => (order.ric == ric && order.user == user))
    val executedQuantity = filteredOrders.isEmpty match {
      case false => Some(filteredOrders.foldLeft(0)((qty,order) => getCorrectedQuanity(qty, order)))
      case true => Some(0)
    }
    executedQuantity
  }

}
