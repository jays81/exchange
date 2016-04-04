package cs.service

import scala.util.matching.Regex

/**
  * matches (SELL|BUY) [0-9]+ [A-Z][A-Z][A-Z].[A-Z] @ [0-9]+(\.[0-9][0-9]?)?
  */
class OrderValidator {
  private val pattern = "(SELL|BUY) [0-9]+ [A-Z][A-Z][A-Z].[A-Z] @ [0-9]+(\\.[0-9][0-9]?)?"
  def validateOrder(orderToValidate: String):Boolean = {
    orderToValidate.matches(pattern)
  }

}
