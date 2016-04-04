package cs.service

import cs.dao.InMemoryExchangeDAO


trait ExchangeServiceFixture {

  val exchangeService = new ExchangeService(new InMemoryExchangeDAO, new OrderValidator, new OrderParser, new SimpleIdGenerator)

}
