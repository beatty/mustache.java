package com.sampullara.util.http;

import com.sampullara.util.CallbackFuture;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.eclipse.jetty.client.ContentExchange;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Future;

/**
 * Retrieve JSON data from a data source without blocking a thread.
 * <p/>
 * User: sam
 * Date: May 7, 2010
 * Time: 5:14:45 PM
 */
public class JSONHttpRequest extends HttpRequest<JsonNode> {
  private static MappingJsonFactory jf = new MappingJsonFactory();

  public JSONHttpRequest(String url) throws MalformedURLException {
    super(url);
  }

  @Override
  public Future<JsonNode> execute() throws IOException {
    final CallbackFuture<JsonNode> future = new CallbackFuture<JsonNode>();
    ContentExchange exchange = new ContentExchange() {
      protected void onResponseComplete() throws IOException {
        super.onResponseComplete();
        String responseContent = this.getResponseContent();
        try {
          JsonParser jp = jf.createJsonParser(responseContent);
          future.set(jp.readValueAsTree());
        } catch (Exception e) {
          future.error(e);
        }
      }
    };
    
    exchange.setRequestHeader("Accept", "application/json, text/javascript");
    exchange.setMethod("GET");
    exchange.setURL(url.toString());

    // start the exchange
    client.send(exchange);

    return future;
  }
}