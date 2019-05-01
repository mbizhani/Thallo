package org.devocative.thallo.cdc.test;

import org.devocative.thallo.cdc.test.model.Person;
import org.devocative.thallo.cdc.test.model.VPerson;
import org.devocative.thallo.cdc.test.repo.PersonRepository;
import org.devocative.thallo.cdc.test.repo.VPersonRepository;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class TestThreadedCDC {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private VPersonRepository vPersonRepository;

	// ---------------

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true);

	// ------------------------------

	@Test
	public void testInThreads() throws Exception {
		final long MAX = 500;

		final ExecutorService executorService = Executors.newFixedThreadPool(50);
		executorService.invokeAll(LongStream
			.range(1, MAX).mapToObj(i -> (Callable<Void>) () -> {
				personRepository.saveAndFlush(new Person(i, String.format("P%05d", i)));
				try {
					Thread.sleep(i * 2);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				return null;
			}).collect(Collectors.toList())
		);

		/*System.out.println("######### vPersonRepository.count() = " + vPersonRepository.count());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}*/

		assertEquals(MAX - 1, vPersonRepository.count());
		final List<VPerson> name = vPersonRepository.findAll(Sort.by("name"));
		for (int i = 1; i < MAX; i++) {
			assertEquals(String.format("P%05d", i), name.get(i - 1).getName());
		}
	}
}
