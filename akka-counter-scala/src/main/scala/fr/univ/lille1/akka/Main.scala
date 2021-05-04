import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.io.Source

object AssignmentPart1 extends App {

  //Define case classes so CountActor knows what format method should be in
  case class readFromFile(actorRef0: ActorRef, actorRef1: ActorRef, source_file : scala.io.BufferedSource)
  // TODO: rename methods
  case class StartCounting(actorRef: ActorRef, str : String)
  case class CountSecond(s : String)
  case class Combine(final_str : String)

  class CountActor extends Actor {
    def receive: Receive = {
      //read from file for every line
      // launch chain of actors (0,1,2) for every line
      case readFromFile(actor1, actor2, source_file) =>
        for (line <- source_file.getLines()) {
          actor1 ! StartCounting(actor2, line)
        }

      // this actor marks a file by spevific labels
      case StartCounting(actor2, s) =>
        //TODO: take line, set label and return string with label
        actor2 ! CountSecond(s)

      // Modify marked string by rule and return it to final processing
      case CountSecond(s) =>
        var tmp_str = s
        if (tmp_str.contains("int")) {
          tmp_str = s.replace("int", "float")
        }
        // TODO: 1. add more changes, like data types: float , int, char ,string
        //       2. dont read exceptions: return , int main and etc
        sender() ! Combine(tmp_str)

      // Write in file the final result line by line
      case Combine(final_str) =>
        // TODO: create a new file , write a line
        println(final_str)
    }
  }

  //Store content from txt file into a String
  val source_file = Source.fromFile("text.txt")

  //Create the actor system and actors, and send a message to CountActor with a method
  val system = ActorSystem("count")
  val actor0 = system.actorOf(Props[CountActor], name = "count-actor0")
  val actor1 = system.actorOf(Props[CountActor], name = "count-actor1")
  val actor2 = system.actorOf(Props[CountActor], name = "count-actor2")

  actor0 ! readFromFile(actor1, actor2, source_file)

  system.terminate()
}
