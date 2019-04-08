package io.monadless.stdlib

import org.scalatest.MustMatchers

import io.monadless.impl.TestSupport

class MonadlessOptionSpec
  extends org.scalatest.FreeSpec
  with MustMatchers
  with MonadlessOption
  with TestSupport[Option] {

  def get[T](t: Option[T]) = t.get

  def fail[T]: T = throw new Exception

  val one = Option(1)
  val two = Option(2)

  "apply" in
    runLiftTest(1) {
      1
    }

  "collect" in
    runLiftTest(3) {
      unlift(one) + unlift(two)
    }

  "map" in
    runLiftTest(2) {
      unlift(one) + 1
    }

  "flatMap" in
    runLiftTest(3) {
      val a = unlift(one)
      a + unlift(two)
    }

  "rescue" - {
    "success" in {
      """
      lift {
        try unlift(one)
        catch {
          case e: Exception => unlift(two)
        }
      }
      """ mustNot compile
    }
    "failure" in {
      """
      lift {
        try fail[Int]
        catch {
          case e: Exception => unlift(one)
        }
      }
      """ mustNot compile
    }
  }

  "ensure" - {
    "success" in {
      """
      lift {
        var i = 0
        def c() = i += 1
        try unlift(one)
        finally {
          c()
        }
        i
      }
      """ mustNot compile
    }
    "failure" in {
      """
      lift {
        var i = 0
        def c() = i += 1
        try {
          try unlift(one) / fail[Int]
          finally {
            c()
          }
        } catch {
          case e: Exception => 1
        }
        i
      }
      """ mustNot compile
    }
  }
}
