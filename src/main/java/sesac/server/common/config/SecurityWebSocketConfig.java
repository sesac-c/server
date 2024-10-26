package sesac.server.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SecurityWebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/topic/chat.*").authenticated()
                .simpDestMatchers("/chat/**").authenticated()
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}