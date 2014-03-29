package jk_5.nailed.web.webserver.http

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object HttpHeaderDateFormat {

  def get = dateFormatThreadLocal.get

  private final val dateFormatThreadLocal: ThreadLocal[HttpHeaderDateFormat] = new ThreadLocal[HttpHeaderDateFormat] {
    protected override def initialValue = new HttpHeaderDateFormat
  }
}

private[http] final class HttpHeaderDateFormatObsolete1 extends SimpleDateFormat("E, dd-MMM-yy HH:mm:ss z", Locale.ENGLISH) {
  this.setTimeZone(TimeZone.getTimeZone("GMT"))
}

private[http] final class HttpHeaderDateFormatObsolete2 extends SimpleDateFormat("E MMM d HH:mm:ss yyyy", Locale.ENGLISH) {
  this.setTimeZone(TimeZone.getTimeZone("GMT"))
}

final class HttpHeaderDateFormat extends SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH) {

  this.setTimeZone(TimeZone.getTimeZone("GMT"))

  override def parse(text: String, pos: ParsePosition): Date ={
    var date = super.parse(text, pos)
    if(date == null) date = format1.parse(text, pos)
    if(date == null) date = format2.parse(text, pos)
    date
  }

  private val format1 = new HttpHeaderDateFormatObsolete1
  private val format2 = new HttpHeaderDateFormatObsolete2
}
