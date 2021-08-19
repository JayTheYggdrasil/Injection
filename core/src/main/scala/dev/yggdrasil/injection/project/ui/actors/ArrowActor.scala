package dev.yggdrasil.injection.project.ui.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dev.yggdrasil.injection.framework.ecs.System.GameState
import dev.yggdrasil.injection.framework.ecs.{Entity, System}
import dev.yggdrasil.injection.framework.events.Event
import dev.yggdrasil.injection.framework.ui.Components.Visual
import dev.yggdrasil.injection.framework.ui.ECSActor
import dev.yggdrasil.injection.project.Events.{MakeArrow, Select}
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, GridPosition}
import dev.yggdrasil.injection.project.ecs.Entities.parentOf
import dev.yggdrasil.injection.project.ui.Global

object ArrowActor {
  val texture = new Texture("Arrow.png")

  def makesFrom(entity: Entity): Boolean =
    entity.componentMap.contains(classOf[Arrow])

  def apply(entity: Entity, gameState: GameState): ECSActor = {
    // Create a new texture region

    val visual = entity(classOf[Visual])

    val textureRegion: TextureRegion = new TextureRegion()
    textureRegion.setTexture(texture)
    textureRegion.setRegion(texture)

    // Define the new actor
    val actor = new GridActor(entity.id, textureRegion) {
      override def update(gameState: GameState): Unit = {
        val me = gameState.entityStorage(id)
        val parent = parentOf(me, gameState.entityStorage)
        parent.foreach(p => {
          val pos = p(classOf[GridPosition])
          addAction(Actions.moveTo(pos.x * Global.GRID_SIZE, pos.y * Global.GRID_SIZE, Global.STEP_INTERVAL))
        })
      }

      override def onEnter: Event = {
        scaleMultiply(1.2f, 1.2f)
        Select(id)
      }

      override def onExit: Event = {
        scaleMultiply(1/1.2f, 1/1.2f)
        super.onExit
      }
    }

    // Set it's position
    val pos = parentOf(entity, gameState.entityStorage).get(classOf[GridPosition])
    actor.setPosition((pos.x * Global.GRID_SIZE).toFloat, (pos.y * Global.GRID_SIZE).toFloat)

    actor
  }
}
