package edu.luc.cs.spring2015.comp471.model

import com.ning.http.client.{ListenableFuture, Response}

/**
 * Created by bruno on 4/25/15.
 */
class Download(val response: ListenableFuture[Response], val progress: ProgressTransferListener)