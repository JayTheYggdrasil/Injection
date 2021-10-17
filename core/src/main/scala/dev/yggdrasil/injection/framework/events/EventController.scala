package dev.yggdrasil.injection.framework.events

import com.badlogic.gdx.InputMultiplexer
import dev.yggdrasil.injection.framework.ecs.GameState
import dev.yggdrasil.injection.framework.ecs.Systems.EventSystem
import dev.yggdrasil.injection.framework.events.EventController.useBind
import dev.yggdrasil.injection.framework.ui.EventActor

class EventController extends InputMultiplexer {
  override def keyDown(keycode: Int): Boolean = {
    useBind(keycode)
//    super.keyDown(keycode)
    true
  }
}

object EventController {
  case class UnitEvent() extends EventSystem("Unit") {
    override def handleEvent(gameState: GameState): GameState = gameState
  }

  private var hovered: Option[EventActor] = None
  private var events: List[EventSystem] = Nil

  private var inputMap: Map[Int, EventSystem] = Map.empty

  def add(value: EventSystem): Unit = value match {
    case _: UnitEvent => ()
    case _ => events = events.appended(value)
  }

  def consumeAll(): List[EventSystem] = {
    val e = events
    events = Nil
    e
  }

  def bind(keycode: Int, event: EventSystem): Unit = inputMap = inputMap.updated(keycode, event)

  def setHover(actor: EventActor): Unit = hovered = Some(actor)

  def removeHover(actor: EventActor): Unit = if(hovered.contains(actor)) hovered = None

  // Problem, useBind method doesn't take into account the required context, IE what's being hovered over.
  def useBind(keycode: Int): Unit = hovered.flatMap(_.hoverEvents.get(keycode)) match {
    case Some(event) => add(event) // Attempt to use the hovered event first.
    case None => inputMap.get(keycode).foreach(add) // Then use the default event, if there is one.
  }

  // Todo: Load binds from file
}
