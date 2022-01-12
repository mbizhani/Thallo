package org.devocative.thallo.cdc.test;

import org.devocative.thallo.cdc.test.model.*;
import org.devocative.thallo.cdc.test.repo.BookRepository;
import org.devocative.thallo.cdc.test.repo.PersonRepository;
import org.devocative.thallo.cdc.test.repo.VBookRepository;
import org.devocative.thallo.cdc.test.repo.VPersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
public class TestCDC {


	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private VBookRepository vBookRepository;

	@Autowired
	private VPersonRepository vPersonRepository;

	// ---------------

	@RegisterExtension
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true);

	// ------------------------------

	@Transactional
	@Test
	public void test() {
		Person owner = new Person(1L, "01");
		personRepository.saveAndFlush(owner);

		Person author1 = new Person(2L, "A1");
		personRepository.saveAndFlush(author1);

		Book book1 = new Book();
		book1.setId(1L);
		book1.setName("B1");
		book1.setTitle(new LangString("Book1"));
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

		assertEquals(2, vBookRepository.count());
		{
			final VBook vBook1 = vBookRepository.findById(1L).orElseThrow(RuntimeException::new);
			assertEquals("t - B1", vBook1.getName()); // CdcHandler
			assertEquals("Book1", vBook1.getTitle().getEn());
			assertEquals(0, new BigDecimal("100000.1").compareTo(vBook1.getPrice().getAmount()));
			assertEquals("ASD", vBook1.getPrice().getCurrency());
			assertEquals(0, vBook1.getVersion().intValue());
		}
		{
			final VBook vBook2 = vBookRepository.findById(2L).orElseThrow(RuntimeException::new);
			assertEquals("B2", vBook2.getName());
			assertEquals(1, vBook2.getVersion().intValue());
		}

		assertEquals(2, vPersonRepository.count());
		{
			final VPerson vPerson1 = vPersonRepository.findById(1L).orElseThrow(RuntimeException::new);
			assertEquals("01", vPerson1.getName());
		}
		{
			final VPerson vPerson2 = vPersonRepository.findById(2L).orElseThrow(RuntimeException::new);
			assertEquals("A1", vPerson2.getName());
		}

		System.out.println("### CdcTestApp.noOfCreated = " + CdcTestApp.noOfCreated);
		System.out.println("### CdcTestApp.noOfUpdated = " + CdcTestApp.noOfUpdated);
		System.out.println("### CdcTestApp.noOfDeleted = " + CdcTestApp.noOfDeleted);

		assertTrue(CdcTestApp.noOfCreated > 0);
		assertEquals(2, CdcTestApp.noOfUpdated);
		assertEquals(0, CdcTestApp.noOfDeleted);
	}

}
