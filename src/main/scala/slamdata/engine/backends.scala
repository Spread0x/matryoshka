package slamdata.engine

import slamdata.engine.fp._
import slamdata.engine.config._

import scalaz.Foldable
import scalaz.std.list._

object BackendDefinitions {
  val MongoDB: BackendDefinition = BackendDefinition({
    case config : MongoDbConfig =>
      import slamdata.engine.physical.mongodb._
      import com.mongodb.{util => _, _}

      val tdb = util.createMongoDB(config) // FIXME: This will leak because Task will be re-run every time. Cache the DB for a given config.

      for {
        db <- tdb
      } yield Backend(MongoDbPlanner, MongoDbEvaluator(db), MongoDbFileSystem(db), Workflow.NativeWorkflowShow)
  })

  val All = Foldable[List].foldMap(MongoDB :: Nil)(identity)
}