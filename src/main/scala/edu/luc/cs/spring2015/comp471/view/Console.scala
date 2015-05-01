package edu.luc.cs.spring2015.comp471.view

import java.net.{MalformedURLException, URL}

import edu.luc.cs.spring2015.comp471.model.DownloadManager
import edu.luc.cs.spring2015.comp471.model.DownloadState._
import jline.console.ConsoleReader

import scala.util.Try

object Console extends App {

  val CancelPattern = """^\s*([cC])\s+([0-9]+)\s*$""".r
  val ListPattern = """^\s*([lL])\s*$""".r
  val PurgePattern = """^\s*([pP])\s*$""".r
  val HelpPattern = """^\s*([hH])\s*$""".r
  val URLPattern = """^\s*(http.*)$""".r

  val console = new ConsoleReader
  val downloadManager = new DownloadManager
  val downloadDir = scala.util.Properties.userHome
  val EOL = scala.util.Properties.lineSeparator

  printHelpMessage
  console.setPrompt("download> ")
  Iterator continually {
    console.readLine()
  } takeWhile {
    isValid
  } foreach { (input: String) =>
    input match {
      case CancelPattern(command, index) => cancel(index.toInt)
      case ListPattern(command) => listAllDownloads(downloadManager.getDownloads)
      case PurgePattern(command) => downloadManager.purge()
      case HelpPattern(command) => printHelpMessage
      case URLPattern(url) =>
        if (Try(new URL(url)).isSuccess) {
          val fileName = url.substring(url.lastIndexOf('/'), url.length)
          try {
            downloadManager.start(url, downloadDir + fileName)
          } catch {
            case e: MalformedURLException => println("Malformed URL!")
          }
        } else
          println("Please inform a valid URL")
      case _ => {
        printHelpMessage
        println("Please inform a valid URL or one of the options.")
      }
    }
  }
  println()

  def cancel(index: Int) {
    val c = downloadManager.cancel(index)
    if (c.isFailure) println(c)
  }

  def printHelpMessage {
    println("\nPossible inputs:")
    println("\tl: List downloads")
    println("\tc <number>: Cancel download identified by <number> if it exists")
    println("\tp: Remove all downloads from the list except those which state is InProgress or NotStarted")
    println("\th: Prints this help")
    println("\tq: Exit\n")
  }

  def listAllDownloads(list: List[(Int, Int, Option[Int], DownloadState)]): Unit = {
    val separator = "-" * console.getTerminal.getWidth
    println("\nDownload list")
    println(separator)
    println("#ID State      Status")
    println(separator)
    list.foreach(el => showProgress(el._1, el._2, el._3, el._4))
    println()
  }

  def isValid(s: String): Boolean = Option(s).isDefined && !(s.equals("q"))

  def showProgress(index: Int, completed: Int, total: Option[Int], state: DownloadState): Unit = {
    val progress = setProgress(index, completed, total, state)
    println(progress)
  }

  //1: 100%[======================================>] 15,790      48.8K/s   in 0.3s
  def setProgress(index: Int, completed: Int, total: Option[Int], state: DownloadState): String = {
    val buffer = new StringBuffer()
    val indexLength = 4
    val stateLength = 11
    val percentageLength = 5
    if (total.isDefined) {
      val totalGet = total.get
      val totalLength = totalGet.toString.size + 4
      val w = console.getTerminal.getWidth - indexLength - stateLength - percentageLength - totalLength
      val completeness = (completed.toDouble / totalGet) * 100
      val progress = (completed.toDouble * w) / totalGet
      /*
      #ID State       Status
       0  InProgress  15% [=====> ]
       */
      buffer.append("%3d %-10s %3d%% " format(index, state, completeness.toInt))
      buffer.append("[")
      buffer.append(fillProgress(progress.toInt))
      buffer.append(fillEmpty(w - progress.toInt))
      buffer.append("]")
      buffer.append(" " + totalGet)
    } else {
      buffer.append("%3d %-10s Unspecified content length (Bytes downloaded: %d)" format(index, state, completed))
    }
    buffer.toString()
  }

  def fillBar(completed: Int, char: String): StringBuffer = {
    val buffer = new StringBuffer()
    (1 to (completed)) foreach { _ => buffer.append(char)}
    buffer
  }

  def fillProgress(completed: Int): String = {
    val buffer = fillBar(completed, "=")
    buffer.append(">")
    buffer.toString
  }

  def fillEmpty(amount: Int): String = {
    val buffer = fillBar(amount, " ")
    buffer.toString
  }
}

