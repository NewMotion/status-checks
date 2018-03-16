package com.newmotion.devops

import java.util.concurrent.TimeoutException

import com.newmotion.devops.status._
import com.newmotion.devops.status.sprayjson.StatusJsonProtocol

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import spray.json._

object Main extends StatusCheck with StatusJsonProtocol with App {
  val takes_six = () => {
    println(s"F takes_six")
    Future[Result] {
      Thread.sleep(6000)
      println(s"E takes_six")
      Result(status = true, Map("response_time"-> "O.034"))
    }
  }
  val takes_five = () => {
    println(s"F takes_five")
    Future[Result] {
      Thread.sleep(5000)
      println(s"E takes_five")
      Result(status = true, Map())
    }
  }

  val exceptionInInitializerError = () => {
    Future {
      throw new TimeoutException("time's up")
    }
  }

  val results = Checks("example.application")
    .internal("me", Importance.Critical, () => {
      println(s"F me")
      Future{
        Thread.sleep(500)
        println(s"E me")
        Result(true)
      }
    })
    .internal("me too", Importance.Major, () => {println(s"F me too");Future{println(s"E me too");Result(true)}})
    .external("check_one", Importance.Critical, takes_six)
    .external("times_out", Importance.Major, exceptionInInitializerError)
    .external("check_two", Importance.Minor, takes_five)
    .external("check_three", Importance.Critical, () => {println(s"F check_three");Future{println(s"E check_tree");Result(true, Map())}})
    .run()

  val r = Await.result(results, Duration.Inf)
  println(r)
  println(r.toJson.prettyPrint)
}
