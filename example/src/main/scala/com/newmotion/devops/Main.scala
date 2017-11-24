package com.newmotion.devops

import java.util.concurrent.TimeoutException

import com.newmotion.devops.status.{Result, StatusCheck}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StatusCheck with App {
  val takes_six = (name:String) => {
    println(s"F $name")
    Future[Result] {
      Thread.sleep(6000)
      println(s"E $name")
      Result(name, status = true, Map("response_time"-> "O.034"))
    }
  }
  val takes_five = (name:String) => {
    println(s"F $name")
    Future[Result] {
      Thread.sleep(5000)
      println(s"E $name")
      Result(name, status = true, Map())
    }
  }

  val exceptionInInitializerError = (name:String) => {
    Future {
      throw new TimeoutException("time's up")
    }
  }

  val results = Checks("example.application")
    .internal("me", (name:String) => {
      println(s"F $name")
      Future{
        Thread.sleep(500)
        println(s"E $name")
        Result(name, true)
      }
    })
    .internal("me too", (name:String) => {println(s"F $name");Future{println(s"E $name");Result(name, true)}})
    .external("check_one", takes_six)
    .external("times_out", exceptionInInitializerError)
    .external("check_two", takes_five)
    .external("check_three", (name:String) => {println(s"F $name");Future{println(s"E $name");Result(name, true, Map())}})
    .run()
  println(Await.result(results, Duration.Inf))
}
