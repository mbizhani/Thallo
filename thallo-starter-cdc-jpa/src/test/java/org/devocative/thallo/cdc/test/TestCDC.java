package org.devocative.thallo.cdc.test;

import org.devocative.thallo.cdc.test.model.*;
import org.devocative.thallo.cdc.test.repo.BookRepository;
import org.devocative.thallo.cdc.test.repo.PersonRepository;
import org.devocative.thallo.cdc.test.repo.VBookRepository;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

@EnableKafka
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class TestCDC {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private VBookRepository vBookRepository;

	// ---------------

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true);

	// ------------------------------

	@Transactional
	@Test
	public void test() {
		Person owner = new Person();
		owner.setId(1L);
		owner.setName("O1");
		personRepository.saveAndFlush(owner);

		Person author1 = new Person();
		author1.setId(2L);
		author1.setName("A1");
		personRepository.saveAndFlush(author1);

		Book book1 = new Book();
		book1.setId(1L);
		book1.setName("B1");
		book1.setPrice(new Price(new BigDecimal("100000.1"), "ASD"));
		book1.setSize(EBookSize.Normal);
		book1.setOwner(owner);
		book1.setAuthors(Arrays.asList(author1));
		book1.setCreatedDate(new Date());
		book1.setCreatedBy("guest");
		bookRepository.saveAndFlush(book1);

		Book book2 = new Book();
		book2.setId(2L);
		//book2.setName("B2");
		bookRepository.saveAndFlush(book2);

		book2.setName("B2");
		book2.setPrice(new Price(new BigDecimal("100"), "ASD"));
		bookRepository.saveAndFlush(book2);

		owner.setMyBooks(Arrays.asList(book1, book2));
		personRepository.saveAndFlush(owner);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Assert.assertEquals(2, vBookRepository.count());

		{
			final VBook vBook1 = vBookRepository.findById(1L).orElseThrow(RuntimeException::new);
			Assert.assertEquals("t - B1", vBook1.getName()); // CdcHandler
			Assert.assertEquals(0, new BigDecimal("100000.1").compareTo(vBook1.getPrice().getAmount()));
			Assert.assertEquals("ASD", vBook1.getPrice().getCurrency());
			Assert.assertEquals(0, vBook1.getVersion().intValue());
		}

		{
			final VBook vBook2 = vBookRepository.findById(2L).orElseThrow(RuntimeException::new);
			Assert.assertEquals("B2", vBook2.getName());
			Assert.assertEquals(1, vBook2.getVersion().intValue());
		}
	}

}
