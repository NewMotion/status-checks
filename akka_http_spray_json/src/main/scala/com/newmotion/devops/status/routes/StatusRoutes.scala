package com.newmotion.devops.status.routes

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.newmotion.devops.status.StatusCheck.Checks
import com.newmotion.devops.status.sprayjson.StatusJsonProtocol
import akka.http.scaladsl.model.StatusCodes._

import scala.util.{Failure, Success}

object StatusRoutes extends StatusRoutes

trait StatusRoutes extends StatusJsonProtocol {
  def statusRoute(checks:Checks): Route =
    get {
      path("status") {
        onComplete(checks.run()) {
          case Success(results) => complete(results)
          case Failure(ex)    => complete(InternalServerError -> s"An error occurred: ${ex.getMessage}")
        }
      } ~
      path("heartbeat") {
        complete(HttpResponse()) //200, no body, no headers
      }
    }
}
