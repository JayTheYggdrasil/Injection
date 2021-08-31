package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.{Actor, InputEvent}
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import dev.yggdrasil.injection.framework.events.{Event, EventController}
import dev.yggdrasil.injection.framework.events.EventController.UnitEvent

abstract class EventActor(texture: TextureRegion) extends Image(texture) {
  private val that = this
  setWidth(texture.getRegionWidth.toFloat)
  setHeight(texture.getRegionHeight.toFloat)

  val hoverEvents: Map[Int, Event] = Map.empty

  def onClick: Event = UnitEvent()
  def onEnter: Event = UnitEvent()
  def onExit: Event = UnitEvent()

  addListener(new ClickListener() {
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = EventController.add(onClick)

    override def enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor): Unit = {
      EventController.add(onEnter)
      EventController.setHover(that)
    }

    override def exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor): Unit = {
      EventController.add(onExit)
      EventController.removeHover(that)
    }
  })
}
