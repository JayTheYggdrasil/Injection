package dev.yggdrasil.injection.framework

import dev.yggdrasil.injection.framework.util.ClassMap
/*
 * Events should only originate from user input, and not elsewhere.
 */
package object events {

  trait Event

  type EventCollection = ClassMap[Event]
}
