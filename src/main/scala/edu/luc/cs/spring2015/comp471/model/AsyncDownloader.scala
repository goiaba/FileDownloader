package edu.luc.cs.spring2015.comp471.model

import java.io.{File, FileOutputStream}

import com.ning.http.client.AsyncHttpClientConfig.Builder
import com.ning.http.client.listener.{TransferCompletionHandler, TransferListener}
import com.ning.http.client.resumable.ResumableIOExceptionFilter
import com.ning.http.client.{AsyncHttpClient, FluentCaseInsensitiveStringsMap}


class AsyncDownloader {

  private val client = new AsyncHttpClient(new Builder()
    .addIOExceptionFilter(new ResumableIOExceptionFilter).build())

  private def fileSaver(name: String) = new TransferListener {
    val file = new File(name)
    var stream: Option[FileOutputStream] = None

    override def onRequestHeadersSent(headers: FluentCaseInsensitiveStringsMap): Unit = ()

    override def onResponseHeadersReceived(headers: FluentCaseInsensitiveStringsMap): Unit =
      stream = Some(new FileOutputStream(file))

    override def onBytesReceived(buffer: Array[Byte]): Unit = stream.get.write(buffer)

    override def onBytesSent(amount: Long, current: Long, total: Long): Unit = ()

    override def onRequestResponseCompleted(): Unit = {
      stream.get.close()
    }

    override def onThrowable(throwable: Throwable): Unit = {
      stream.get.close()
    }

  }

  def download(url: String, local: String): Download = {
    val t = new TransferCompletionHandler
    t.addTransferListener(fileSaver(local))
    val tR = new ProgressTransferListener
    t.addTransferListener(tR)
    val fR = client.prepareGet(url).execute(t)
    new Download(fR, tR)
  }

}
