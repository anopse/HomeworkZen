package homeworkzen.domain.utils

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}

object ConfigPreset {
  val default: Config = ConfigFactory.empty()

  def addPersistence(config: Config): Config =
    ConfigFactory.parseString(
      """
        akka {
          persistence {
            journal.plugin = "inmemory-journal"
            snapshot-store.plugin = "inmemory-snapshot-store"
          }
        }
        inmemory-journal {
          event-adapters {
             tagging = "homeworkzen.domain.utils.TaggingEventAdapter"
          }
          event-adapter-bindings {
             "homeworkzen.domain.command.message.Event" = tagging
          }
        }
      """).withFallback(config)

  val defaultWithPersistence: Config = addPersistence(default)
}
