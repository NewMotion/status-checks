package com.newmotion.devops.status.sprayjson

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.newmotion.devops.status.{Result, Results}
import spray.json.DefaultJsonProtocol

trait StatusJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val resultJF = jsonFormat3(Result)
  implicit val resultsJF = jsonFormat3(Results)
}

object StatusJsonProtocol extends StatusJsonProtocol
