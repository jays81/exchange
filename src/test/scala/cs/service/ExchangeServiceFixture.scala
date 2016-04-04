package cs.service

import cs.dao.InMemoryExchangeDAO

/**
  * Created by jay on 04/04/16.
  */
trait ExchangeServiceFixture {

  val exchangeService = new ExchangeService(new InMemoryExchangeDAO, new OrderValidator, new OrderParser, new SimpleIdGenerator)

}
