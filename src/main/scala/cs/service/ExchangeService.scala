package cs.service

import cs.Direction.Direction
import cs.dao.ExchangeDAO
import cs.{Direction, ExecutionResult, Order}

/**
  * Created by jay on 02/04/16.
  */
class ExchangeService(exchangeDao:ExchangeDAO, orderValidator:OrderValidator, orderParser:OrderParser, idGenerator: IdGenerator) {

  /**
    * Add an order, this will first validate and parse the order
    * creating an order, this will go on to check the order against existing orders
    * to check if it can be matched
    * If orders match they are said to be executed
    *
    * @param order
    * @param user
    * @return
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
    *
    * @param order
    * @return
    */
  private def addOrder(order: Order): ExecutionResult = {
    exchangeDao.addNewOrder(order)
    val filteredOrders = exchangeDao.getOpenOrders.filter(openOrder => matchOrder(order, openOrder))
    println("orders found " + filteredOrders)
    val matchedOrder = getPriceFromMultipleMatches(order, filteredOrders)

    println("matched order " + matchedOrder)

    val executionResult = matchedOrder match {
      case Some(openOrder) => {
        //remove executed orders
        exchangeDao.addExecutedOrder(order, openOrder)
        ExecutionResult(executed = true,
          orderDirection = order.direction,
          matchDirection = getOppositeDirection(order.direction),
          matchPrice = openOrder.price,
          executionPrice = order.price)
      }
      case None => ExecutionResult(executed = false,
                                  orderDirection = order.direction,
                                  executionPrice = order.price)
    }

    executionResult
  }

  //for sell order get the highest price, for buy get the lowest
  private def getPriceFromMultipleMatches(order: Order, filteredOrders: Seq[Order]): Option[Order] = {
    val matchedOrder = order.direction match {
      case Direction.sell => filteredOrders.nonEmpty match {
        case true => Some(filteredOrders.reduceLeft(max))
        case false => None
      }
      case Direction.buy => filteredOrders.nonEmpty match {
        case true => Some(filteredOrders.reduceLeft(min))
        case false => None
      }
    }
    matchedOrder
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

  private def max(order1: Order, order2: Order): Order = if (order1.price > order2.price) order1 else order2

  private def min(order1: Order, order2: Order): Order = if (order1.price < order2.price) order1 else order2

  private def getOppositeDirection(direction: Direction): Direction = direction match {
    case Direction.buy => Direction.sell
    case Direction.sell => Direction.buy
  }

}
