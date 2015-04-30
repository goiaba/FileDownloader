package edu.luc.cs.spring2015.comp471.view

import edu.luc.cs.spring2015.comp471.model.{DownloadManager, AsyncDownloader}

/**
 * Created by bruno on 4/21/15.
 */
object downloaderApp {

  val longDownloadURL = "http://mirrors.xmission.com/eclipse/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-macosx-cocoa-x86_64.tar.gz"
  val localFileName = "/home/bruno/eclipse-jee-kepler-SR2-macosx-cocoa-x86_64.tar.gz"

  val dM = new DownloadManager

  dM.start(longDownloadURL, localFileName)

  Thread.sleep(5000)
  dM.getDownloads.foreach(d => println(d._1 + " - " + d._2 + " - " + d._3))
  Thread.sleep(5000)
  dM.getDownloads.foreach(d => println(d._1 + " - " + d._2 + " - " + d._3))
  Thread.sleep(5000)
  dM.getDownloads.foreach(d => println(d._1 + " - " + d._2 + " - " + d._3))
//  Thread.sleep(10000)
//  dM.suspend(0)
//  Thread.sleep(10000)
//  dM.resume(0)
//  Thread.sleep(60000)
//  println("cancelling...")
//  dM.cancel(0)
}

