package edu.luc.cs.spring2015.comp471.view

import edu.luc.cs.spring2015.comp471.model.DownloadManager
import jline.console.ConsoleReader
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
      (s: String) => s match {
        case "c"  => println("Hi from c")
        case "l"  => {
          downloadManager.getDownloads.foreach { tuple =>
            showProgress(tuple._2, tuple._3)
          }
        }
        case "d"  => downloadManager.start(urlSample, "/Users/sauloaguiar/Dropbox/LoyolaChicago/Classes/372-PL/homeworks/cs372s15p4/downloads/a.gz")
        case _    => print("input: " + s)

      }
    }
    println()
  }


  def isValid(s: String): Boolean = !(s.equals("q"))

  def showProgress(completed: Int, total: Int): Unit = {
    val progress = setProgress(completed, total)
    console.getCursorBuffer().clear()
    console.getCursorBuffer().write("\nStatus: \n")
    console.getCursorBuffer().write(progress)
    console.setCursorPosition(console.getTerminal.getWidth)
    console.redrawLine()
  }

  //100%[======================================>] 15,790      48.8K/s   in 0.3s
  def setProgress(completed: Int, total: Int): String = {
    val progress = (completed * 20) / total
    val buffer = new StringBuffer()
    val completeness = (completed.toDouble / total) * 100
    buffer.append(EOL)
    buffer.append(completeness.toInt.toString)
    buffer.append("%")
    buffer.append("[")
    buffer.append(fillProgress(progress))
    buffer.append(fillEmpty(20-progress))
    buffer.append("]")
    buffer.append(" " + total)
    buffer.append(EOL)
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

