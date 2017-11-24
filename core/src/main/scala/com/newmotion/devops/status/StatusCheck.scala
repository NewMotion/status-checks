package com.newmotion.devops.status

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Result(name: String, status:Boolean, data:Map[String, String] = Map())
case class Results(name: String, internals: Seq[Result], externals: Seq[Result])

object StatusCheck extends StatusCheck

/**
  * Contains behaviour to compose and execute checks on dependencies to determine the health of an application
  * and report back in a consistent manner
  */
trait StatusCheck {
  type Check = (String) => Future[Result]

  object Checks {
    def apply(id: String, externals: Map[String, Check], internals:Map[String, Check]): Checks = new Checks(id, externals,internals)

    def apply(id:String): Checks = new Checks(id, Map[String, Check](),Map[String, Check]())
  }

  /**
    * Immutable class to hold the checks
    * @param id the id of this check, usually the name of the application
    * @param externals checks that determine dependencies are healthy
    * @param internals checks that determine if this application is healthy
    */
  class Checks(val id: String, val externals:Map[String, Check], val internals:Map[String, Check]) {
    /**
      * Add a check for dependencies that are internal to the microservice, the application itself
      * and those that are required for this application to run, like databases
      * @param name simple name for the dependent system
      * @param check function that takes the name and preforms the check
      * @return The Result of the check
      */
    def internal(name:String, check:Check):Checks = Checks(id, externals, internals + (name -> check))

    /**
      * Adds a check for dependencies that are not required for this application to run, like other applications, message queues
      * @param name simple name for the dependent system
      * @param check function that takes the name and preforms the check
      * @return The Result of the check
      */
    def external(name: String, check: Check): Checks = Checks(id, externals + (name -> check), internals)

    private def recoverResult(x: String, e:Throwable): Result = Result(x, status = false, Map("exception" -> e.getMessage))

    /**
      * composes the checks in a Future[Results]
      * @return A Future of all the checks added
      */
    def run() :Future[Results] = {
      require(internals.nonEmpty, "you need to call .internal at least once before running")
      //TODO these are run sequential, refactor so all Futures run in parallel
      for {
        int <- Future.traverse(internals)((x) => x._2(x._1).recover{ case e => recoverResult(x._1, e) }).map(r => r.toSeq)
        ext <- Future.traverse(externals)((x) => x._2(x._1).recover{ case e => recoverResult(x._1, e) }).map(r => r.toSeq)
      } yield Results(id, int, ext)

    }
  }
}

