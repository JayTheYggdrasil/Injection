package dev.yggdrasil.injection.project.ui.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dev.yggdrasil.injection.framework.ecs.{Entity, System}
import dev.yggdrasil.injection.framework.events.Event
import dev.yggdrasil.injection.framework.ui.ECSActor
import dev.yggdrasil.injection.project.Events.MakeArrow
import dev.yggdrasil.injection.project.ecs.Components.{GridPosition, Space}
import dev.yggdrasil.injection.project.ui.Global


object SpaceActor {
  val texture = new Texture("Space.png")

  def makesFrom(entity: Entity): Boolean =
    entity.componentMap.contains(classOf[Space])

  def apply(entity: Entity): ECSActor = {
    // Create a new texture region
    val textureRegion: TextureRegion = new TextureRegion()
    textureRegion.setTexture(texture)
    textureRegion.setRegion(texture)

    // Define the new actor
    val actor = new GridActor(entity.id, textureRegion) {
      override def update(gameState: System.GameState): Unit = ()
      override def onClick: Event = MakeArrow(entity.id)
    }

    // Set it's position
    val pos = entity(classOf[GridPosition])
    actor.setPosition(
      (pos.x * Global.GRID_SIZE).toFloat,
      (pos.y * Global.GRID_SIZE).toFloat
    )

    actor
  }
}
