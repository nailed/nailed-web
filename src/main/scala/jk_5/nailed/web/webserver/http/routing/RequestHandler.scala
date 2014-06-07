package jk_5.nailed.web.webserver.http.routing

/**
 * No description given
 *
 * @author jk-5
 */
abstract class RequestHandler {

  def handleGet(request: Request, response: Response) = {}
  def handlePost(request: Request, response: Response) = {}
  def handlePut(request: Request, response: Response) = {}
  def handlePatch(request: Request, response: Response) = {}
  def handleDelete(request: Request, response: Response) = {}
  def handleHead(request: Request, response: Response) = {}
  def handleConnect(request: Request, response: Response) = {}
  def handleOptions(request: Request, response: Response) = {}
  def handleTrace(request: Request, response: Response) = {}
}
