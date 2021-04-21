package fr.univ.lille1.akka.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.collection.mutable

object WorkerOnlyWord {
  def props(word: String, masterActor: ActorRef): Props = Props(new WorkerOnlyWord(word, masterActor))
}

class WorkerOnlyWord(word: String, masterActor: ActorRef) extends Actor with ActorLogging {
  println("Worker only started")

  def receive = {
    case line: String => {
      val number: Int = line.split("[ ,!.]+").filter(_ == word).length
      masterActor ! number
    }
  }
}