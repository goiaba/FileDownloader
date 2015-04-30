package edu.luc.cs.spring2015.comp471.view

import java.net.URL

import edu.luc.cs.spring2015.comp471.model.DownloadManager
import edu.luc.cs.spring2015.comp471.model.DownloadState._
import jline.console.ConsoleReader

import scala.util.Try

object Console {

  val console = new ConsoleReader
  val downloadManager = new DownloadManager
  val downloadDir = scala.util.Properties.userDir
  val EOL = scala.util.Properties.lineSeparator
  val urlSample = "http://mirrors.xmission.com/eclipse/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-macosx-cocoa-x86_64.tar.gz"

  def main(args: Array[String]) = {
    console.setPrompt("download> ")
    printHelpMessage

    Iterator continually {
      console.readLine()
    } takeWhile {
      isValid
    } foreach {
      (s: String) => {
        if (!s.isEmpty)
          s.charAt(0) match {
            case 'c'  => cancel(s)
            case 'l'  => listAllDownloads(downloadManager.getDownloads)
            case 'd'  => downloadManager.start(urlSample, downloadDir + "/eclipse-jee-kepler-SR2-macosx-cocoa-x86_64.tar.gz")
            case 'p'  => downloadManager.purge()
            case 'H'  => printHelpMessage
            case _    => {
              if (Try(new URL(s)).isFailure) println("Malformed URL!")
              else {
                val fileName = s.substring(s.lastIndexOf('/'), s.length)
                downloadManager.start(s, downloadDir + fileName)
              }
            }
          }
      }
    }
    println()
  }

  def cancel(s: String) {
    val c = downloadManager.cancel(Integer.valueOf(s.substring(2, 3)))
    if (c.isFailure) println(c)
  }

  def printHelpMessage {
    println("Possible inputs:")
    println("\tl : List downloads")
    println("\tc <number> : Cancel download identified by <number> if it exists")
    println("\tp : Remove all downloads from the list except those which state is InProgress or NotStarted")
    println("\tH : Prints this help")
    println("\tq : Exit\n")
  }

  def listAllDownloads(list: List[(Int, Int, Option[Int], DownloadState)]): Unit = {
    val separator = "=" * console.getTerminal.getWidth
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
  def setProgress(index:Int, completed: Int, total: Option[Int], state: DownloadState): String = {
    val buffer = new StringBuffer()
    val indexLength = 3
    val stateLength = 10
    val percentageLength = 3
    if (total.isDefined) {
      val totalGet = total.get
      val totalLength = totalGet.toString.size
      val w = console.getTerminal.getWidth - (indexLength + stateLength + percentageLength + totalLength*2)
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
      buffer.append("%3d %-10s %s" format(index, state, "Unspecified length (Cannot print current status. Will print only state.)"))
    }
    buffer.toString()
  }

  def fillBar(completed: Int, char: String): StringBuffer = {
    val buffer = new StringBuffer()
    (1 to (completed)) foreach { _ => buffer.append(char) }
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

