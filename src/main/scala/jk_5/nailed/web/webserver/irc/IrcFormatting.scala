package jk_5.nailed.web.webserver.irc

/**
 * No description given
 *
 * @author jk-5
 */
object IrcFormatting {

  final val NORMAL = "\u000f"
  final val BOLD = "\u0002"
  final val UNDERLINE = "\u001f"
  final val REVERSE = "\u0016"
  final val WHITE = "\u000300"
  final val BLACK = "\u000301"
  final val DARK_BLUE = "\u000302"
  final val DARK_GREEN = "\u000303"
  final val RED = "\u000304"
  final val BROWN = "\u000305"
  final val PURPLE = "\u000306"
  final val OLIVE = "\u000307"
  final val YELLOW = "\u000308"
  final val GREEN = "\u000309"
  final val TEAL = "\u000310"
  final val CYAN = "\u000311"
  final val BLUE = "\u000312"
  final val MAGENTA = "\u000313"
  final val DARK_GRAY = "\u000314"
  final val LIGHT_GRAY = "\u000315"

  def removeColors(line: String): String = {
    val length = line.length
    val builder = new StringBuilder
    var i = 0
    while(i < length){
      var ch = line.charAt(i)
      if(ch == '\u0003'){
        i += 1
        if(i < length){
          ch = line.charAt(i)
          if(Character.isDigit(ch)){
            i += 1
            if(i < length){
              ch = line.charAt(i)
              if(Character.isDigit(ch)) i += 1
            }
            if(i < length){
              ch = line.charAt(i)
              if(ch == ','){
                i += 1
                if(i < length){
                  ch = line.charAt(i)
                  if(Character.isDigit(ch)){
                    i += 1
                    if(i < length){
                      ch = line.charAt(i)
                      if(Character.isDigit(ch)) i += 1
                    }
                  }else i -= 1
                }else i -= 1
              }
            }
          }
        }
      }else if(ch == '\u000f') i += 1
      else{
        builder.append(ch)
        i += 1
      }
    }
    builder.toString()
  }

  def removeFormatting(line: String): String = {
    val length = line.length
    val builder = new StringBuilder
    for(i <- 0 until length){
      val ch = line.charAt(i)
      if(ch != '\u000f' && ch != '\u0002' && ch != '\u001f' && ch != '\u0016'){
        builder.append(ch)
      }
    }
    builder.toString()
  }

  def removeFormattingAndColors(line: String): String = this.removeFormatting(this.removeColors(line))
}
