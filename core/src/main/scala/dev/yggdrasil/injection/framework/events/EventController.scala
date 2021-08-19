package dev.yggdrasil.injection.framework.events

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Input.Keys
import dev.yggdrasil.injection.framework.events.EventCollection
import dev.yggdrasil.injection.framework.events.EventController.useBind
import dev.yggdrasil.injection.framework.util.ClassMap
import dev.yggdrasil.injection.project.Events.MakeArrow

class EventController extends InputMultiplexer {
  override def keyDown(keycode: Int): Boolean = {
    useBind(keycode)
    super.keyDown(keycode)
  }
}

object EventController {
  case class UnitEvent() extends Event

  private var events = ClassMap.empty[Event]

  def apply[A <: Event](clss: Class[A]): Set[A] = events(clss)

  private var inputMap: Map[Int, Event] = Map.empty

  def add[C <: Event](value: C): Unit = value match {
    case _: UnitEvent => ()
    case _ => {
      println(value)
      events = events.add[C](value)
    }
  }

  def remove[C <: Event](value: C): Unit = events = events.remove[C](value)

  def bind(keycode: Int, event: Event): Unit = inputMap = inputMap.updated(keycode, event)

  def useBind(keycode: Int): Unit = inputMap.get(keycode).foreach(add)

  // Todo: Load binds from file
}
