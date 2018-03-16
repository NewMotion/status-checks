package com.newmotion.devops.status.sprayjson

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.newmotion.devops.status._
import spray.json._

trait StatusJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object ImportanceJF extends RootJsonFormat[Importance] {
    override def read(value: JsValue): Importance = value match {
      case JsString(name) => Importance.Critical //Importance.levels.map(_.toString).find(name).getOrElse(throw deserializationError(s"Unknown Importance Level $name"))
      case _ => throw deserializationError("bla")
    }

    override def write(obj: Importance) = JsString(obj.toString)
  }

  implicit val resultJF: RootJsonFormat[Result] = jsonFormat2(Result)
  implicit val extendedResultJF: RootJsonFormat[ExtendedResult] = jsonFormat3(ExtendedResult)
  implicit val resultsJF: RootJsonFormat[Results] = jsonFormat3(Results)

}

object StatusJsonProtocol extends StatusJsonProtocol
