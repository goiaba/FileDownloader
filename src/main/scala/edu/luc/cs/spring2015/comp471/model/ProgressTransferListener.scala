package edu.luc.cs.spring2015.comp471.model

import java.util.concurrent.atomic.AtomicInteger

import com.ning.http.client.FluentCaseInsensitiveStringsMap
import com.ning.http.client.listener.TransferListener
import edu.luc.cs.spring2015.comp471.model.DownloadState.DownloadState

/**
 * Created by bruno on 4/29/15.
 */
class ProgressTransferListener extends TransferListener {

    private var totalBytes: Int = 0

    private val bytesRead = new AtomicInteger(0)

    private var state = DownloadState.NotStarted

    def getState = state

    def getTotalBytes = totalBytes

    def getBytesRead = bytesRead.get()

    def addBytesRead(current: Int) = bytesRead.addAndGet(current)

    def getPercentage = util.Try(bytesRead.get() / totalBytes).getOrElse(0)

    def setState(downloadState: DownloadState) = this.state = downloadState

    override def onRequestHeadersSent(headers: FluentCaseInsensitiveStringsMap): Unit = ()

    override def onResponseHeadersReceived(headers: FluentCaseInsensitiveStringsMap): Unit = {
        totalBytes = headers.getFirstValue("Content-Length").toInt
        state = DownloadState.InProgress
    }

    override def onBytesReceived(buffer: Array[Byte]): Unit = bytesRead.addAndGet(buffer.length)

    override def onBytesSent(amount: Long, current: Long, total: Long): Unit = ()

    override def onRequestResponseCompleted(): Unit = {
        state = DownloadState.Success
    }

    override def onThrowable(throwable: Throwable): Unit = {
        state = DownloadState.Failure
    }

}
