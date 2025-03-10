
import burp.api.montoya.core.ToolType
import burp.api.montoya.http.handler.*

class RequestHandler :HttpHandler{

    override fun handleHttpRequestToBeSent(request: HttpRequestToBeSent): RequestToBeSentAction {
        if (request.toolSource().isFromTool(ToolType.SCANNER))  {
            Timer.touch()
        }
        return RequestToBeSentAction.continueWith(request)
    }

    override fun handleHttpResponseReceived(response : HttpResponseReceived): ResponseReceivedAction {
        return ResponseReceivedAction.continueWith(response)
    }

}
