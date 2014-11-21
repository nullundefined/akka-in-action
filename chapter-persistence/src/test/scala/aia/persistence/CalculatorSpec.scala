package aia.persistence

import akka.actor._
import akka.testkit._
import org.scalatest._

import PersistenceSpec._

class CalculatorSpec extends PersistenceSpec(ActorSystem("test"))
    with WordSpecLike
    with PersistenceCleanup {

  "The Calculator" should {
    "Recover last known result after crash" in {
      val calc = system.actorOf(Calculator.props, Calculator.name)
      calc ! Calculator.Add(1d)
      calc ! Calculator.GetResult
      expectMsg(1d)
      calc ! Calculator.Subtract(0.5d)
      calc ! Calculator.GetResult
      expectMsg(0.5d)

      watch(calc)
      system.stop(calc)
      expectTerminated(calc)

      val calcResurrected = system.actorOf(Calculator.props, Calculator.name)
      calcResurrected ! Calculator.Add(1d)
      calcResurrected ! Calculator.GetResult
      expectMsg(1.5d)
    }
  }
}