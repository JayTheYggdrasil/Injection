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
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction, Grid, GridPosition, Pushable, Space}
import dev.yggdrasil.injection.project.ecs.Systems.SequencedMovement
import dev.yggdrasil.injection.util.LoopedVector

class LevelScreen extends ECSScreen {
  override type Globals = this.type

  override def initialState: GameState = {
    // Should load a level from a file
    // For now it's just a test level

    val storage = EntityStorage.empty
    val gridEntity = Entity.fromComponents()
    def makeSpace(x: Int, y: Int): Entity = Entity.fromComponents(Space(), Global.SPACE_SHAPE, GridPosition(x, y, gridEntity.id), Global.textures.space)

    val arrowEntity = Entity.fromComponents(
      Arrow(),
      Direction.UP,
      GridPosition(0, 0, gridEntity.id),
      Pushable(),
      Global.SPACE_SHAPE,
      Global.textures.arrow
    )

    val arrowEntity2 = Entity.fromComponents(
      Arrow(),
      Direction.RIGHT,
      GridPosition(0, 1, gridEntity.id),
      Pushable(),
      Global.SPACE_SHAPE,
      Global.textures.arrow
    )

    val sequence = LoopedVector[Int](Vector(arrowEntity.id, arrowEntity2.id))

    val populatedGrid = gridEntity.updated(Entity.gridWithEntities(Set(arrowEntity, arrowEntity2), makeSpace))

    val spaces = populatedGrid.getInstance(classOf[Grid]).get.origin.allDefaults

    val populatedStorage = {
      val s = storage.updated(populatedGrid).updated(arrowEntity).updated(arrowEntity2)
      spaces.foldLeft(s)((store, space) => store.updated(space))
    }

    val systems: Set[System] = Set(SequencedMovement("sequence", Global.STEP_INTERVAL, 0, sequence))

    GameState(populatedStorage, systems)
  }

  override def makeActors(created: EntityStorage): Iterable[(Int, Actor)] = {

    // Make the arrows
    val renderable = created.join(classOf[Visual], classOf[GridPosition], classOf[Shape])
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

      val pos = entity(classOf[GridPosition])
      actor.setPosition(pos.x * shape.width, pos.y * shape.height)
      entity.id -> actor
    })

    actors
  }

  override def changeActors(changed: EntityStorage): Unit = {
    val arrows = changed.join(classOf[GridPosition], classOf[Pushable], classOf[Shape])
    arrows.foreach(arrow => {
      val pos = arrow(classOf[GridPosition])
      val shape = arrow(classOf[Shape])
      actors.fromEntity(arrow).foreach(_.addAction(Actions.moveTo(
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
