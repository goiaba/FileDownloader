package edu.luc.cs.spring2015.comp471.view

import java.net.URL

import edu.luc.cs.spring2015.comp471.model.DownloadManager
import edu.luc.cs.spring2015.comp471.model.DownloadState.DownloadState
import jline.console.ConsoleReader
import scala.util.Try
import scala.util.matching.Regex

object Console {
  val console = new ConsoleReader
  val EOL = scala.util.Properties.lineSeparator
  val downloadManager = new DownloadManager
  val httpRegex = new Regex("/^(http?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/")
  val urlSample = "http://mirrors.xmission.com/eclipse/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-macosx-cocoa-x86_64.tar.gz"

  def main(args: Array[String]) = {
    console.setPrompt("download>")
    println("Possible inputs: l / c <number> / q")

    Iterator continually {
      console.readLine()
    } takeWhile {
      isValid((_:String))
    } foreach {
      (s: String) => {
        if (!s.isEmpty)
          s.charAt(0) match {
            case 'c'  => {
              downloadManager.cancel(Integer.valueOf(s.substring(2,3)))
            }
            case 'l'  => {
              println("Download list:\n")
              println("#ID State      Status")
              downloadManager.getDownloads.foreach { tuple =>
                showProgress(tuple._1, tuple._2, tuple._3, tuple._4)
              }
            }
            case 'd'  => downloadManager.start(urlSample, "downloads/a.gz")
            case 'p'  => downloadManager.purge()
            case _    => {
              val url = Try(new URL(s))
              if (url.isFailure) {
                println("Malformed URL!")
              } else {
                downloadManager.start( s,
                  "downloads/" + s.substring(s.lastIndexOf('/'), s.length))
              }
            }
          }
      }
    }
    println()
  }


  def isValid(s: String): Boolean = !(s.equals("q"))

  def showProgress(index: Int, completed: Int, total: Int, state: DownloadState): Unit = {
    val progress = setProgress(index, completed, total, state)
    println(progress)
  }

  //1: 100%[======================================>] 15,790      48.8K/s   in 0.3s
  def setProgress(index:Int, completed: Int, total: Int, state: DownloadState): String = {
    val w = console.getTerminal.getWidth - total.toString.length - 22
    val progress = (completed.toDouble * w) / total

    /*
    #ID State       Status
     0  InProgress  15% [=====> ]
     */
    val buffer = new StringBuffer()
    val completeness = (completed.toDouble / total) * 100
    buffer.append(" " + index+ "  " + state + " ")
    buffer.append(completeness.toInt.toString)
    buffer.append("%")
    buffer.append("[")
    buffer.append(fillProgress(progress.toInt))
    buffer.append(fillEmpty(w-progress.toInt))
    buffer.append("]")
    buffer.append(" " + total)
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

