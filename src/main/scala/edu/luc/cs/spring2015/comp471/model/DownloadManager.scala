package edu.luc.cs.spring2015.comp471.model

import edu.luc.cs.spring2015.comp471.model.DownloadState.DownloadState

import scala.collection.mutable.ListBuffer

/**
 * Created by bruno on 4/23/15.
 */
class DownloadManager() {

  private val downloads = ListBuffer[Download]()
  private val asyncDownloader = new AsyncDownloader

  def start(url: String, file: String): Unit =
    downloads += asyncDownloader.download(url, file)

  def cancel(index: Int): Boolean = {
    executeOverDownloadsList(index) {
      val download = downloads(index)
      val canceled = download.response.cancel(true)
      if (canceled) download.progress.setState(DownloadState.Canceled)
      canceled
    }
  }

  def purge() =
    (0 until downloads.size).foreach {
      index => downloads(index).progress.getState match {
        case DownloadState.InProgress | DownloadState.NotStarted =>
        case _ => downloads.remove(index)
      }
    }

  def getDownloads: List[(Int, Int, Int, DownloadState)] =
    (0 until downloads.size).map(getDownload(_)).toList

  //(Index, bytesReceived, totalBytes)
  def getDownload(index: Int): (Int, Int, Int, DownloadState) = {
    executeOverDownloadsList(index) {
      val download = downloads(index)
      (index,
        download.progress.getBytesRead,
        download.progress.getTotalBytes,
        download.progress.getState)
    }
  }

  def executeOverDownloadsList[T](index: Int)(command: => T) =
    if(index >= 0 && index < downloads.size) command
    else throw new IndexOutOfBoundsException("Invalid index passed to DownloadManager.")

}
