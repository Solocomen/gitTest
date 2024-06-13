package epc.epcsalesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;	// added by Danny Chan on 2022-9-30
//import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
@EnableCaching		// added by Danny Chan on 2022-9-30
public class EpcsalesapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpcsalesapiApplication.class, args);
	}

}
