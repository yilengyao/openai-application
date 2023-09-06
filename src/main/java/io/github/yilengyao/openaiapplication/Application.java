package io.github.yilengyao.openaiapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
  scanBasePackageClasses = {
    io.github.yilengyao.openai.configuration.ApplicationSpecificSpringComponentScanMarker.class,
    io.github.yilengyao.openaiapplication.graphql.ApplicationSpecificSpringComponentScanMarker.class
  }
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
