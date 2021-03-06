//package org.springframework.boot.actuate.endpoint;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
//
//@ConfigurationProperties(prefix = "endpoints.websocket", ignoreUnknownFields = true)
//public class WebSocketEndpoint extends AbstractEndpoint<WebSocketMessageBrokerStats> {
//
//    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
//
//    public WebSocketEndpoint(WebSocketMessageBrokerStats webSocketMessageBrokerStats) {
//        super("websocketstats");
//        this.webSocketMessageBrokerStats = webSocketMessageBrokerStats;
//    }
//
//    @Override
//    public WebSocketMessageBrokerStats invoke() {
//        return webSocketMessageBrokerStats;
//    }
//
//
//
//}
