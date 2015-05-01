package edu.luc.cs.spring2015.comp471.model

import java.net.{MalformedURLException, URL}

import edu.luc.cs.spring2015.comp471.model.DownloadState.DownloadState

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Try}

/**
 * Created by bruno on 4/23/15.
 */
class DownloadManager() {

  private val downloads = ListBuffer[Download]()
  private val asyncDownloader = new AsyncDownloader

  def start(url: String, file: String): Unit = {
    if (Try(new URL(url)).isFailure || (new URL(url).getHost.isEmpty)) {
      throw new MalformedURLException
    }
    else {
      downloads += asyncDownloader.download(url, file)
    }
  }


  def cancel(index: Int): Try[Boolean] = {
    executeOverDownloadsList(index) {
      val download = downloads(index)
      val canceled = download.response.cancel(true)
      if (canceled) download.progress.setState(DownloadState.Canceled)
      Try(canceled)
    }
  }

  def purge() =
    (0 until downloads.size).foreach {
      index =>
        executeOverDownloadsList(index)(
          Try(
            downloads(index).progress.getState match {
            case DownloadState.InProgress | DownloadState.NotStarted =>
            case _ => downloads.remove(index)
          })
        )
    }

  def getDownloads: List[(Int, Int, Option[Int], DownloadState)] =
    (0 until downloads.size).map(getDownload(_).get).toList

  //(Index, bytesReceived, totalBytes)
  def getDownload(index: Int): Try[(Int, Int, Option[Int], DownloadState)] = {
    executeOverDownloadsList(index) {
      val download = downloads(index)
      Try(
        (index,
        download.progress.getBytesRead,
        download.progress.getTotalBytes,
        download.progress.getState)
      )
    }
  }

  def executeOverDownloadsList[T](index: Int)(command: => Try[T]) =
    if(index >= 0 && index < downloads.size) command
    else Failure(new IndexOutOfBoundsException("Invalid index passed to DownloadManager."))

}
