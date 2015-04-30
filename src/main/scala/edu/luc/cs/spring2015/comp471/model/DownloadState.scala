package edu.luc.cs.spring2015.comp471.model

/**
 * Created by bruno on 4/29/15.
 */
object DownloadState extends Enumeration {
  type DownloadState = Value
  val NotStarted, InProgress, Success, Failure, Canceled = Value
}
