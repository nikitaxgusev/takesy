import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.io.Source
import java.io.FileWriter
import java.io.IOException

object AssignmentPart1 extends App {

  //Define case classes so CountActor knows what format method should be in
  case class readFromFile(actorRef0: ActorRef, actorRef1: ActorRef, source_file : scala.io.BufferedSource)
  case class MarkLabel(actorRef: ActorRef, line : String)
  case class ModifyLine(mark_line : String)
  case class WriteLabel(final_line : String)

  class CountActor extends Actor {
    def receive: Receive = {
      //read from file for every line
      // launch chain of actors (0,1,2) for every line
      case readFromFile(actor1, actor2, source_file) =>
        for (line <- source_file.getLines()) {
          actor1 ! MarkLabel(actor2, line)
        }

      // this actor marks a file by spevific labels
      case MarkLabel(actor2, line) =>

        var tmp_line = line
        val label_type = "@type_"
        val label_init = "@init_"

        if (!line.contains("#include")
          && !line.contains("//")
          && !line.contains("int main() {")
          && !line.contains("return 0;")) {

          if (line.contains("int")) {
              tmp_line = label_type + "int@" + tmp_line
            if (!line.contains("=")) {
              tmp_line = label_init + "int@" + tmp_line
            }
          }
          else if (line.contains("std::string")) {
            tmp_line = label_type + "std::string@" + tmp_line
          }
          else if (line.contains("std::cout << ")) {
            tmp_line = label_type + "std::cout@" + tmp_line
          }
          else if (line.contains("float")) {
            if (!line.contains("=")) {
              tmp_line = label_init + "float@" + tmp_line
            }
          }
          else if (line.contains("bool")) {
            if (!line.contains("=")) {
              tmp_line = label_init + "bool@" + tmp_line
            }
          }
        }

        actor2 ! ModifyLine(tmp_line)

      // Modify marked string by rule and return it to final processing
      case ModifyLine(mark_line) =>
        var tmp_marked_str = mark_line
        if (!tmp_marked_str.contains("#include")
          && !tmp_marked_str.contains("//")
          && !tmp_marked_str.contains("int main() {")
          && !tmp_marked_str.contains("return 0;")) {
            // type
            if (tmp_marked_str.contains("@type")) {
              if (tmp_marked_str.contains("@type_int@")) {
                tmp_marked_str = tmp_marked_str.replace("@type_int@", "").replace("int", "float")
                if (mark_line.contains("@init_int")) {
                  tmp_marked_str = tmp_marked_str.replace("@init_float@", "").replace(";", " = 0.0;")
                }
                else {
                  tmp_marked_str = tmp_marked_str.replace(";", ".0;")
                }
              }
              else if (tmp_marked_str.contains("@type_std::string@")) {
                tmp_marked_str = tmp_marked_str.replace("@type_std::string@", "").replace("std::string ", "char *")
              }
              else if (tmp_marked_str.contains("@type_std::cout@")) {
                tmp_marked_str = tmp_marked_str.replace("@type_std::cout@", "").replace("std::cout << ", "pritnf(").replace(";", ");")
              }
            }
            // init
            if (tmp_marked_str.contains("@init")) {
              if (tmp_marked_str.contains("@init_int@")) {
                tmp_marked_str = tmp_marked_str.replace("@init_int@", "").replace(";", " = 0;")
              }
              else if (tmp_marked_str.contains("@init_float@")) {
                tmp_marked_str = tmp_marked_str.replace("@init_float@", "").replace(";", " = 0.0;")
              }
              else if (tmp_marked_str.contains("@init_bool@")) {
                tmp_marked_str = tmp_marked_str.replace("@init_bool@", "").replace(";", " = false;")
              }
            }
        }
        sender() ! WriteLabel(tmp_marked_str)

      // Write in file the final result line by line
      case WriteLabel(final_line) =>
        println(final_line)
        val writer = new FileWriter("new_text.txt", true)
        try {
          writer.write(final_line)
          writer.append('\n')
          writer.flush()
        } catch {
          case ex: IOException =>
            System.out.println(ex.getMessage)
        } finally if (writer != null) writer.close()
    }
  }

  //Store content from txt file into a String
  val source_file = Source.fromFile("text.txt")

  //Create the actor system and actors, and send a message to CountActor with a method
  val system = ActorSystem("parser")
  val actor0 = system.actorOf(Props[CountActor], name = "parser-reader")
  val actor1 = system.actorOf(Props[CountActor], name = "parser-labeler")
  val actor2 = system.actorOf(Props[CountActor], name = "parser-replacer")

  actor0 ! readFromFile(actor1, actor2, source_file)
  //system.terminate()
}
