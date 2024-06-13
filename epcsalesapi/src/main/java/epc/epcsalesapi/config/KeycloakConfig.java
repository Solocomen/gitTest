package epc.epcsalesapi.config;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${EPCAPI_KEYCLOAK_URL}")
    private String url;

    @Value("${EPCAPI_KEYCLOAK_DIC}")
    private String dic;

    @Value("${EPCAPI_KEYCLOAK_CS}")
    private String cs;

    // @Value("${EPCAPI_KEYCLOAK_UN}")
    // private String un;

    // @Value("${EPCAPI_KEYCLOAK_DWP}")
    // private String dwp;

    @Value("${EPCAPI_KEYCLOAK_REALM}")
    private String realm;

    @Value("${EPCAPI_KEYCLOAK_POOL_SIZE:10}")
	private int poolSize; // default = 10 conn


    @Bean
    public Keycloak keyclock() {
        // if request CLIENT_CREDENTIALS
		//  need client id & client secret only
		// if request P.....A.....S.....S.....W.....O.....R.....D
		//  need client id, username, p...w..d

        return KeycloakBuilder.builder()
            .serverUrl(url)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .realm(realm)
            .clientId(dic)
            .clientSecret(cs)
            .resteasyClient(
//                new ResteasyClientBuilder().connectionPoolSize(poolSize).build()
//                ClientBuilder.newBuilder().build()
                ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(poolSize).build()
            ).build();
    }
}
