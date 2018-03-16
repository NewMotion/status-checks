package com.newmotion.devops.status

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Result(status:Boolean, data:Map[String, String] = Map())
case class ExtendedResult(name:String, importance: Importance, result: Result)
case class Results(name: String, status: Boolean, internals: Seq[ExtendedResult], externals: Seq[ExtendedResult])

sealed trait Importance
object Importance {
  /**
    * my application will not work without it
    */
  case object Critical extends Importance

  /**
    * parts of my application will not work without it
    */
  case object Major extends Importance

  /**
    * my application will mostly work without it
    */
  case object Minor extends Importance

  val levels = List(Critical, Major, Minor)
}


object StatusCheck extends StatusCheck

/**
  * Contains behaviour to compose and execute checks on dependencies to determine the health of an application
  * and report back in a consistent manner
  */
trait StatusCheck {
  type Check = () => Future[Result]

  object Checks {
    def apply(id: String, externals: Map[(String, Importance), Check], internals:Map[(String, Importance), Check]): Checks = new Checks(id, externals,internals)

    def apply(id:String): Checks = new Checks(id, Map[(String, Importance), Check](),Map[(String, Importance), Check]())
  }

  /**
    * Immutable class to hold the checks
    * @param id the id of this check, usually the name of the application
    * @param externals checks that determine dependencies are healthy
    * @param internals checks that determine if this application is healthy
    */
  class Checks(val id: String, val externals:Map[(String, Importance), Check], val internals:Map[(String, Importance), Check]) {
    /**
      * Add a check for dependencies that are internal to the microservice, the application itself
      * and those that are required for this application to run, like databases
      * @param name simple name for the dependent system
      * @param importance the importance of the dependency
      * @param check function that takes the name and preforms the check
      * @return The Result of the check
      */
    def internal(name: String, importance: Importance, check: Check):Checks = Checks(id, externals, internals + ((name, importance) -> check))

    /**
      * Adds a check for dependencies that are not required for this application to run, like other applications, message queues
      * @param name simple name for the dependent system
      * @param importance the importance of the dependency
      * @param check function that takes the name and preforms the check
      * @return The Result of the check
      */
    def external(name: String, importance: Importance, check: Check): Checks = Checks(id, externals + ((name, importance) -> check), internals)

    private def recoverResult(e:Throwable): Result =
      Result(status = false, Map("exception" -> e.getMessage))

    /**
      * composes the checks in a Future[Results]
      * @return A Future of all the checks added
      */
    def run() :Future[Results] = {

      require(internals.nonEmpty, "you need to call .internal at least once before running")
      //TODO these are run sequential, refactor so all Futures run in parallel
      for {
        int <- Future.traverse(internals)((x) => x._2().recover{ case e => recoverResult(e) }
                      .map(ExtendedResult(x._1._1,x._1._2, _))).map(r => r.toSeq)
        ext <- Future.traverse(externals)((x) => x._2().recover{ case e => recoverResult(e) }
                      .map(ExtendedResult(x._1._1,x._1._2, _))).map(r => r.toSeq)
        status <- Future((int.map(_.result.status) ++ ext.map(_.result.status)).reduce(_ && _))
      } yield Results(id, status, int, ext)


    }
  }
}

