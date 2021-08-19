package dev.yggdrasil.injection.framework

import dev.yggdrasil.injection.framework.util.ClassMap
/*
 * Events should only originate from user input, and not elsewhere.
 */
package object events {

  trait Event

  type EventCollection = ClassMap[Event]
//  case class EventCollection private(eventStorage: Map[String, Set[_ <: Event]]) {
//    def get[A <: Event](clss: Class[A]): Option[Set[A]] = eventStorage.get(clss.getName).asInstanceOf[Option[Set[A]]]
//    def apply[A <: Event](clss: Class[A]): Set[A] = eventStorage(clss.getName).asInstanceOf[Set[A]]
//    def remove[A <: Event](value: A): EventCollection = {
//      val clss: String = value.getClass.getName
//      val _set = eventStorage.get(clss).asInstanceOf[Option[Set[A]]]
//      val newStore = _set.map(s => {
//        val set = s + value
//        eventStorage.updated(clss, set)
//      }).getOrElse(eventStorage)
//      EventCollection(newStore)
//    }
//    def add[A <: Event](value: A): EventCollection = ???
//  }
}
