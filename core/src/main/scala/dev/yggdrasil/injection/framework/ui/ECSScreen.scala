package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, stepState}
import dev.yggdrasil.injection.framework.ui.Components.Clicked
import dev.yggdrasil.injection.framework.util.StateDifferences

abstract class ECSScreen extends Screen {
  private var _batch: Option[SpriteBatch] = None
  private var _stage: Option[Stage] = None
  private var _viewport: Option[Viewport] = None

  private var clickedQueue: List[Int] = List()

  type Globals

  def batch: SpriteBatch = _batch.get
  def stage: Stage = _stage.get
  def viewport: Viewport = _viewport.get

  override def show() = {
    _batch = Some(new SpriteBatch())
    _stage = Some(new Stage())
    _viewport = Some(new StretchViewport(900, 900))

    val added = makeActors(gameState.entityStorage)
    added.foreach(s => actors.addActor(s._1, s._2))
    added.foreach(s => stage.addActor(s._2))

    Gdx.input.setInputProcessor(stage)
  }

  protected val actors: ActorStorage = ActorStorage.empty

  var gameState: GameState = initialState

  def initialState: GameState

  def clickEntity(id: Int): Unit = {
    clickedQueue = id :: clickedQueue
  }

  def addClick(state: GameState, id: Int): GameState = {
    println("clicked this: " + id)
    val storage = state.entityStorage
    val entity = storage(id).updated(Clicked())
    state.copy(entityStorage = storage.updated(entity))
  }

  def defaultClickAction(entity: Entity): Unit = clickEntity(entity.id)

  override def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Update the game state
    val steppedState = stepState(delta, gameState.clearChanged())

    // Add the click input(s)
    val newState = clickedQueue.foldLeft(steppedState)(addClick)
    clickedQueue = List.empty

    // Update Screen
    val (diffAdded, diffRemoved, diffChanged) = StateDifferences(gameState, newState)

    val added = makeActors(diffAdded)
    added.foreach(s => actors.addActor(s._1, s._2))
    added.foreach(s => stage.addActor(s._2))

    changeActors(diffChanged)
    removeActors(diffRemoved)

    stage.act(delta)
    stage.draw()

    // Do the state transition
    gameState = newState
  }

  def makeActors(created: EntityStorage): Iterable[(Int, Actor)]
  def changeActors(changed: EntityStorage): Unit
  def removeActors(removed: EntityStorage): Unit = {
    val entities = removed.allEntities.values
    entities.map(e => actors.removeEntity(e)).foreach(_.foreach(_.remove()))
  }
}
