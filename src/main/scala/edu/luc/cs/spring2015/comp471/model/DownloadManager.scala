package edu.luc.cs.spring2015.comp471;
package model;

import scala.collection.mutable.ListBuffer

/**
 * Created by bruno on 4/23/15.
 */
class DownloadManager() {

  private val downloads = ListBuffer[Download]()
  private val asyncDownloader = new AsyncDownloader

  def start(url: String, file: String): Unit =
    downloads += asyncDownloader.download(url, file)

  def cancel(index: Int): Boolean =
    executeOverDownloadsList(index)(downloads.remove(index).response.cancel(true))

  //(Index, bytesReceived, totalBytes)
  def getDownloads: List[(Int, Int, Int)] = {
    var index = 0
    downloads.map {
      index +=1; download => (index, download.progress.getBytesRead, download.progress.getTotalBytes)
    }.toList
  }

  def getDownload(index: Int) = downloads(index)

  def executeOverDownloadsList[T](index: Int)(command: => T) =
    if(index >= 0 && index < downloads.size) command
    else throw new IndexOutOfBoundsException("Invalid index passed to DownloadManager.")

}
