package dev.yggdrasil.injection.project.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Scaling
import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.ui.Components.{Shape, Visual}
import dev.yggdrasil.injection.framework.ui.{ECSActor, ECSScreen}
import dev.yggdrasil.injection.framework.util.dir2deg
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction, GridEntity, GridPosition, Pushable, Space}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, emptyGrid, parentOf, putGridEntity}
import dev.yggdrasil.injection.project.ecs.Systems.SequencedMovement
import dev.yggdrasil.injection.util.LoopedVector

class LevelScreen extends ECSScreen {
  override type Globals = this.type

  override def initialState: GameState = {
    // Should load a level from a file
    // For now it's just a test level

    var storage = EntityStorage.empty
    val gridEntity = Entity.fromComponents()
    val gridID = gridEntity.id

    val (entities, lookup) = emptyGrid(10, 20, gridID)

    storage = entities.foldLeft(storage)((s, e) => s.updated(e))

    // Create and add the arrows
    val arrowEntity = arrow(Direction.UP)
    val arrowEntity2 = arrow(Direction.RIGHT)

    storage = storage.updated(arrowEntity).updated(arrowEntity2)

    // Put the arrows in the grid
    storage = putGridEntity(arrowEntity, GridPosition(0, 0, gridID), storage)
    storage = putGridEntity(arrowEntity2, GridPosition(0, 1, gridID), storage)

    // Create the movement sequence
    val sequence = LoopedVector[Int](Vector(arrowEntity.id, arrowEntity2.id))
    val systems: Set[System] = Set(SequencedMovement("sequence", Global.STEP_INTERVAL, 0, sequence))

    GameState(storage, systems)
  }

  override def makeActors(created: EntityStorage): Iterable[(Int, Actor)] = {

    // Make the arrows
    val renderable: Set[Entity] = created.join(classOf[Visual], classOf[Shape])
    val actors: Set[(Int, Actor)] = renderable.map(entity => {
      val shape = entity(classOf[Shape])
      val visual = entity(classOf[Visual])
      val texture = new TextureRegion()
      texture.setTexture(visual.texture)

      texture.setRegionWidth(visual.shape.width)
      texture.setRegionHeight(visual.shape.height)

      val actor = new ECSActor(texture, () => defaultClickAction(entity))

      actor.setOrigin(visual.shape.width/2, visual.shape.height/2)

      actor.setScaleX(shape.width.toFloat/visual.shape.width)
      actor.setScaleY(shape.height.toFloat/visual.shape.height)

      if(visual.directed)
        actor.setRotation(dir2deg(entity(classOf[Direction])))

      val pos = entity.getInstance(classOf[GridPosition]).getOrElse(
        parentOf(entity, gameState.entityStorage).get(classOf[GridPosition])
      )
      actor.setPosition(pos.x * shape.width, pos.y * shape.height)
      entity.id -> actor
    })

    actors
  }

  override def changeActors(changed: EntityStorage): Unit = {
    val gridEntities = changed.join(classOf[GridEntity], classOf[Visual], classOf[Shape])
    gridEntities.foreach(entity => {
      val pos = parentOf(entity, gameState.entityStorage).get(classOf[GridPosition])
      val shape = entity(classOf[Shape])
      actors.fromEntity(entity).foreach(_.addAction(Actions.moveTo(
        pos.x * shape.width,
        pos.y * shape.height,
        Global.STEP_INTERVAL
      )))
    })
  }

  override def resize(width: Int, height: Int): Unit = ()

  override def pause(): Unit = ()

  override def resume(): Unit = ()

  override def hide(): Unit = ()

  override def dispose(): Unit = ()
}
