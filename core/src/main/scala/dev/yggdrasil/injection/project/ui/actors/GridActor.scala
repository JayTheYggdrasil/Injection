package dev.yggdrasil.injection.project.ui.actors

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import dev.yggdrasil.injection.framework.ecs.GameState
import dev.yggdrasil.injection.framework.ui.Components.Shape
import dev.yggdrasil.injection.framework.ui.{ECSActor, ECSActorFactory}
import dev.yggdrasil.injection.project.ecs.Components.GridPosition
import dev.yggdrasil.injection.project.ecs.Entities.{onGrid, parentOf}
import dev.yggdrasil.injection.project.ui.Global

abstract class GridActor(override val id: Int, texture: TextureRegion) extends ECSActor(id, texture) {
  protected val txtSide: Float = texture.getTexture.getWidth.toFloat
  setScaleX(Global.GRID_SIZE/txtSide)
  setScaleY(Global.GRID_SIZE/txtSide)
  setOrigin(getHeight/2, getWidth/2)

  override def update(gameState: GameState): Unit = {
    val storage = gameState.storage
    val entity = storage(id)

    val pos = if(onGrid(entity)) {
      parentOf(entity, storage).get.apply[GridPosition]
    } else {
      entity[GridPosition]
    }

    val shape = entity[Shape]

    addAction(Actions.moveTo(
      (pos.x * shape.width).toFloat,
      (pos.y * shape.height).toFloat,
      Global.STEP_INTERVAL
    ))
  }
}
