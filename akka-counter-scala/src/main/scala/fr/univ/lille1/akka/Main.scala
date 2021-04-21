package fr.univ.lille1.akka

import akka.actor.{ActorRef, ActorSystem, Props}
import fr.univ.lille1.akka.actors._
import scala.io.StdIn

object Main extends App {

  println("[INFO] System creation : akka-counter-scala")
  val system: ActorSystem = ActorSystem("akka-counter-scala")

  var textFiles = Map(1 -> "text.txt", 2 -> "small-text.txt")
  var choiceTextFile: Int = 1
  var typeCounter: Int = 1
  var occurenceToFind: String = null
  var numberOfWorkers: Int = 1

  println("[MENU] Welcome to the menu")
//  try {
//    println("Which text do you want to analyze? (default 1)")
//    for ((key,value) <- textFiles) printf("\t%s) %s\n", key, value)
//    this.choiceTextFile = StdIn.readInt()
//    if(this.choiceTextFile < 1 || this.choiceTextFile > textFiles.size)
//      throw new NumberFormatException
//  } catch {
//    case e: NumberFormatException => {
//      println("[ERROR] Incorrect value, we took the default choice")
//      this.choiceTextFile = 1
//    };
//  }
  try {
    println("How many workers do you want (default 1) ?")
    this.numberOfWorkers = StdIn.readInt()
  } catch {
    case e: NumberFormatException => {
      println("[ERROR] Incorrect value, we took the default choice")
      this.numberOfWorkers = 1
    };
  }

  println("[INFO] Creation & configuration of the master")
  val master: ActorRef = system.actorOf(Props(new Master(this.numberOfWorkers, occurenceToFind)))

  master ! textFiles(choiceTextFile)
}
