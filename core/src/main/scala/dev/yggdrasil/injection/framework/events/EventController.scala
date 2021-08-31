package dev.yggdrasil.injection.framework.events

import com.badlogic.gdx.InputMultiplexer
import dev.yggdrasil.injection.framework.events.EventController.useBind
import dev.yggdrasil.injection.framework.ui.EventActor
import dev.yggdrasil.injection.framework.util.ClassMap

class EventController extends InputMultiplexer {
  override def keyDown(keycode: Int): Boolean = {
    useBind(keycode)
//    super.keyDown(keycode)
    true
  }
}

object EventController {
  case class UnitEvent() extends Event
  case class Select(entityID: Int) extends Event
  case class Deselect(entityID: Int) extends Event

  private var hovered: Option[EventActor] = None
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

  def setHover(actor: EventActor): Unit = hovered = Some(actor)

  def removeHover(actor: EventActor): Unit = if(hovered.contains(actor)) hovered = None

  // Problem, useBind method doesn't take into account the required context, IE what's being hovered over.
  def useBind(keycode: Int): Unit = hovered.flatMap(_.hoverEvents.get(keycode)) match {
    case Some(event) => add(event) // Attempt to use the hovered event first.
    case None => inputMap.get(keycode).foreach(add) // Then use the default event, if there is one.
  }

  // Todo: Load binds from file
}
