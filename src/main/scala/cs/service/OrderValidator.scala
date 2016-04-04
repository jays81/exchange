package cs.service


/**
  * matches (SELL|BUY) [0-9]+ [A-Z][A-Z][A-Z].[A-Z] @ [0-9]+(\.[0-9][0-9]?)?
  * eg SELL 1000 VOD.L @ 100.2
  */
class OrderValidator {
  private val pattern = "(SELL|BUY) [0-9]+ [A-Z][A-Z][A-Z].[A-Z] @ [0-9]+(\\.[0-9][0-9]?)?"

  def validateOrder(orderToValidate: Option[String]):Boolean = {
    orderToValidate.map(order => order.matches(pattern)).getOrElse(false)
  }

}
