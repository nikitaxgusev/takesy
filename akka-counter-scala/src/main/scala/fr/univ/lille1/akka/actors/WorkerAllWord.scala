package fr.univ.lille1.akka.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import java.io.FileWriter
import java.io.IOException
import scala.collection.mutable

object WorkerAllWord {
  def props(masterActor: ActorRef): Props = Props(new WorkerAllWord(masterActor))
}

class WorkerAllWord(masterActor: ActorRef) extends Actor with ActorLogging {
  println("Worker all started")

//  def find_substr(line: String, substr: String, replace_string: String): String = {
  def find_substr(line: String): String = {
//    val new_line
    if (line.contains("int")) {
      return line.replace("int", "float")
//      return line.replace("std::cout << ", "printf(")
    }
    if (line.contains("std::string ")) {
      return line.replace("std::string ", "char *")
      //      return line.replace("std::cout << ", "printf(")
    }
    if (line.contains("std::cout << ")) {
      return line.replace("std::cout << ", "pritnf(").replace(";", ");")
      //      return line.replace("std::cout << ", "printf(")
    }
//    if (line.contains("std::string") || line.contains("string"))
//    {
//        return line.replace("std::cout << ", "printf(")
//    }
//    println(new_line)
    return line
  }

  def init_variables(line: String): String = {
    if (line.contains("int")) {
      if (line.contains("=") == false) {
        return line.replace(";", " = 0;")
      }
    }
    if (line.contains("float")) {
      if (line.contains("=") == false) {
        return line.replace(";", " = 0.0;")
      }
    }
    if (line.contains("bool")) {
      if (line.contains("=") == false) {
        return line.replace(";", " = false;")
      }
    }
    return line
  }

  def receive = {
    case line: String => {
      val numbers: mutable.Map[Int, String] = mutable.Map.empty[Int, String]
      val writer = new FileWriter("temp.txt", true)
      if (line.contains("#include")
          || line.contains("//")
          || line.contains("int main() {")
          || line.contains("return 0;"))
      {
           println(line)
//           numbers(line) += 1

        try {
            writer.write(line)
            writer.append('\n')
            writer.flush()
          } catch {
            case ex: IOException =>
              System.out.println(ex.getMessage)
          } finally if (writer != null) writer.close()

      }
      else
      {
        val new_line = find_substr(init_variables(line))
        //        val _new_line = find_substr(line, "int", "float")
//        val new_line = find_substr(_new_line, "std::string ", "char *")
        println(new_line)

        try {
          writer.write(new_line)
          writer.append('\n')
          writer.flush()
        } catch {
          case ex: IOException =>
            System.out.println(ex.getMessage)
        } finally if (writer != null) writer.close()

//        val strings = new_line.split("[@]+")
//        println(strings.toArray(0))
//        numbers[strings[0].toInt] = strings[1]

//        new_line.split("[@]+").foreach(str_num => {
//          val word = rawWord.toLowerCase
//          numbers(str_num) += 1
//        })
      }
      masterActor ! numbers
    }
  }
}