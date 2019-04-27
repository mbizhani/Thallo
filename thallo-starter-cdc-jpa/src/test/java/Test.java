import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devocative.thallo.cdc.CdcAnnotationIntrospector;
import org.devocative.thallo.cdc.test.model.Book;
import org.devocative.thallo.cdc.test.model.EBookSize;
import org.devocative.thallo.cdc.test.model.Person;
import org.devocative.thallo.cdc.test.model.Price;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

public class Test {
	public static void main(String[] args) throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new CdcAnnotationIntrospector());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

		Person owner = new Person();
		owner.setId(1L);
		owner.setName("O1");

		Person author1 = new Person();
		author1.setId(1L);
		author1.setName("A1");

		Book book = new Book();
		book.setId(1L);
		book.setName("B1");
		book.setPrice(new Price(new BigDecimal("100000.1"), null));
		book.setSize(EBookSize.Normal);
		book.setOwner(owner);
		book.setAuthors(Arrays.asList(author1));

		book.setCreatedDate(new Date());
		book.setCreatedBy("guest");
		book.setVersion(1);

		owner.setMyBooks(Arrays.asList(book));

		System.out.println(mapper.writeValueAsString(book));
		System.out.println(mapper.writeValueAsString(owner));

		final BigDecimal b1 = new BigDecimal(1.1);
		final BigDecimal b2 = new BigDecimal(1.1);
		System.out.println(b1.equals(b2));
		System.out.println(b1.compareTo(b2));
	}
}
