package eu.stamp.botsing.utility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigurationBean.class })
public class ConfigurationBeanTest {

	Logger log = LoggerFactory.getLogger(ConfigurationBeanTest.class);

	@Autowired
	ConfigurationBean configuration;

	@Test
	public void checkNullProxy() {

		Integer port = configuration.getProxyPort();
		log.info("proxyPort: " + port);
	}
}
