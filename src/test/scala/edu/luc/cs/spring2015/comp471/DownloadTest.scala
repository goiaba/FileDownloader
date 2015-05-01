package edu.luc.cs.spring2015.comp471

import java.net.MalformedURLException

import edu.luc.cs.spring2015.comp471.model.{DownloadState, DownloadManager}
import org.scalatest.FunSuite

/**
 * Created by sauloaguiar on 4/30/15.
 */
class DownloadTest extends FunSuite {

  def fixtureUrl(): String = {
    "http://mirrors.xmission.com/eclipse/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-macosx-cocoa-x86_64.tar.gz"
  }
  def fixturePath(): String = {
    scala.util.Properties.userDir + "file.gz"
  }

  def fixture(): DownloadManager = {
    new DownloadManager
  }

  test("assert that started download change its state"){
    val manager = fixture()
    manager.start(fixtureUrl(), fixturePath())
    Thread.sleep(3000)
    assert(manager.getDownload(0).get._4 == DownloadState.InProgress)
    assert(manager.getDownload(0).get._3.get == 260837979)
  }

  test("assert that good url with empty path don't download"){
    val manager = fixture()
    intercept[MalformedURLException] {
      manager.start("http://", fixturePath())
    }

  }

  test("assert that bad url is rejected"){
    val manager = fixture()
    intercept[MalformedURLException] {
      manager.start("",fixturePath())
    }
  }



}
